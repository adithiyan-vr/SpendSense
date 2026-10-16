/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.backup.ImportMode
import com.supernovea.spendsense.core.backup.PasswordStrength
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrivacySecurityViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var pendingExportPassword by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null && pendingExportPassword != null) {
            viewModel.onExportBackup(uri, pendingExportPassword!!)
            pendingExportPassword = null
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.onImportFileSelected(uri)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is PrivacySecurityEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is PrivacySecurityEffect.PinVerificationFailed -> {
                    snackbarHostState.showSnackbar(effect.reason)
                }
                is PrivacySecurityEffect.RequestExportDocument -> {
                    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    exportLauncher.launch("spendsense_backup_$timestamp.ssbackup")
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.privacy_security_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Group: App Lock & Authentication
            SecuritySectionHeader(title = stringResource(id = R.string.privacy_security_section_app_lock))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column {
                    SecuritySwitchRow(
                        icon = Icons.Default.Lock,
                        title = stringResource(id = R.string.privacy_security_app_lock_enable),
                        subtitle = stringResource(id = R.string.privacy_security_app_lock_enable_desc),
                        checked = state.appLockEnabled,
                        onCheckedChange = { viewModel.onToggleAppLock(it) },
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SecuritySwitchRow(
                        icon = Icons.Default.Fingerprint,
                        title = stringResource(id = R.string.privacy_security_biometric_enable),
                        subtitle = if (state.canAuthenticateBiometrics) {
                            stringResource(id = R.string.privacy_security_biometric_enable_desc)
                        } else {
                            stringResource(id = R.string.privacy_security_biometric_unavailable)
                        },
                        checked = state.biometricAuthEnabled && state.canAuthenticateBiometrics,
                        enabled = state.canAuthenticateBiometrics && state.appLockEnabled,
                        onCheckedChange = { viewModel.onToggleBiometric(it) },
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SecurityActionRow(
                        icon = Icons.Default.Pin,
                        title = if (state.isPinSet) {
                            stringResource(id = R.string.privacy_security_change_pin)
                        } else {
                            stringResource(id = R.string.privacy_security_set_pin)
                        },
                        subtitle = if (state.isPinSet) {
                            stringResource(id = R.string.privacy_security_change_pin_desc)
                        } else {
                            stringResource(id = R.string.privacy_security_set_pin_desc)
                        },
                        onClick = {
                            if (state.isPinSet) viewModel.openChangePinDialog()
                            else viewModel.openSetPinDialog()
                        },
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    val timeoutLabel = when (state.appLockTimeoutSeconds) {
                        0 -> stringResource(id = R.string.privacy_security_timeout_immediately)
                        30 -> stringResource(id = R.string.privacy_security_timeout_30s)
                        60 -> stringResource(id = R.string.privacy_security_timeout_1m)
                        else -> stringResource(id = R.string.privacy_security_timeout_5m)
                    }

                    SecurityActionRow(
                        icon = Icons.Default.Timer,
                        title = stringResource(id = R.string.privacy_security_timeout),
                        subtitle = timeoutLabel,
                        enabled = state.appLockEnabled,
                        onClick = { viewModel.openTimeoutDialog() },
                    )
                }
            }

            // Group: Screen & Notification Privacy
            SecuritySectionHeader(title = stringResource(id = R.string.privacy_security_section_screen_notif))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column {
                    SecuritySwitchRow(
                        icon = Icons.Default.Shield,
                        title = stringResource(id = R.string.privacy_security_flag_secure),
                        subtitle = stringResource(id = R.string.privacy_security_flag_secure_desc),
                        checked = state.secureScreenEnabled,
                        onCheckedChange = { viewModel.onToggleSecureScreen(it) },
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SecuritySwitchRow(
                        icon = Icons.Default.Notifications,
                        title = stringResource(id = R.string.privacy_security_notif_privacy),
                        subtitle = stringResource(id = R.string.privacy_security_notif_privacy_desc),
                        checked = state.notificationPrivacyEnabled,
                        onCheckedChange = { viewModel.onToggleNotificationPrivacy(it) },
                    )
                }
            }

            // Group: Encrypted Backup (.ssbackup)
            SecuritySectionHeader(title = stringResource(id = R.string.privacy_security_section_encrypted_backup))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.privacy_security_encrypted_backup_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Button(
                            onClick = { viewModel.openExportPasswordDialog() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(stringResource(id = R.string.privacy_security_export_backup))
                        }

                        OutlinedButton(
                            onClick = { importLauncher.launch(arrayOf("application/octet-stream", "*/*")) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(stringResource(id = R.string.privacy_security_import_backup))
                        }
                    }
                }
            }

            // Group: Honest Security Limits Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp),
                    )
                    Column {
                        Text(
                            text = stringResource(id = R.string.privacy_security_section_honest_limits),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(id = R.string.privacy_security_honest_limits_card),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }

            // Group: Danger Zone
            SecuritySectionHeader(title = stringResource(id = R.string.settings_section_danger))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.settings_delete_all),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.settings_delete_all_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.openDeleteConfirmDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(stringResource(id = R.string.settings_delete_all))
                    }
                }
            }
        }
    }

    // Dialog: Set PIN
    if (state.showSetPinDialog) {
        SetPinDialog(
            onDismiss = { viewModel.dismissSetPinDialog() },
            onConfirm = { pin -> viewModel.onSaveNewPin(pin) },
        )
    }

    // Dialog: Change PIN
    if (state.showChangePinDialog) {
        ChangePinDialog(
            onDismiss = { viewModel.dismissChangePinDialog() },
            onConfirm = { currentPin, newPin -> viewModel.onChangePin(currentPin, newPin) },
        )
    }

    // Dialog: Verify PIN to Disable App Lock
    if (state.showVerifyPinToDisableDialog) {
        VerifyPinToDisableDialog(
            onDismiss = { viewModel.dismissVerifyPinToDisableDialog() },
            onConfirm = { pin -> viewModel.onVerifyPinToDisable(pin) },
        )
    }

    // Dialog: Timeout Selection
    if (state.showTimeoutDialog) {
        TimeoutSelectionDialog(
            currentTimeout = state.appLockTimeoutSeconds,
            onDismiss = { viewModel.dismissTimeoutDialog() },
            onSelect = { viewModel.onSetTimeout(it) },
        )
    }

    // Dialog: Export Password
    if (state.showExportPasswordDialog) {
        ExportPasswordDialog(
            onDismiss = { viewModel.dismissExportPasswordDialog() },
            onEvaluateStrength = { viewModel.evaluatePasswordStrength(it) },
            onConfirm = { password ->
                pendingExportPassword = password
                val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                exportLauncher.launch("spendsense_backup_$timestamp.ssbackup")
            }
        )
    }

    // Dialog: Import Password & Mode
    if (state.showImportPasswordDialog && state.pendingImportUri != null) {
        ImportPasswordDialog(
            onDismiss = { viewModel.dismissImportPasswordDialog() },
            onConfirm = { password, mode ->
                viewModel.onImportBackup(state.pendingImportUri!!, password, mode)
            }
        )
    }

    // Dialog: Delete All Data Confirmation 1
    if (state.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirmDialog() },
            title = { Text(stringResource(id = R.string.settings_delete_confirm_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(id = R.string.settings_delete_confirm_msg)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.proceedToFinalDeleteConfirm() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(id = R.string.settings_delete_confirm_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteConfirmDialog() }) {
                    Text(stringResource(id = R.string.action_cancel))
                }
            }
        )
    }

    // Dialog: Delete All Data Confirmation 2 (Final Warning)
    if (state.showDeleteFinalConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteFinalConfirmDialog() },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Are you absolutely sure?", fontWeight = FontWeight.Bold) },
            text = { Text("All your financial transactions, budgets, categories, and security PINs will be completely wiped immediately.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onDeleteAllData() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Permanently Erase Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteFinalConfirmDialog() }) {
                    Text(stringResource(id = R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun SecuritySectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
    )
}

@Composable
private fun SecuritySwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(24.dp),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant,
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
        )
    }
}

