/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.core.backup.EncryptedBackupManager
import com.supernovea.spendsense.core.backup.ImportMode
import com.supernovea.spendsense.core.backup.PasswordStrength
import com.supernovea.spendsense.core.security.AppLockManager
import com.supernovea.spendsense.core.security.BiometricAuthHelper
import com.supernovea.spendsense.core.security.LockoutManager
import com.supernovea.spendsense.core.security.PinManager
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.DeleteAllDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrivacySecurityUiState(
    val appLockEnabled: Boolean = false,
    val biometricAuthEnabled: Boolean = true,
    val canAuthenticateBiometrics: Boolean = false,
    val isPinSet: Boolean = false,
    val appLockTimeoutSeconds: Int = 0,
    val secureScreenEnabled: Boolean = true,
    val notificationPrivacyEnabled: Boolean = true,
    val isProcessing: Boolean = false,
    val showSetPinDialog: Boolean = false,
    val showChangePinDialog: Boolean = false,
    val showVerifyPinToDisableDialog: Boolean = false,
    val showTimeoutDialog: Boolean = false,
    val showExportPasswordDialog: Boolean = false,
    val showImportPasswordDialog: Boolean = false,
    val pendingImportUri: Uri? = null,
    val showDeleteConfirmDialog: Boolean = false,
    val showDeleteFinalConfirmDialog: Boolean = false,
)

sealed interface PrivacySecurityEffect {
    data class ShowMessage(val message: String) : PrivacySecurityEffect
    data class PinVerificationFailed(val reason: String) : PrivacySecurityEffect
    data object RequestExportDocument : PrivacySecurityEffect
}

