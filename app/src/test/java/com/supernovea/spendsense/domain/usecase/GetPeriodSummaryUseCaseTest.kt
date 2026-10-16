/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.repository.IncomeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class GetPeriodSummaryUseCaseTest {

    private val expenseRepository = mockk<ExpenseRepository>()
    private val incomeRepository = mockk<IncomeRepository>()
    private val budgetRepository = mockk<BudgetRepository>()
    private val settingsRepository = mockk<SettingsRepository>()

    private val category = Category(
        id = 1L,
        name = "Groceries",
        iconKey = "ic_food",
        colorHex = "#4CAF50",
        defaultType = ExpenseType.NEED,
        isArchived = false,
        isDefault = true,
    )

    @Test
    fun `GetPeriodSummaryUseCase reacts to changes and computes summary, insights, and budget status`() = runTest {
        val useCase = GetPeriodSummaryUseCase(
            expenseRepository = expenseRepository,
            incomeRepository = incomeRepository,
            budgetRepository = budgetRepository,
            settingsRepository = settingsRepository,
        )

        val month = Period.Month(YearMonth.of(2026, 10))
        val expense = ExpenseWithCategory(
            expense = Expense(
                id = 1L,
                amountMinor = 85000L,
                type = ExpenseType.NEED,
                categoryId = 1L,
                occurredAt = Instant.now(),
                occurredDate = LocalDate.of(2026, 10, 15),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            category = category,
        )

        val budget = Budget(
            monthKey = "2026-10",
            totalLimitMinor = 100000L,
            wasteLimitMinor = 20000L,
        )

        every {
            settingsRepository.observeSettings()
        } returns flowOf(UserSettings(weekStartDay = WeekStartDay.MONDAY))

        every {
            budgetRepository.observeByMonth("2026-10")
        } returns flowOf(budget)

        every {
            expenseRepository.observeBetween(any(), any())
        } returns flowOf(listOf(expense))

        every {
            incomeRepository.observeBetween(any(), any())
        } returns flowOf(emptyList())

        val result = useCase(month, today = LocalDate.of(2026, 10, 15)).first()

        // Summary checks
        assertEquals(85000L, result.summary.totalMinor)
        assertEquals(85000L, result.summary.needMinor)
        assertEquals(0L, result.summary.wasteMinor)

        // Budget status checks (85% total budget reached)
        assertNotNull(result.budgetStatus)
        assertEquals(85.0, result.budgetStatus?.totalProgressPct ?: 0.0, 0.001)
        assertEquals(false, result.budgetStatus?.isOverTotal)

        // Insights check
        assertNotNull(result.insights.find { it.id == "zero_waste" })
        assertNotNull(result.insights.find { it.id == "budget_total_80" })
    }

    @Test
    fun `GetPeriodSummaryUseCase emits null budget status for Week period without month budget`() = runTest {
        val useCase = GetPeriodSummaryUseCase(
            expenseRepository = expenseRepository,
            incomeRepository = incomeRepository,
            budgetRepository = budgetRepository,
            settingsRepository = settingsRepository,
        )

        val week = Period.Week(LocalDate.of(2026, 10, 12))

        every {
            settingsRepository.observeSettings()
        } returns flowOf(UserSettings())

        every {
            budgetRepository.observeByMonth(any())
        } returns flowOf(null)

        every {
            expenseRepository.observeBetween(any(), any())
        } returns flowOf(emptyList())

        every {
            incomeRepository.observeBetween(any(), any())
        } returns flowOf(emptyList())

        val result = useCase(week).first()

        assertEquals(0L, result.summary.totalMinor)
        assertNull(result.budgetStatus)
    }
}