@Composable
private fun SecurityActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(24.dp),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant,
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun SetPinDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var step by remember { mutableIntStateOf(1) }
    var pin1 by remember { mutableStateOf("") }
    var pin2 by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (step == 1) stringResource(id = R.string.privacy_security_pin_dialog_title_set)
                else stringResource(id = R.string.privacy_security_pin_dialog_title_confirm),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (step == 1) "Enter a 4 to 6 digit security PIN."
                    else "Re-enter your PIN to confirm.",
                    style = MaterialTheme.typography.bodyMedium,
                )

                OutlinedTextField(
                    value = if (step == 1) pin1 else pin2,
                    onValueChange = { value ->
                        if (value.length <= 6 && value.all { it.isDigit() }) {
                            if (step == 1) pin1 = value else pin2 = value
                            error = null
                        }
                    },
                    label = { Text("PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = error != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                AnimatedVisibility(visible = error != null) {
                    Text(
                        text = error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step == 1) {
                        if (pin1.length !in 4..6) {
                            error = "PIN must be between 4 and 6 digits."
                        } else {
                            step = 2
                            error = null
                        }
                    } else {
                        if (pin1 != pin2) {
                            error = "PINs do not match. Try again."
                            pin2 = ""
                        } else {
                            onConfirm(pin1)
                        }
                    }
                }
            ) {
                Text(if (step == 1) "Next" else "Save PIN")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun ChangePinDialog(
    onDismiss: () -> Unit,
    onConfirm: (current: String, newPin: String) -> Boolean,
) {
    var currentPin by remember { mutableStateOf("") }
    var newPin1 by remember { mutableStateOf("") }
    var newPin2 by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.privacy_security_change_pin), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = currentPin,
                    onValueChange = {
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                            currentPin = it
                            error = null
                        }
                    },
                    label = { Text(stringResource(id = R.string.privacy_security_pin_dialog_title_current)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = newPin1,
                    onValueChange = {
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                            newPin1 = it
                            error = null
                        }
                    },
                    label = { Text(stringResource(id = R.string.privacy_security_pin_dialog_title_new)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = newPin2,
                    onValueChange = {
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                            newPin2 = it
                            error = null
                        }
                    },
                    label = { Text(stringResource(id = R.string.privacy_security_pin_dialog_title_confirm)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                AnimatedVisibility(visible = error != null) {
                    Text(
                        text = error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPin1.length !in 4..6) {
                        error = "New PIN must be 4 to 6 digits."
                    } else if (newPin1 != newPin2) {
                        error = "New PINs do not match."
                    } else {
                        val success = onConfirm(currentPin, newPin1)
                        if (!success) {
                            error = "Current PIN is incorrect."
                        }
                    }
                }
            ) {
                Text(stringResource(id = R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun VerifyPinToDisableDialog(
    onDismiss: () -> Unit,
    onConfirm: (pin: String) -> Boolean,
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Disable App Lock", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter your current PIN to disable App Lock.")
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                            pin = it
                            error = null
                        }
                    },
                    label = { Text("Current PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                AnimatedVisibility(visible = error != null) {
                    Text(text = error.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = onConfirm(pin)
                    if (!success) error = "Incorrect PIN."
                }
            ) {
                Text("Disable")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun TimeoutSelectionDialog(
    currentTimeout: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    val options = listOf(
        0 to stringResource(id = R.string.privacy_security_timeout_immediately),
        30 to stringResource(id = R.string.privacy_security_timeout_30s),
        60 to stringResource(id = R.string.privacy_security_timeout_1m),
        300 to stringResource(id = R.string.privacy_security_timeout_5m),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.privacy_security_timeout), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                options.forEach { (seconds, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(seconds) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = (currentTimeout == seconds),
                            onClick = { onSelect(seconds) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun ExportPasswordDialog(
    onDismiss: () -> Unit,
    onEvaluateStrength: (String) -> PasswordStrength,
    onConfirm: (password: String) -> Unit,
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val strength = remember(password) { onEvaluateStrength(password) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.privacy_security_password_dialog_title_export), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Set a password to encrypt this backup with AES-256-GCM. You will need this password to restore your data.",
                    style = MaterialTheme.typography.bodySmall,
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; error = null },
                    label = { Text(stringResource(id = R.string.privacy_security_password_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Password strength bar
                if (password.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val (progress, color, label) = when (strength) {
                            PasswordStrength.TOO_SHORT -> Triple(0.25f, MaterialTheme.colorScheme.error, stringResource(id = R.string.privacy_security_password_min_length))
                            PasswordStrength.WEAK -> Triple(0.35f, MaterialTheme.colorScheme.error, stringResource(id = R.string.privacy_security_strength_weak))
                            PasswordStrength.FAIR -> Triple(0.6f, Color(0xFFFFA000), stringResource(id = R.string.privacy_security_strength_fair))
                            PasswordStrength.GOOD -> Triple(0.85f, Color(0xFF4CAF50), stringResource(id = R.string.privacy_security_strength_good))
                            PasswordStrength.STRONG -> Triple(1f, Color(0xFF2E7D32), stringResource(id = R.string.privacy_security_strength_strong))
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            color = color,
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        )
                        Text(text = "Strength: $label", style = MaterialTheme.typography.labelSmall, color = color)
                    }
                }

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; error = null },
                    label = { Text(stringResource(id = R.string.privacy_security_password_confirm_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                AnimatedVisibility(visible = error != null) {
                    Text(text = error.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (password.length < 8) {
                        error = "Password must be at least 8 characters."
                    } else if (password != confirmPassword) {
                        error = "Passwords do not match."
                    } else {
                        onConfirm(password)
                    }
                }
            ) {
                Text(stringResource(id = R.string.export_report_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun ImportPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (password: String, mode: ImportMode) -> Unit,
) {
    var password by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf(ImportMode.MERGE) }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.privacy_security_password_dialog_title_import), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Enter the password used when creating this .ssbackup file.",
                    style = MaterialTheme.typography.bodySmall,
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(id = R.string.privacy_security_password_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "Restore mode:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth().clickable { mode = ImportMode.MERGE },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = mode == ImportMode.MERGE, onClick = { mode = ImportMode.MERGE })
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Merge", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("Preserve current data and add imported records", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().clickable { mode = ImportMode.REPLACE },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = mode == ImportMode.REPLACE, onClick = { mode = ImportMode.REPLACE })
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Replace Everything", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("Clear current transactions before restoring", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (password.isNotBlank()) onConfirm(password, mode) },
                enabled = password.isNotBlank(),
            ) {
                Text("Restore")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.action_cancel))
            }
        }
    )
}
