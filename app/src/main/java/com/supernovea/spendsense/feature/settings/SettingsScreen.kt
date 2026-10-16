/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import com.supernovea.spendsense.core.designsystem.SpendSenseLogo
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.backup.ImportMode
import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.WeekStartDay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SettingsScreen(
    onNavigateToCategories: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToPrivacySecurity: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val csv = viewModel.getCsvContent()
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(csv.toByteArray(Charsets.UTF_8))
                    }
                    viewModel.onEvent(SettingsEvent.ExportCsvSuccess)
                } catch (e: Exception) {
                    viewModel.onEvent(SettingsEvent.ExportFailed(e.message ?: "Failed to export CSV"))
                }
            }
        }
    }

    val jsonExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val json = viewModel.getJsonBackupContent()
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(json.toByteArray(Charsets.UTF_8))
                    }
                    viewModel.onEvent(SettingsEvent.ExportJsonSuccess)
                } catch (e: Exception) {
                    viewModel.onEvent(SettingsEvent.ExportFailed(e.message ?: "Failed to export JSON backup"))
                }
            }
        }
    }

    val jsonImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val content = context.contentResolver.openInputStream(uri)?.use { input ->
                        input.bufferedReader(Charsets.UTF_8).readText()
                    }
                    if (content != null) {
                        viewModel.onEvent(SettingsEvent.ImportFileSelected(content))
                    }
                } catch (e: Exception) {
                    viewModel.onEvent(SettingsEvent.ExportFailed("Could not read backup file: ${e.message}"))
                }
            }
        }
    }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onEvent(SettingsEvent.ToggleDailyReminder(true))
        }
    }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(SettingsEvent.DismissMessage)
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(SettingsEvent.DismissMessage)
        }
    }

    SettingsContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToCategories = onNavigateToCategories,
        onNavigateToBudget = onNavigateToBudget,
        onNavigateToPrivacySecurity = onNavigateToPrivacySecurity,
        onNavigateBack = onNavigateBack,
        onExportCsv = { csvLauncher.launch("spendsense_expenses_${System.currentTimeMillis()}.csv") },
        onExportJson = { jsonExportLauncher.launch("spendsense_backup_${System.currentTimeMillis()}.json") },
        onImportJson = { jsonImportLauncher.launch(arrayOf("application/json", "text/*")) },
        onDailyReminderToggled = { enabled ->
            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.onEvent(SettingsEvent.ToggleDailyReminder(enabled))
            }
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToPrivacySecurity: () -> Unit,
    onNavigateBack: (() -> Unit)?,
    onExportCsv: () -> Unit,
    onExportJson: () -> Unit,
    onImportJson: () -> Unit,
    onDailyReminderToggled: (Boolean) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showWeekStartDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.cd_back),
                            )
                        }
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Group: Manage
            SettingsSectionHeader(title = stringResource(id = R.string.settings_section_manage))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column {
                    SettingsNavigationRow(
                        icon = Icons.Default.Category,
                        title = stringResource(id = R.string.settings_manage_categories),
                        subtitle = stringResource(id = R.string.settings_manage_categories_desc),
                        onClick = onNavigateToCategories,
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsNavigationRow(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = stringResource(id = R.string.settings_manage_budget),
                        subtitle = stringResource(id = R.string.settings_manage_budget_desc),
                        onClick = onNavigateToBudget,
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsNavigationRow(
                        icon = Icons.Default.Security,
                        title = stringResource(id = R.string.privacy_security_title),
                        subtitle = stringResource(id = R.string.privacy_security_subtitle),
                        onClick = onNavigateToPrivacySecurity,
                    )
                }
            }

            // Group: Preferences
            SettingsSectionHeader(title = stringResource(id = R.string.settings_section_preferences))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column {
                    SettingsValueRow(
                        icon = Icons.Default.CurrencyExchange,
                        title = stringResource(id = R.string.settings_currency),
                        value = state.currencySymbol,
                        onClick = { showCurrencyDialog = true },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsValueRow(
                        icon = Icons.Default.DateRange,
                        title = stringResource(id = R.string.settings_week_start),
                        value = if (state.weekStartDay == WeekStartDay.MONDAY) {
                            stringResource(id = R.string.settings_week_start_mon)
                        } else {
                            stringResource(id = R.string.settings_week_start_sun)
                        },
                        onClick = { showWeekStartDialog = true },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsValueRow(
                        icon = Icons.Default.Brightness4,
                        title = stringResource(id = R.string.settings_theme),
                        value = when (state.themeMode) {
                            ThemeMode.SYSTEM -> stringResource(id = R.string.settings_theme_system)
                            ThemeMode.LIGHT -> stringResource(id = R.string.settings_theme_light)
                            ThemeMode.DARK -> stringResource(id = R.string.settings_theme_dark)
                        },
                        onClick = { showThemeDialog = true },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsToggleRow(
                        icon = Icons.Default.DarkMode,
                        title = stringResource(id = R.string.settings_theme_pure_black),
                        subtitle = stringResource(id = R.string.settings_theme_pure_black_desc),
                        checked = state.pureBlack,
                        onCheckedChange = { onEvent(SettingsEvent.TogglePureBlack(it)) },
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        SettingsToggleRow(
                            icon = Icons.Default.Palette,
                            title = stringResource(id = R.string.settings_theme_dynamic_color),
                            subtitle = stringResource(id = R.string.settings_theme_dynamic_color_desc),
                            checked = state.dynamicColor,
                            onCheckedChange = { onEvent(SettingsEvent.ToggleDynamicColor(it)) },
                        )
                    }
                }
            }

            // Group: Reminders & Summaries
            SettingsSectionHeader(title = stringResource(id = R.string.settings_section_notifications))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column {
                    SettingsToggleRow(
                        icon = Icons.Default.Notifications,
                        title = stringResource(id = R.string.settings_daily_reminder),
                        subtitle = stringResource(id = R.string.settings_daily_reminder_desc),
                        checked = state.dailyReminderEnabled,
                        onCheckedChange = onDailyReminderToggled,
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsToggleRow(
                        icon = Icons.Default.Notifications,
                        title = stringResource(id = R.string.settings_weekly_summary),
                        subtitle = stringResource(id = R.string.settings_weekly_summary_desc),
                        checked = state.weeklySummaryEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.ToggleWeeklySummary(it)) },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsToggleRow(
                        icon = Icons.Default.Notifications,
                        title = stringResource(id = R.string.settings_monthly_summary),
                        subtitle = stringResource(id = R.string.settings_monthly_summary_desc),
                        checked = state.monthlySummaryEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.ToggleMonthlySummary(it)) },
                    )
                }
            }

            // Group: Export & Backup
            SettingsSectionHeader(title = stringResource(id = R.string.settings_section_backup))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column {
                    // Backup Status Row
                    val lastBackupText = if (state.lastBackupEpochMillis != null) {
                        val dt = Instant.ofEpochMilli(state.lastBackupEpochMillis)
                            .atZone(ZoneId.systemDefault())
                        val fmt = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.getDefault())
                        stringResource(id = R.string.settings_last_backup, dt.format(fmt))
                    } else {
                        stringResource(id = R.string.settings_last_backup_never)
                    }

                    val isBackupOld = state.lastBackupEpochMillis == null ||
                        (System.currentTimeMillis() - state.lastBackupEpochMillis) > 30L * 24 * 60 * 60 * 1000L

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = null,
                            tint = if (isBackupOld) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lastBackupText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            if (isBackupOld) {
                                Text(
                                    text = stringResource(id = R.string.settings_backup_warning),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SettingsNavigationRow(
                        icon = Icons.Default.FileDownload,
                        title = stringResource(id = R.string.settings_export_csv),
                        subtitle = stringResource(id = R.string.settings_export_csv_desc),
                        onClick = onExportCsv,
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SettingsNavigationRow(
                        icon = Icons.Default.FileDownload,
                        title = stringResource(id = R.string.settings_export_json),
                        subtitle = stringResource(id = R.string.settings_export_json_desc),
                        onClick = onExportJson,
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SettingsNavigationRow(
                        icon = Icons.Default.FileUpload,
                        title = stringResource(id = R.string.settings_import_json),
                        subtitle = stringResource(id = R.string.settings_import_json_desc),
                        onClick = onImportJson,
                    )
                }
            }

            // Group: About SpendSense
            SettingsSectionHeader(title = stringResource(id = R.string.settings_section_about))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SpendSenseLogo(
                            size = 40.dp,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                        )

                        Column {
                            Text(
                                text = "SpendSense",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(id = R.string.settings_version),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Text(
                        text = stringResource(id = R.string.settings_about_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = stringResource(id = R.string.settings_built_by),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = stringResource(id = R.string.settings_privacy_note),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            // Group: Danger Zone
            SettingsSectionHeader(
                title = stringResource(id = R.string.settings_section_danger),
                color = MaterialTheme.colorScheme.error,
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.isDeletingData) {
                            onEvent(SettingsEvent.RequestDeleteAllData)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isDeletingData) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.error,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.settings_delete_all),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = stringResource(id = R.string.settings_delete_all_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Double Confirmation Dialog for Data Wipe
    if (state.showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.CancelDeleteAllData) },
            title = {
                Text(
                    text = stringResource(id = R.string.settings_delete_confirm_title),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
            },
            text = {
                Text(text = stringResource(id = R.string.settings_delete_confirm_msg))
            },
            confirmButton = {
                Button(
                    onClick = { onEvent(SettingsEvent.ConfirmDeleteAllData) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                ) {
                    Text(text = stringResource(id = R.string.settings_delete_confirm_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(SettingsEvent.CancelDeleteAllData) }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        )
    }

    // Currency Symbol Dialog
    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentSymbol = state.currencySymbol,
            onSelect = {
                onEvent(SettingsEvent.ChangeCurrencySymbol(it))
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false },
        )
    }

    // Theme Mode Dialog
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentMode = state.themeMode,
            onSelect = {
                onEvent(SettingsEvent.ChangeThemeMode(it))
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false },
        )
    }

    // Week Start Day Dialog
    if (showWeekStartDialog) {
        WeekStartSelectionDialog(
            currentDay = state.weekStartDay,
            onSelect = {
                onEvent(SettingsEvent.ChangeWeekStartDay(it))
                showWeekStartDialog = false
            },
            onDismiss = { showWeekStartDialog = false },
        )
    }
    // Processing / Loading indicator dialog
    if (state.isExportingOrImporting) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            text = {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    Text(
                        text = stringResource(id = R.string.settings_processing),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        )
    }

    // Import Mode Dialog
    if (state.showImportDialog) {
        ImportConfirmationDialog(
            onConfirm = { mode -> onEvent(SettingsEvent.ConfirmImport(mode)) },
            onDismiss = { onEvent(SettingsEvent.CancelImport) },
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier.padding(start = 4.dp),
    )
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsValueRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun CurrencySelectionDialog(
    currentSymbol: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val commonCurrencies = listOf(
        "₹" to "Indian Rupee (₹)",
        "$" to "US Dollar ($)",
        "€" to "Euro (€)",
        "£" to "British Pound (£)",
        "¥" to "Japanese Yen (¥)",
        "₩" to "South Korean Won (₩)",
        "A$" to "Australian Dollar (A$)",
        "C$" to "Canadian Dollar (C$)",
    )
    var customSymbol by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.settings_currency)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                commonCurrencies.forEach { (sym, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(sym) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSymbol == sym,
                            onClick = { onSelect(sym) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                OutlinedTextField(
                    value = customSymbol,
                    onValueChange = { customSymbol = it },
                    label = { Text("Custom Symbol") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            if (customSymbol.isNotBlank()) {
                Button(onClick = { onSelect(customSymbol.trim()) }) {
                    Text(stringResource(id = R.string.action_save))
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = R.string.action_cancel))
                }
            }
        },
        dismissButton = {
            if (customSymbol.isNotBlank()) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = R.string.action_cancel))
                }
            }
        }
    )
}

@Composable
private fun ThemeSelectionDialog(
    currentMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf(
        ThemeMode.SYSTEM to stringResource(id = R.string.settings_theme_system),
        ThemeMode.LIGHT to stringResource(id = R.string.settings_theme_light),
        ThemeMode.DARK to stringResource(id = R.string.settings_theme_dark),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.settings_theme)) },
        text = {
            Column {
                options.forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mode) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentMode == mode,
                            onClick = { onSelect(mode) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun WeekStartSelectionDialog(
    currentDay: WeekStartDay,
    onSelect: (WeekStartDay) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf(
        WeekStartDay.MONDAY to stringResource(id = R.string.settings_week_start_mon),
        WeekStartDay.SUNDAY to stringResource(id = R.string.settings_week_start_sun),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.settings_week_start)) },
        text = {
            Column {
                options.forEach { (day, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(day) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentDay == day,
                            onClick = { onSelect(day) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun ImportConfirmationDialog(
    onConfirm: (ImportMode) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedMode by remember { mutableStateOf(ImportMode.MERGE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.FileUpload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            Text(
                text = stringResource(id = R.string.settings_import_title),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(id = R.string.settings_import_msg),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedMode = ImportMode.MERGE }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedMode == ImportMode.MERGE,
                        onClick = { selectedMode = ImportMode.MERGE },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.settings_import_merge),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = stringResource(id = R.string.settings_import_merge_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedMode = ImportMode.REPLACE }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedMode == ImportMode.REPLACE,
                        onClick = { selectedMode = ImportMode.REPLACE },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.settings_import_replace),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = stringResource(id = R.string.settings_import_replace_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMode) },
                colors = if (selectedMode == ImportMode.REPLACE) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Text(
                    text = if (selectedMode == ImportMode.REPLACE) {
                        stringResource(id = R.string.settings_import_replace)
                    } else {
                        stringResource(id = R.string.settings_import_merge)
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        }
    )
}

