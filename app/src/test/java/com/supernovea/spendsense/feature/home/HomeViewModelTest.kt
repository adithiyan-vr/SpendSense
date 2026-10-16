/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.home

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.BudgetStatus
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.Insight
import com.supernovea.spendsense.domain.model.InsightSeverity
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.GetExpensesUseCase
import com.supernovea.spendsense.domain.usecase.GetIncomesUseCase
import com.supernovea.spendsense.domain.usecase.GetPeriodSummaryUseCase
import com.supernovea.spendsense.domain.usecase.PeriodSummaryResult
import com.supernovea.spendsense.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getPeriodSummaryUseCase = mockk<GetPeriodSummaryUseCase>()
    private val getExpensesUseCase = mockk<GetExpensesUseCase>()
    private val getIncomesUseCase = mockk<GetIncomesUseCase>()
    private val settingsRepository = mockk<SettingsRepository>()

    private val userSettings = UserSettings(
        weekStartDay = WeekStartDay.MONDAY,
    )

    private val foodCategory = Category(
        id = 1L,
        name = "Food",
        iconKey = "restaurant",
        colorHex = "#F59E0B",
        defaultType = ExpenseType.NEED,
        isDefault = true,
    )

    private val testExpense = Expense(
        id = 1L,
        amountMinor = 15000L,
        type = ExpenseType.NEED,
        categoryId = 1L,
        note = "Lunch",
        occurredAt = Instant.now(),
        occurredDate = LocalDate.now(),
        paymentMethod = PaymentMethod.UPI,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
    )

    private val expenseWithCategory = ExpenseWithCategory(testExpense, foodCategory)

    private val monthSummary = PeriodSummary(
        period = Period.Month(java.time.YearMonth.now()),
        totalMinor = 50000L,
        needMinor = 35000L,
        wasteMinor = 15000L,
        wasteRatioPct = 30.0,
        needRatioPct = 70.0,
        avgDailyMinor = 1600L,
        peakDay = null,
        dailyTotals = emptyList(),
        weeklyTotals = emptyList(),
        topCategories = emptyList(),
        previous = null,
        cleanDays = 2,
    )

    private val weekSummary = PeriodSummary(
        period = Period.Week(LocalDate.now()),
        totalMinor = 20000L,
        needMinor = 15000L,
        wasteMinor = 5000L,
        wasteRatioPct = 25.0,
        needRatioPct = 75.0,
        avgDailyMinor = 2800L,
        peakDay = null,
        dailyTotals = emptyList(),
        weeklyTotals = emptyList(),
        topCategories = emptyList(),
        previous = null,
        cleanDays = 1,
    )

    private val insight = Insight(
        id = "test_insight",
        title = "Great progress",
        message = "Waste is low this week",
        severity = InsightSeverity.POSITIVE,
    )

    private val budgetStatus = BudgetStatus(
        budget = Budget(monthKey = "2026-10", totalLimitMinor = 100000L),
        totalSpentMinor = 50000L,
        wasteSpentMinor = 15000L,
        totalProgressPct = 50.0,
        wasteProgressPct = null,
        isOverTotal = false,
        isOverWaste = false,
        daysRemainingInMonth = 19,
    )

    @Before
    fun setUp() {
        every { settingsRepository.observeSettings() } returns flowOf(userSettings)
        every { getPeriodSummaryUseCase.invoke(match { it is Period.Month }, any()) } returns flowOf(
            PeriodSummaryResult(
                summary = monthSummary,
                insights = listOf(insight),
                budgetStatus = budgetStatus,
            )
        )
        every { getPeriodSummaryUseCase.invoke(match { it is Period.Week }, any()) } returns flowOf(
            PeriodSummaryResult(
                summary = weekSummary,
                insights = emptyList(),
                budgetStatus = null,
            )
        )
        every { getExpensesUseCase.getBetween(any(), any()) } returns flowOf(listOf(expenseWithCategory))
        every { getExpensesUseCase.getAll() } returns flowOf(listOf(expenseWithCategory))
        every { getIncomesUseCase.observeBetween(any(), any()) } returns flowOf(emptyList())
        every { getIncomesUseCase.observeAll() } returns flowOf(emptyList())
        every { getIncomesUseCase.observeTotalAllTime() } returns flowOf(0L)
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            getPeriodSummaryUseCase = getPeriodSummaryUseCase,
            getExpensesUseCase = getExpensesUseCase,
            getIncomesUseCase = getIncomesUseCase,
            settingsRepository = settingsRepository,
        )
    }

    @Test
    fun `loads home data successfully with summaries, insights, and budget status`() = runTest {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertTrue(state.hasAnyExpenses)
        assertNotNull(state.monthSummary)
        assertEquals(50000L, state.monthSummary?.totalMinor)
        assertEquals(30.0, state.monthSummary?.wasteRatioPct ?: 0.0, 0.01)

        // Today spend
        assertEquals(15000L, state.todaySpend.totalMinor)

        // Week spend
        assertEquals(20000L, state.weekSpend.totalMinor)
        assertEquals(25.0, state.weekSpend.wasteRatioPct, 0.01)

        // Top Insight & Budget Status
        assertEquals("Great progress", state.topInsight?.title)
        assertNotNull(state.budgetStatus)
        assertEquals(50.0, state.budgetStatus?.totalProgressPct ?: 0.0, 0.01)

        // Recent expenses
        assertEquals(1, state.recentExpenses.size)
        assertEquals(1L, state.recentExpenses[0].expense.id)
    }

    @Test
    fun `when no expenses exist, hasAnyExpenses is false`() = runTest {
        every { getExpensesUseCase.getAll() } returns flowOf(emptyList())
        every { getExpensesUseCase.getBetween(any(), any()) } returns flowOf(emptyList())

        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.hasAnyExpenses)
        assertTrue(state.recentExpenses.isEmpty())
    }
}