@HiltViewModel
class PrivacySecurityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val pinManager: PinManager,
    private val lockoutManager: LockoutManager,
    private val appLockManager: AppLockManager,
    private val encryptedBackupManager: EncryptedBackupManager,
    private val deleteAllDataUseCase: DeleteAllDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacySecurityUiState())
    val uiState: StateFlow<PrivacySecurityUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<PrivacySecurityEffect>()
    val effects: SharedFlow<PrivacySecurityEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            val canBio = BiometricAuthHelper.isBiometricAvailable(context)
            settingsRepository.observeSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        appLockEnabled = settings.appLockEnabled,
                        biometricAuthEnabled = settings.biometricAuthEnabled,
                        canAuthenticateBiometrics = canBio,
                        isPinSet = pinManager.isPinSet(),
                        appLockTimeoutSeconds = settings.appLockTimeoutSeconds,
                        secureScreenEnabled = settings.secureScreenEnabled,
                        notificationPrivacyEnabled = settings.notificationPrivacyEnabled,
                    )
                }
            }
        }
    }

    fun onToggleAppLock(enabled: Boolean) {
        if (enabled) {
            if (!pinManager.isPinSet()) {
                _uiState.update { it.copy(showSetPinDialog = true) }
            } else {
                viewModelScope.launch {
                    settingsRepository.setAppLockEnabled(true)
                }
            }
        } else {
            if (pinManager.isPinSet()) {
                _uiState.update { it.copy(showVerifyPinToDisableDialog = true) }
            } else {
                viewModelScope.launch {
                    settingsRepository.setAppLockEnabled(false)
                }
            }
        }
    }

    fun onToggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBiometricAuthEnabled(enabled)
        }
    }

    fun onToggleSecureScreen(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSecureScreenEnabled(enabled)
        }
    }

    fun onToggleNotificationPrivacy(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationPrivacyEnabled(enabled)
        }
    }

    fun onSetTimeout(seconds: Int) {
        viewModelScope.launch {
            settingsRepository.setAppLockTimeoutSeconds(seconds)
            _uiState.update { it.copy(showTimeoutDialog = false) }
        }
    }

    fun onSaveNewPin(pin: String) {
        viewModelScope.launch {
            pinManager.setPin(pin)
            settingsRepository.setAppLockEnabled(true)
            _uiState.update {
                it.copy(
                    isPinSet = true,
                    appLockEnabled = true,
                    showSetPinDialog = false,
                )
            }
            _effects.emit(PrivacySecurityEffect.ShowMessage("PIN set successfully"))
        }
    }

    fun onChangePin(currentPin: String, newPin: String): Boolean {
        if (!pinManager.verifyPin(currentPin)) {
            viewModelScope.launch {
                _effects.emit(PrivacySecurityEffect.PinVerificationFailed("Current PIN is incorrect"))
            }
            return false
        }

        pinManager.setPin(newPin)
        _uiState.update { it.copy(showChangePinDialog = false) }
        viewModelScope.launch {
            _effects.emit(PrivacySecurityEffect.ShowMessage("Security PIN updated"))
        }
        return true
    }

    fun onVerifyPinToDisable(pin: String): Boolean {
        if (!pinManager.verifyPin(pin)) {
            viewModelScope.launch {
                _effects.emit(PrivacySecurityEffect.PinVerificationFailed("Incorrect PIN"))
            }
            return false
        }

        viewModelScope.launch {
            settingsRepository.setAppLockEnabled(false)
            _uiState.update { it.copy(showVerifyPinToDisableDialog = false, appLockEnabled = false) }
            _effects.emit(PrivacySecurityEffect.ShowMessage("App Lock disabled"))
        }
        return true
    }

    fun evaluatePasswordStrength(password: String): PasswordStrength {
        return encryptedBackupManager.evaluatePasswordStrength(password)
    }

    fun onExportBackup(uri: Uri, password: String) {
        _uiState.update { it.copy(isProcessing = true, showExportPasswordDialog = false) }
        viewModelScope.launch {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    encryptedBackupManager.exportEncryptedBackup(password, os)
                }
                settingsRepository.setLastBackupEpochMillis(System.currentTimeMillis())
                _effects.emit(PrivacySecurityEffect.ShowMessage("Encrypted backup exported successfully"))
            } catch (e: Exception) {
                _effects.emit(PrivacySecurityEffect.ShowMessage("Export failed: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isProcessing = false) }
            }
        }
    }

    fun onImportBackup(uri: Uri, password: String, mode: ImportMode) {
        _uiState.update { it.copy(isProcessing = true, showImportPasswordDialog = false, pendingImportUri = null) }
        viewModelScope.launch {
            try {
                val result = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    encryptedBackupManager.importEncryptedBackup(inputStream, password, mode)
                }
                if (result != null) {
                    _effects.emit(PrivacySecurityEffect.ShowMessage("Restored ${result.expensesRestoredCount} expenses"))
                } else {
                    _effects.emit(PrivacySecurityEffect.ShowMessage("Failed to restore backup"))
                }
            } catch (e: Exception) {
                _effects.emit(PrivacySecurityEffect.ShowMessage("Restore failed: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isProcessing = false) }
            }
        }
    }

    fun onDeleteAllData() {
        _uiState.update {
            it.copy(
                isProcessing = true,
                showDeleteConfirmDialog = false,
                showDeleteFinalConfirmDialog = false
            )
        }
        viewModelScope.launch {
            try {
                deleteAllDataUseCase()
                _effects.emit(PrivacySecurityEffect.ShowMessage("All data has been erased"))
            } catch (e: Exception) {
                _effects.emit(PrivacySecurityEffect.ShowMessage("Failed to erase data: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isProcessing = false) }
            }
        }
    }

    // Dialog state handlers
    fun openSetPinDialog() = _uiState.update { it.copy(showSetPinDialog = true) }
    fun dismissSetPinDialog() = _uiState.update { it.copy(showSetPinDialog = false) }

    fun openChangePinDialog() = _uiState.update { it.copy(showChangePinDialog = true) }
    fun dismissChangePinDialog() = _uiState.update { it.copy(showChangePinDialog = false) }

    fun dismissVerifyPinToDisableDialog() = _uiState.update { it.copy(showVerifyPinToDisableDialog = false) }

    fun openTimeoutDialog() = _uiState.update { it.copy(showTimeoutDialog = true) }
    fun dismissTimeoutDialog() = _uiState.update { it.copy(showTimeoutDialog = false) }

    fun openExportPasswordDialog() = _uiState.update { it.copy(showExportPasswordDialog = true) }
    fun dismissExportPasswordDialog() = _uiState.update { it.copy(showExportPasswordDialog = false) }

    fun onImportFileSelected(uri: Uri) = _uiState.update {
        it.copy(showImportPasswordDialog = true, pendingImportUri = uri)
    }
    fun dismissImportPasswordDialog() = _uiState.update {
        it.copy(showImportPasswordDialog = false, pendingImportUri = null)
    }

    fun openDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    fun dismissDeleteConfirmDialog() = _uiState.update { it.copy(showDeleteConfirmDialog = false) }

    fun proceedToFinalDeleteConfirm() = _uiState.update {
        it.copy(showDeleteConfirmDialog = false, showDeleteFinalConfirmDialog = true)
    }
    fun dismissDeleteFinalConfirmDialog() = _uiState.update { it.copy(showDeleteFinalConfirmDialog = false) }
}
