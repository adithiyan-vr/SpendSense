/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.backup

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.Income
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.UserProfileType
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.CategoryRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonBackupManager @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val settingsRepository: SettingsRepository,
    private val incomeRepository: IncomeRepository,
    private val incomeSourceRepository: IncomeSourceRepository,
) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun createBackupPayload(): BackupPayload {
        val expensesWithCat = expenseRepository.observeAll().first()
        val allCategories = categoryRepository.observeAll().first()
        val allBudgets = budgetRepository.observeAll().first()
        val settings = settingsRepository.getSettings()
        val allIncomesWithSource = incomeRepository.observeAll().first()
        val allIncomeSources = incomeSourceRepository.observeAll().first()

        val expenseDtos = expensesWithCat.map { item ->
            val exp = item.expense
            BackupExpenseDto(
                id = exp.id,
                amountMinor = exp.amountMinor,
                type = exp.type.name,
                categoryId = exp.categoryId,
                note = exp.note,
                paymentMethod = exp.paymentMethod.name,
                occurredAtEpochMillis = exp.occurredAt.toEpochMilli(),
                occurredDateString = exp.occurredDate.toString(),
                createdAtEpochMillis = exp.createdAt.toEpochMilli(),
                updatedAtEpochMillis = exp.updatedAt.toEpochMilli(),
            )
        }

        val categoryDtos = allCategories.map { cat ->
            BackupCategoryDto(
                id = cat.id,
                name = cat.name,
                iconKey = cat.iconKey,
                colorHex = cat.colorHex,
                defaultType = cat.defaultType.name,
                isArchived = cat.isArchived,
                isDefault = cat.isDefault,
                sortOrder = cat.sortOrder,
                defaultKey = cat.defaultKey,
                groupKey = cat.groupKey,
            )
        }

        val budgetDtos = allBudgets.map { b ->
            BackupBudgetDto(
                monthKey = b.monthKey,
                totalLimitMinor = b.totalLimitMinor,
                wasteLimitMinor = b.wasteLimitMinor,
            )
        }

        val incomeSourceDtos = allIncomeSources.map { src ->
            BackupIncomeSourceDto(
                id = src.id,
                name = src.name,
                iconKey = src.iconKey,
                colorHex = src.colorHex,
                defaultKey = src.defaultKey,
                isArchived = src.isArchived,
                isDefault = src.isDefault,
                sortOrder = src.sortOrder,
            )
        }

        val incomeDtos = allIncomesWithSource.map { item ->
            val inc = item.income
            BackupIncomeDto(
                id = inc.id,
                amountMinor = inc.amountMinor,
                sourceId = inc.sourceId,
                note = inc.note,
                occurredAtEpochMillis = inc.receivedAt.toEpochMilli(),
                receivedDateString = inc.receivedDate.toString(),
                createdAtEpochMillis = inc.createdAt.toEpochMilli(),
                updatedAtEpochMillis = inc.updatedAt.toEpochMilli(),
            )
        }

        val settingsDto = BackupSettingsDto(
            currencySymbol = settings.currencySymbol,
            weekStartDay = settings.weekStartDay.name,
            themeMode = settings.themeMode.name,
            pureBlack = settings.pureBlack,
            dynamicColor = settings.dynamicColor,
            reminderEnabled = settings.reminderEnabled,
            reminderTimeMinutes = settings.reminderTimeMinutes,
            weeklySummaryEnabled = settings.weeklySummaryEnabled,
            monthlySummaryEnabled = settings.monthlySummaryEnabled,
            userProfile = settings.userProfile.name,
            openingBalanceMinor = settings.openingBalanceMinor,
            pocketMoneyDay = settings.pocketMoneyDay,
            hideAmountsDefault = settings.hideAmountsDefault,
            secureScreenEnabled = settings.secureScreenEnabled,
            notificationPrivacyEnabled = settings.notificationPrivacyEnabled,
        )

        return BackupPayload(
            schemaVersion = BackupPayload.CURRENT_SCHEMA_VERSION,
            exportedAtEpochMillis = System.currentTimeMillis(),
            appVersion = "1.1.0",
            expenses = expenseDtos,
            categories = categoryDtos,
            budgets = budgetDtos,
            settings = settingsDto,
            incomes = incomeDtos,
            incomeSources = incomeSourceDtos,
        )
    }

    suspend fun exportJson(): String {
        val payload = createBackupPayload()
        val encoded = json.encodeToString(BackupPayload.serializer(), payload)
        settingsRepository.setLastBackupEpochMillis(System.currentTimeMillis())
        return encoded
    }

    suspend fun importJson(jsonString: String, mode: ImportMode): ImportResult {
        val payload = json.decodeFromString(BackupPayload.serializer(), jsonString)

        if (payload.schemaVersion > BackupPayload.CURRENT_SCHEMA_VERSION) {
            throw IllegalArgumentException(
                "Unsupported backup schema version: ${payload.schemaVersion}. Please update SpendSense to restore this backup."
            )
        }

        if (mode == ImportMode.REPLACE) {
            // Delete all current expenses and incomes
            expenseRepository.deleteAllExpenses()
            incomeRepository.deleteAllIncomes()

            // Delete current budgets
            val currentBudgets = budgetRepository.observeAll().first()
            for (b in currentBudgets) {
                budgetRepository.deleteBudget(b.monthKey)
            }
        }

        // 1. Restore Categories
        var categoriesCount = 0
        for (catDto in payload.categories) {
            val cat = Category(
                id = if (mode == ImportMode.REPLACE) catDto.id else 0L,
                name = catDto.name,
                iconKey = catDto.iconKey,
                colorHex = catDto.colorHex,
                defaultType = runCatching { ExpenseType.valueOf(catDto.defaultType) }.getOrDefault(ExpenseType.NEED),
                isArchived = catDto.isArchived,
                isDefault = catDto.isDefault,
                sortOrder = catDto.sortOrder,
                defaultKey = catDto.defaultKey,
                groupKey = catDto.groupKey,
            )
            categoryRepository.upsertCategory(cat)
            categoriesCount++
        }

        // 2. Restore Budgets
        var budgetsCount = 0
        for (bDto in payload.budgets) {
            val budget = Budget(
                monthKey = bDto.monthKey,
                totalLimitMinor = bDto.totalLimitMinor,
                wasteLimitMinor = bDto.wasteLimitMinor,
            )
            budgetRepository.setBudget(budget)
            budgetsCount++
        }

        // 3. Restore Income Sources
        var incomeSourcesCount = 0
        for (srcDto in payload.incomeSources) {
            val src = IncomeSource(
                id = if (mode == ImportMode.REPLACE) srcDto.id else 0L,
                name = srcDto.name,
                iconKey = srcDto.iconKey,
                colorHex = srcDto.colorHex,
                defaultKey = srcDto.defaultKey,
                isArchived = srcDto.isArchived,
                isDefault = srcDto.isDefault,
                sortOrder = srcDto.sortOrder,
            )
            incomeSourceRepository.upsertIncomeSource(src)
            incomeSourcesCount++
        }

        // 4. Restore Expenses
        var expensesCount = 0
        val existingExpenses = if (mode == ImportMode.MERGE) {
            expenseRepository.observeAll().first()
        } else {
            emptyList()
        }

        for (expDto in payload.expenses) {
            val occurredAt = Instant.ofEpochMilli(expDto.occurredAtEpochMillis)
            val occurredDate = runCatching { LocalDate.parse(expDto.occurredDateString) }
                .getOrDefault(LocalDate.now())

            // If merging, deduplicate by exact amount, timestamp, and note
            val isDuplicate = existingExpenses.any { existing ->
                existing.expense.occurredAt.toEpochMilli() == expDto.occurredAtEpochMillis &&
                    existing.expense.amountMinor == expDto.amountMinor &&
                    existing.expense.note == expDto.note
            }

            if (!isDuplicate) {
                val expense = Expense(
                    id = if (mode == ImportMode.REPLACE) expDto.id else 0L,
                    amountMinor = expDto.amountMinor,
                    type = runCatching { ExpenseType.valueOf(expDto.type) }.getOrDefault(ExpenseType.NEED),
                    categoryId = expDto.categoryId,
                    note = expDto.note,
                    paymentMethod = runCatching { PaymentMethod.valueOf(expDto.paymentMethod) }.getOrDefault(PaymentMethod.OTHER),
                    occurredAt = occurredAt,
                    occurredDate = occurredDate,
                    createdAt = Instant.ofEpochMilli(expDto.createdAtEpochMillis),
                    updatedAt = Instant.ofEpochMilli(expDto.updatedAtEpochMillis),
                )
                expenseRepository.insertExpense(expense)
                expensesCount++
            }
        }

        // 5. Restore Incomes
        var incomesCount = 0
        val existingIncomes = if (mode == ImportMode.MERGE) {
            incomeRepository.observeAll().first()
        } else {
            emptyList()
        }

        for (incDto in payload.incomes) {
            val occurredAt = Instant.ofEpochMilli(incDto.occurredAtEpochMillis)
            val receivedDate = runCatching { LocalDate.parse(incDto.receivedDateString) }
                .getOrDefault(LocalDate.now())

            val isDuplicate = existingIncomes.any { existing ->
                existing.income.receivedAt.toEpochMilli() == incDto.occurredAtEpochMillis &&
                    existing.income.amountMinor == incDto.amountMinor &&
                    existing.income.note == incDto.note
            }

            if (!isDuplicate) {
                val income = Income(
                    id = if (mode == ImportMode.REPLACE) incDto.id else 0L,
                    amountMinor = incDto.amountMinor,
                    sourceId = incDto.sourceId,
                    note = incDto.note,
                    receivedAt = occurredAt,
                    receivedDate = receivedDate,
                    createdAt = Instant.ofEpochMilli(incDto.createdAtEpochMillis),
                    updatedAt = Instant.ofEpochMilli(incDto.updatedAtEpochMillis),
                )
                incomeRepository.insertIncome(income)
                incomesCount++
            }
        }

        // 6. Restore Settings if present and in REPLACE mode
        if (mode == ImportMode.REPLACE && payload.settings != null) {
            val s = payload.settings
            settingsRepository.setCurrencySymbol(s.currencySymbol)
            runCatching { WeekStartDay.valueOf(s.weekStartDay) }.getOrNull()?.let {
                settingsRepository.setWeekStartDay(it)
            }
            runCatching { ThemeMode.valueOf(s.themeMode) }.getOrNull()?.let {
                settingsRepository.setThemeMode(it)
            }
            settingsRepository.setPureBlack(s.pureBlack)
            settingsRepository.setDynamicColor(s.dynamicColor)
            settingsRepository.setReminderEnabled(s.reminderEnabled)
            settingsRepository.setReminderTimeMinutes(s.reminderTimeMinutes)
            settingsRepository.setWeeklySummaryEnabled(s.weeklySummaryEnabled)
            settingsRepository.setMonthlySummaryEnabled(s.monthlySummaryEnabled)
            runCatching { UserProfileType.valueOf(s.userProfile) }.getOrNull()?.let {
                settingsRepository.setUserProfile(it)
            }
            settingsRepository.setOpeningBalanceMinor(s.openingBalanceMinor)
            settingsRepository.setPocketMoneyDay(s.pocketMoneyDay)
            settingsRepository.setHideAmountsDefault(s.hideAmountsDefault)
            settingsRepository.setSecureScreenEnabled(s.secureScreenEnabled)
            settingsRepository.setNotificationPrivacyEnabled(s.notificationPrivacyEnabled)
        }

        settingsRepository.setLastBackupEpochMillis(System.currentTimeMillis())

        return ImportResult(
            expensesRestoredCount = expensesCount,
            categoriesRestoredCount = categoriesCount,
            budgetsRestoredCount = budgetsCount,
            incomesRestoredCount = incomesCount,
            incomeSourcesRestoredCount = incomeSourcesCount,
        )
    }
}
