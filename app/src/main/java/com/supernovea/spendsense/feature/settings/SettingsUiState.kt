/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.settings

import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.WeekStartDay

data class SettingsUiState(
    val currencySymbol: String = "₹",
    val weekStartDay: WeekStartDay = WeekStartDay.MONDAY,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val pureBlack: Boolean = false,
    val dynamicColor: Boolean = false,
    val dailyReminderEnabled: Boolean = false,
    val weeklySummaryEnabled: Boolean = true,
    val monthlySummaryEnabled: Boolean = true,
    val lastBackupEpochMillis: Long? = null,
    val showDeleteAllDialog: Boolean = false,
    val isDeletingData: Boolean = false,
    val showImportDialog: Boolean = false,
    val pendingImportJson: String? = null,
    val isExportingOrImporting: Boolean = false,
    val userMessage: String? = null,
    val errorMessage: String? = null,
)
