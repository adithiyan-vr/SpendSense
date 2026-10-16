/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.analytics

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.Income
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.model.Period
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class AnalyticsEngineIncomeTest {

    private val category = Category(
        id = 1L,
        name = "Groceries",
        iconKey = "ic_food",
        colorHex = "#4CAF50",
        defaultType = ExpenseType.NEED,
        isArchived = false,
        isDefault = true,
    )

    private val salarySource = IncomeSource(
        id = 1L,
        defaultKey = "salary",
        name = "Salary",
        iconKey = "ic_salary",
        colorHex = "#2E7D32",
        isArchived = false,
        isDefault = true,
        sortOrder = 1,
    )

    private val freelanceSource = IncomeSource(
        id = 2L,
        defaultKey = "freelance",
        name = "Freelance",
        iconKey = "ic_work",
        colorHex = "#1E88E5",
        isArchived = false,
        isDefault = false,
        sortOrder = 2,
    )

    @Test
    fun `summarize with zero income yields null savings rate and spend rate`() {
        val month = Period.Month(YearMonth.of(2026, 10))
        val expense = ExpenseWithCategory(
            expense = Expense(
                id = 1L,
                amountMinor = 15000L,
                type = ExpenseType.NEED,
                categoryId = 1L,
                occurredAt = Instant.now(),
                occurredDate = LocalDate.of(2026, 10, 5),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            category = category,
        )

        val summary = AnalyticsEngine.summarize(
            expenses = listOf(expense),
            incomes = emptyList(),
            period = month,
        )

        assertEquals(15000L, summary.totalMinor)
        assertEquals(0L, summary.incomeMinor)
        assertEquals(-15000L, summary.netMinor)
        assertNull(summary.savingsRatePct)
        assertNull(summary.spendRatePct)
        assertEquals(0, summary.incomeBySource.size)
    }

    @Test
    fun `summarize with zero expenses yields 100 percent savings rate and 0 percent spend rate`() {
        val month = Period.Month(YearMonth.of(2026, 10))
        val income = IncomeWithSource(
            income = Income(
                id = 1L,
                amountMinor = 100000L,
                sourceId = 1L,
                receivedAt = Instant.now(),
                receivedDate = LocalDate.of(2026, 10, 1),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            source = salarySource,
        )

        val summary = AnalyticsEngine.summarize(
            expenses = emptyList(),
            incomes = listOf(income),
            period = month,
        )

        assertEquals(0L, summary.totalMinor)
        assertEquals(100000L, summary.incomeMinor)
        assertEquals(100000L, summary.netMinor)
        assertEquals(100.0, summary.savingsRatePct ?: 0.0, 0.001)
        assertEquals(0.0, summary.spendRatePct ?: -1.0, 0.001)
        assertEquals(1, summary.incomeBySource.size)
        assertEquals(100.0, summary.incomeBySource[0].sharePct, 0.001)
    }

    @Test
    fun `summarize computes net savings, savings rate, spend rate, and income source shares`() {
        val month = Period.Month(YearMonth.of(2026, 10))
        val expense = ExpenseWithCategory(
            expense = Expense(
                id = 1L,
                amountMinor = 40000L,
                type = ExpenseType.NEED,
                categoryId = 1L,
                occurredAt = Instant.now(),
                occurredDate = LocalDate.of(2026, 10, 10),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            category = category,
        )

        val income1 = IncomeWithSource(
            income = Income(
                id = 1L,
                amountMinor = 80000L,
                sourceId = 1L,
                receivedAt = Instant.now(),
                receivedDate = LocalDate.of(2026, 10, 1),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            source = salarySource,
        )

        val income2 = IncomeWithSource(
            income = Income(
                id = 2L,
                amountMinor = 20000L,
                sourceId = 2L,
                receivedAt = Instant.now(),
                receivedDate = LocalDate.of(2026, 10, 15),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            source = freelanceSource,
        )

        val summary = AnalyticsEngine.summarize(
            expenses = listOf(expense),
            incomes = listOf(income1, income2),
            period = month,
        )

        assertEquals(40000L, summary.totalMinor)
        assertEquals(100000L, summary.incomeMinor)
        assertEquals(60000L, summary.netMinor)
        assertEquals(60.0, summary.savingsRatePct ?: 0.0, 0.001)
        assertEquals(40.0, summary.spendRatePct ?: 0.0, 0.001)

        // Sources sorted descending: 80k (80%), 20k (20%)
        assertEquals(2, summary.incomeBySource.size)
        assertEquals(salarySource.id, summary.incomeBySource[0].source.id)
        assertEquals(80000L, summary.incomeBySource[0].totalMinor)
        assertEquals(80.0, summary.incomeBySource[0].sharePct, 0.001)
        assertEquals(freelanceSource.id, summary.incomeBySource[1].source.id)
        assertEquals(20000L, summary.incomeBySource[1].totalMinor)
        assertEquals(20.0, summary.incomeBySource[1].sharePct, 0.001)

        // Daily income continuity (31 days in October)
        assertEquals(31, summary.dailyIncome.size)
        val day1 = summary.dailyIncome.first { it.date == LocalDate.of(2026, 10, 1) }
        val day15 = summary.dailyIncome.first { it.date == LocalDate.of(2026, 10, 15) }
        val day2 = summary.dailyIncome.first { it.date == LocalDate.of(2026, 10, 2) }
        assertEquals(80000L, day1.totalMinor)
        assertEquals(20000L, day15.totalMinor)
        assertEquals(0L, day2.totalMinor)
    }

    @Test
    fun `summarize handles net negative when spend exceeds income`() {
        val month = Period.Month(YearMonth.of(2026, 10))
        val expense = ExpenseWithCategory(
            expense = Expense(
                id = 1L,
                amountMinor = 150000L,
                type = ExpenseType.NEED,
                categoryId = 1L,
                occurredAt = Instant.now(),
                occurredDate = LocalDate.of(2026, 10, 10),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            category = category,
        )

        val income = IncomeWithSource(
            income = Income(
                id = 1L,
                amountMinor = 100000L,
                sourceId = 1L,
                receivedAt = Instant.now(),
                receivedDate = LocalDate.of(2026, 10, 1),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            source = salarySource,
        )

        val summary = AnalyticsEngine.summarize(
            expenses = listOf(expense),
            incomes = listOf(income),
            period = month,
        )

        assertEquals(150000L, summary.totalMinor)
        assertEquals(100000L, summary.incomeMinor)
        assertEquals(-50000L, summary.netMinor)
        assertEquals(-50.0, summary.savingsRatePct ?: 0.0, 0.001)
        assertEquals(150.0, summary.spendRatePct ?: 0.0, 0.001)
    }

    @Test
    fun `buildPreviousComparison computes income delta and savings rate delta points`() {
        val currentPeriod = Period.Month(YearMonth.of(2026, 10))

        val prevExpense = ExpenseWithCategory(
            expense = Expense(
                id = 1L,
                amountMinor = 50000L,
                type = ExpenseType.NEED,
                categoryId = 1L,
                occurredAt = Instant.now(),
                occurredDate = LocalDate.of(2026, 9, 10),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            category = category,
        )

        val prevIncome = IncomeWithSource(
            income = Income(
                id = 1L,
                amountMinor = 100000L,
                sourceId = 1L,
                receivedAt = Instant.now(),
                receivedDate = LocalDate.of(2026, 9, 1),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            source = salarySource,
        )

        // Previous: spend 50k, income 100k -> net 50k, savingsRate 50.0%
        // Current: spend 40k, income 120k -> net 80k, savingsRate 66.67%
        val comparison = AnalyticsEngine.buildPreviousComparison(
            currentPeriod = currentPeriod,
            previousExpenses = listOf(prevExpense),
            previousIncomes = listOf(prevIncome),
            currentTotalMinor = 40000L,
            currentWasteRatioPct = 0.0,
            currentIncomeMinor = 120000L,
            currentNetMinor = 80000L,
            currentSavingsRatePct = (80000.0 / 120000.0) * 100.0, // 66.67%
        )

        assertEquals(100000L, comparison.previousIncomeMinor)
        assertEquals(50000L, comparison.previousNetMinor)
        assertEquals(50.0, comparison.previousSavingsRatePct ?: 0.0, 0.001)

        // Income delta: ((120000 - 100000) / 100000) * 100 = 20.0%
        assertEquals(20.0, comparison.incomeDeltaPct ?: 0.0, 0.001)

        // Net delta: ((80000 - 50000) / 50000) * 100 = 60.0%
        assertEquals(60.0, comparison.netDeltaPct ?: 0.0, 0.001)

        // Savings rate points delta: 66.67 - 50.0 = 16.67 pts
        assertEquals(16.667, comparison.savingsRateDeltaPts ?: 0.0, 0.01)
    }

    @Test
    fun `calculateAvailableBalance accurately computes all-time balance`() {
        // Positive balance
        val bal1 = AnalyticsEngine.calculateAvailableBalance(
            openingBalanceMinor = 50000L,
            totalIncomeMinor = 200000L,
            totalExpenseMinor = 150000L,
        )
        assertEquals(100000L, bal1)

        // Zero balance
        val bal2 = AnalyticsEngine.calculateAvailableBalance(
            openingBalanceMinor = 0L,
            totalIncomeMinor = 100000L,
            totalExpenseMinor = 100000L,
        )
        assertEquals(0L, bal2)

        // Negative balance
        val bal3 = AnalyticsEngine.calculateAvailableBalance(
            openingBalanceMinor = 10000L,
            totalIncomeMinor = 50000L,
            totalExpenseMinor = 80000L,
        )
        assertEquals(-20000L, bal3)
    }

    @Test
    fun `calculateDaysLeftEstimate calculates days based on 14-day spending`() {
        val today = LocalDate.of(2026, 10, 15)
        // 14 days is 2026-10-02 to 2026-10-15
        val expense = ExpenseWithCategory(
            expense = Expense(
                id = 1L,
                amountMinor = 14000L, // 14,000 paise over 14 days = 1,000 paise/day avg
                type = ExpenseType.NEED,
                categoryId = 1L,
                occurredAt = Instant.now(),
                occurredDate = LocalDate.of(2026, 10, 10),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            category = category,
        )

        // 30,000 paise available / 1,000 per day = 30 days
        val days = AnalyticsEngine.calculateDaysLeftEstimate(
            availableBalanceMinor = 30000L,
            expensesLast14Days = listOf(expense),
            today = today,
        )
        assertEquals(30, days)

        // Balance <= 0 -> null
        assertNull(AnalyticsEngine.calculateDaysLeftEstimate(0L, listOf(expense), today))
        assertNull(AnalyticsEngine.calculateDaysLeftEstimate(-5000L, listOf(expense), today))

        // Spending == 0 -> null
        assertNull(AnalyticsEngine.calculateDaysLeftEstimate(30000L, emptyList(), today))

        // Expense outside 14 days -> ignored
        val oldExpense = ExpenseWithCategory(
            expense = Expense(
                id = 2L,
                amountMinor = 20000L,
                type = ExpenseType.NEED,
                categoryId = 1L,
                occurredAt = Instant.now(),
                occurredDate = LocalDate.of(2026, 9, 20),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            category = category,
        )
        assertNull(AnalyticsEngine.calculateDaysLeftEstimate(30000L, listOf(oldExpense), today))
    }
}
