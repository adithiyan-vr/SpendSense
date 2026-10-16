/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.UserProfileType
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.CategoryRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.core.security.AppLockManager
import com.supernovea.spendsense.core.security.LockoutManager
import com.supernovea.spendsense.core.security.PinManager
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case to delete all expenses, incomes, reset budgets, unarchive default categories
 * and income sources, clear security state, and restore default user settings. Double confirmation is handled at the UI layer.
 */
class DeleteAllDataUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val settingsRepository: SettingsRepository,
    private val incomeRepository: IncomeRepository,
    private val incomeSourceRepository: IncomeSourceRepository,
    private val pinManager: PinManager,
    private val lockoutManager: LockoutManager,
    private val appLockManager: AppLockManager,
) {

    suspend operator fun invoke() {
        // 1. Delete all expenses and incomes
        expenseRepository.deleteAllExpenses()
        incomeRepository.deleteAllIncomes()

        // 2. Delete all monthly budgets
        val allBudgets = budgetRepository.observeAll().first()
        for (b in allBudgets) {
            budgetRepository.deleteBudget(b.monthKey)
        }

        // 3. Unarchive all categories
        val allCategories = categoryRepository.observeAll().first()
        for (cat in allCategories) {
            if (cat.isArchived) {
                categoryRepository.setArchived(cat.id, false)
            }
        }

        // 4. Unarchive all income sources
        val allSources = incomeSourceRepository.observeAll().first()
        for (src in allSources) {
            if (src.isArchived) {
                incomeSourceRepository.setArchived(src.id, false)
            }
        }

        // 5. Reset all user settings
        settingsRepository.setCurrencySymbol("₹")
        settingsRepository.setWeekStartDay(WeekStartDay.MONDAY)
        settingsRepository.setThemeMode(ThemeMode.SYSTEM)
        settingsRepository.setPureBlack(false)
        settingsRepository.setDynamicColor(false)
        settingsRepository.setReminderEnabled(false)
        settingsRepository.setWeeklySummaryEnabled(true)
        settingsRepository.setMonthlySummaryEnabled(true)
        settingsRepository.setUserProfile(UserProfileType.STUDENT)
        settingsRepository.setOpeningBalanceMinor(0L)
        settingsRepository.setPocketMoneyDay(null)
        settingsRepository.setHideAmountsDefault(false)
        settingsRepository.setSecureScreenEnabled(true)
        settingsRepository.setAppLockEnabled(false)
        settingsRepository.setBiometricAuthEnabled(true)
        settingsRepository.setAppLockTimeoutSeconds(0)
        settingsRepository.setNotificationPrivacyEnabled(true)
        settingsRepository.setOnboardingDone(false)

        // 6. Reset security state
        pinManager.clearPin()
        lockoutManager.resetLockout()
        appLockManager.unlock()
    }
}
