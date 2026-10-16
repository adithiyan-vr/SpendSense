/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupPayload(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val exportedAtEpochMillis: Long,
    val appVersion: String = "1.1.0",
    val expenses: List<BackupExpenseDto> = emptyList(),
    val categories: List<BackupCategoryDto> = emptyList(),
    val budgets: List<BackupBudgetDto> = emptyList(),
    val settings: BackupSettingsDto? = null,
    val incomes: List<BackupIncomeDto> = emptyList(),
    val incomeSources: List<BackupIncomeSourceDto> = emptyList(),
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION = 2
    }
}

@Serializable
data class BackupExpenseDto(
    val id: Long,
    val amountMinor: Long,
    val type: String, // "NEED" or "WASTE"
    val categoryId: Long,
    val note: String? = null,
    val paymentMethod: String, // e.g. "UPI"
    val occurredAtEpochMillis: Long,
    val occurredDateString: String, // "yyyy-MM-dd"
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
data class BackupCategoryDto(
    val id: Long,
    val name: String,
    val iconKey: String,
    val colorHex: String,
    val defaultType: String,
    val isArchived: Boolean = false,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
    val defaultKey: String? = null,
    val groupKey: String = "other",
)

@Serializable
data class BackupIncomeSourceDto(
    val id: Long,
    val name: String,
    val iconKey: String,
    val colorHex: String,
    val defaultKey: String? = null,
    val isArchived: Boolean = false,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
)

@Serializable
data class BackupIncomeDto(
    val id: Long,
    val amountMinor: Long,
    val sourceId: Long,
    val note: String? = null,
    val occurredAtEpochMillis: Long,
    val receivedDateString: String, // "yyyy-MM-dd"
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
data class BackupBudgetDto(
    val monthKey: String, // "yyyy-MM"
    val totalLimitMinor: Long,
    val wasteLimitMinor: Long? = null,
)

@Serializable
data class BackupSettingsDto(
    val currencySymbol: String = "₹",
    val weekStartDay: String = "MONDAY",
    val themeMode: String = "SYSTEM",
    val pureBlack: Boolean = false,
    val dynamicColor: Boolean = false,
    val reminderEnabled: Boolean = false,
    val reminderTimeMinutes: Int = 21 * 60,
    val weeklySummaryEnabled: Boolean = true,
    val monthlySummaryEnabled: Boolean = true,
    val userProfile: String = "STUDENT",
    val openingBalanceMinor: Long = 0L,
    val pocketMoneyDay: Int? = null,
    val hideAmountsDefault: Boolean = false,
    val secureScreenEnabled: Boolean = true,
    val notificationPrivacyEnabled: Boolean = true,
)

enum class ImportMode {
    MERGE,
    REPLACE,
}

data class ImportResult(
    val expensesRestoredCount: Int,
    val categoriesRestoredCount: Int,
    val budgetsRestoredCount: Int,
    val incomesRestoredCount: Int = 0,
    val incomeSourcesRestoredCount: Int = 0,
)
