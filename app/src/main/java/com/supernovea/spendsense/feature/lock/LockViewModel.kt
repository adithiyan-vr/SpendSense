/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.core.security.AppLockManager
import com.supernovea.spendsense.core.security.BiometricAuthHelper
import com.supernovea.spendsense.core.security.LockoutManager
import com.supernovea.spendsense.core.security.PinManager
import com.supernovea.spendsense.domain.repository.SettingsRepository
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LockUiState(
    val enteredPin: String = "",
    val pinLength: Int = 4,
    val isLockedOut: Boolean = false,
    val lockoutRemainingSeconds: Long = 0L,
    val errorMessage: String? = null,
    val isBiometricEnabled: Boolean = false,
    val canAuthenticateBiometric: Boolean = false,
    val showForgotPinDialog: Boolean = false,
)

sealed interface LockEffect {
    data object TriggerBiometric : LockEffect
    data object ShakePin : LockEffect
}

@HiltViewModel
class LockViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appLockManager: AppLockManager,
    private val pinManager: PinManager,
    private val lockoutManager: LockoutManager,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LockUiState())
    val uiState: StateFlow<LockUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<LockEffect>()
    val effects: SharedFlow<LockEffect> = _effects.asSharedFlow()

    private var countdownJob: Job? = null

    init {
        checkSecurityState()
    }

    private fun checkSecurityState() {
        val pinLength = pinManager.getPinLength()
        val canBio = BiometricAuthHelper.isBiometricAvailable(context)

        viewModelScope.launch {
            val settings = settingsRepository.observeSettings().first()
            val bioEnabled = settings.biometricAuthEnabled && canBio

            val isLockedOut = lockoutManager.isLockedOut()
            val remaining = lockoutManager.getRemainingLockoutSeconds()

            _uiState.update {
                it.copy(
                    pinLength = pinLength,
                    isBiometricEnabled = bioEnabled,
                    canAuthenticateBiometric = canBio,
                    isLockedOut = isLockedOut,
                    lockoutRemainingSeconds = remaining,
                )
            }

            if (isLockedOut && remaining > 0) {
                startLockoutCountdown()
            } else if (bioEnabled) {
                // Auto-trigger biometric on initial appearance (FR-136)
                _effects.emit(LockEffect.TriggerBiometric)
            }
        }
    }

    fun onDigitEntered(digit: Char) {
        if (_uiState.value.isLockedOut) return

        val current = _uiState.value.enteredPin
        val targetLen = _uiState.value.pinLength
        if (current.length >= targetLen) return

        val updated = current + digit
        _uiState.update { it.copy(enteredPin = updated, errorMessage = null) }

        if (updated.length == targetLen) {
            verifyPin(updated)
        }
    }

    fun onDeleteDigit() {
        if (_uiState.value.isLockedOut) return
        val current = _uiState.value.enteredPin
        if (current.isNotEmpty()) {
            _uiState.update {
                it.copy(enteredPin = current.dropLast(1), errorMessage = null)
            }
        }
    }

    private fun verifyPin(pin: String) {
        viewModelScope.launch {
            val isValid = pinManager.verifyPin(pin)
            if (isValid) {
                lockoutManager.resetFailedAttempts()
                _uiState.update { it.copy(enteredPin = "", errorMessage = null) }
                appLockManager.unlock()
            } else {
                val lockoutSeconds = lockoutManager.recordFailedAttempt()
                _effects.emit(LockEffect.ShakePin)
                _uiState.update {
                    it.copy(
                        enteredPin = "",
                        errorMessage = if (lockoutSeconds > 0) null else "Incorrect PIN",
                        isLockedOut = lockoutSeconds > 0,
                        lockoutRemainingSeconds = lockoutSeconds,
                    )
                }
                if (lockoutSeconds > 0) {
                    startLockoutCountdown()
                }
            }
        }
    }

    fun onBiometricSuccess() {
        viewModelScope.launch {
            lockoutManager.resetFailedAttempts()
            _uiState.update { it.copy(enteredPin = "", errorMessage = null) }
            appLockManager.unlock()
        }
    }

    fun onBiometricClick() {
        if (_uiState.value.isLockedOut) return
        viewModelScope.launch {
            _effects.emit(LockEffect.TriggerBiometric)
        }
    }

    fun onForgotPinClick() {
        _uiState.update { it.copy(showForgotPinDialog = true) }
    }

    fun onDismissForgotPinDialog() {
        _uiState.update { it.copy(showForgotPinDialog = false) }
    }

    private fun startLockoutCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                val remaining = lockoutManager.getRemainingLockoutSeconds()
                if (remaining <= 0) {
                    _uiState.update {
                        it.copy(isLockedOut = false, lockoutRemainingSeconds = 0, errorMessage = null)
                    }
                    break
                }
                _uiState.update {
                    it.copy(isLockedOut = true, lockoutRemainingSeconds = remaining)
                }
                delay(1000)
            }
        }
    }
}
