/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.settings

import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.WeekStartDay

sealed interface SettingsEvent {
    data class ChangeCurrencySymbol(val symbol: String) : SettingsEvent
    data class ChangeWeekStartDay(val day: WeekStartDay) : SettingsEvent
    data class ChangeThemeMode(val mode: ThemeMode) : SettingsEvent
    data class TogglePureBlack(val enabled: Boolean) : SettingsEvent
    data class ToggleDynamicColor(val enabled: Boolean) : SettingsEvent
    data class ToggleDailyReminder(val enabled: Boolean) : SettingsEvent
    data class ToggleWeeklySummary(val enabled: Boolean) : SettingsEvent
    data class ToggleMonthlySummary(val enabled: Boolean) : SettingsEvent
    data object RequestDeleteAllData : SettingsEvent
    data object CancelDeleteAllData : SettingsEvent
    data object ConfirmDeleteAllData : SettingsEvent
    data class ImportFileSelected(val jsonContent: String) : SettingsEvent
    data class ConfirmImport(val mode: com.supernovea.spendsense.core.backup.ImportMode) : SettingsEvent
    data object CancelImport : SettingsEvent
    data object ExportCsvSuccess : SettingsEvent
    data object ExportJsonSuccess : SettingsEvent
    data class ExportFailed(val message: String) : SettingsEvent
    data object DismissMessage : SettingsEvent
}
