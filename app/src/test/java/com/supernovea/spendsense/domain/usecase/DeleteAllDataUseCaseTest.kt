/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.UserProfileType
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.CategoryRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.core.security.AppLockManager
import com.supernovea.spendsense.core.security.LockoutManager
import com.supernovea.spendsense.core.security.PinManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteAllDataUseCaseTest {

    private val expenseRepository = mockk<ExpenseRepository>(relaxed = true)
    private val budgetRepository = mockk<BudgetRepository>(relaxed = true)
    private val categoryRepository = mockk<CategoryRepository>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val incomeRepository = mockk<IncomeRepository>(relaxed = true)
    private val incomeSourceRepository = mockk<IncomeSourceRepository>(relaxed = true)
    private val pinManager = mockk<PinManager>(relaxed = true)
    private val lockoutManager = mockk<LockoutManager>(relaxed = true)
    private val appLockManager = mockk<AppLockManager>(relaxed = true)

    private val useCase = DeleteAllDataUseCase(
        expenseRepository = expenseRepository,
        categoryRepository = categoryRepository,
        budgetRepository = budgetRepository,
        settingsRepository = settingsRepository,
        incomeRepository = incomeRepository,
        incomeSourceRepository = incomeSourceRepository,
        pinManager = pinManager,
        lockoutManager = lockoutManager,
        appLockManager = appLockManager,
    )

    @Test
    fun `invoke deletes all expenses, budgets, resets categories, and resets settings`() = runTest {
        val budgets = listOf(
            Budget(monthKey = "2026-09", totalLimitMinor = 4000000L),
            Budget(monthKey = "2026-10", totalLimitMinor = 5000000L),
        )
        val defaultArchivedCategory = Category(
            id = 1L,
            name = "Groceries",
            iconKey = "ic_grocery",
            colorHex = "#4CAF50",
            defaultType = ExpenseType.NEED,
            isArchived = true,
            isDefault = true,
        )
        val customCategory = Category(
            id = 2L,
            name = "Custom Hobby",
            iconKey = "ic_sports",
            colorHex = "#E91E63",
            defaultType = ExpenseType.WASTE,
            isArchived = false,
            isDefault = false,
        )
        val archivedIncomeSource = IncomeSource(
            id = 10L,
            name = "Old Freelance",
            iconKey = "work",
            colorHex = "#607D8B",
            isArchived = true,
        )

        coEvery { budgetRepository.observeAll() } returns flowOf(budgets)
        coEvery { categoryRepository.observeAll() } returns flowOf(listOf(defaultArchivedCategory, customCategory))
        coEvery { incomeSourceRepository.observeAll() } returns flowOf(listOf(archivedIncomeSource))

        useCase()

        coVerify { expenseRepository.deleteAllExpenses() }
        coVerify { incomeRepository.deleteAllIncomes() }
        coVerify { budgetRepository.deleteBudget("2026-09") }
        coVerify { budgetRepository.deleteBudget("2026-10") }
        coVerify { categoryRepository.setArchived(1L, false) }
        coVerify { incomeSourceRepository.setArchived(10L, false) }
        coVerify { settingsRepository.setCurrencySymbol("₹") }
        coVerify { settingsRepository.setWeekStartDay(WeekStartDay.MONDAY) }
        coVerify { settingsRepository.setThemeMode(ThemeMode.SYSTEM) }
        coVerify { settingsRepository.setUserProfile(UserProfileType.STUDENT) }
        coVerify { settingsRepository.setOpeningBalanceMinor(0L) }
        coVerify { settingsRepository.setSecureScreenEnabled(true) }
        coVerify { settingsRepository.setAppLockEnabled(false) }
        coVerify { settingsRepository.setBiometricAuthEnabled(true) }
        coVerify { settingsRepository.setAppLockTimeoutSeconds(0) }
        coVerify { settingsRepository.setNotificationPrivacyEnabled(true) }
        coVerify { settingsRepository.setOnboardingDone(false) }
        verify { pinManager.clearPin() }
        verify { lockoutManager.resetLockout() }
        verify { appLockManager.unlock() }
    }
}
