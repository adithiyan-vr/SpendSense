/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.BudgetStatus
import com.supernovea.spendsense.domain.model.Insight
import com.supernovea.spendsense.domain.model.InsightSeverity
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.GetPeriodSummaryUseCase
import com.supernovea.spendsense.domain.usecase.PeriodSummaryResult
import com.supernovea.spendsense.feature.analysis.components.ChartType
import com.supernovea.spendsense.feature.analysis.components.DateRangePreset
import com.supernovea.spendsense.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class AnalysisViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getPeriodSummaryUseCase = mockk<GetPeriodSummaryUseCase>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val expenseRepository = mockk<ExpenseRepository>()
    private val incomeRepository = mockk<IncomeRepository>()

    private val fixedToday = LocalDate.of(2026, 10, 12) // Monday
    private val userSettings = UserSettings(
        weekStartDay = WeekStartDay.MONDAY,
    )

    private val weekSummary = PeriodSummary(
        period = Period.Week(fixedToday),
        totalMinor = 142000L,
        needMinor = 98000L,
        wasteMinor = 44000L,
        wasteRatioPct = 31.0,
        needRatioPct = 69.0,
        avgDailyMinor = 20285L,
        peakDay = null,
        dailyTotals = emptyList(),
        weeklyTotals = emptyList(),
        topCategories = emptyList(),
        previous = null,
        cleanDays = 2,
    )

    private val monthSummary = PeriodSummary(
        period = Period.Month(YearMonth.of(2026, 10)),
        totalMinor = 450000L,
        needMinor = 310000L,
        wasteMinor = 140000L,
        wasteRatioPct = 31.1,
        needRatioPct = 68.9,
        avgDailyMinor = 37500L,
        peakDay = null,
        dailyTotals = emptyList(),
        weeklyTotals = emptyList(),
        topCategories = emptyList(),
        previous = null,
        cleanDays = 4,
    )

    private val customSummary = PeriodSummary(
        period = Period.Custom(fixedToday.minusDays(6), fixedToday),
        totalMinor = 200000L,
        needMinor = 150000L,
        wasteMinor = 50000L,
        wasteRatioPct = 25.0,
        needRatioPct = 75.0,
        avgDailyMinor = 28571L,
        peakDay = null,
        dailyTotals = emptyList(),
        weeklyTotals = emptyList(),
        topCategories = emptyList(),
        previous = null,
        cleanDays = 1,
    )

    private val insight = Insight(
        id = "test_insight",
        title = "Clean day streak",
        message = "2 clean days this week",
        severity = InsightSeverity.POSITIVE,
    )

    private val budgetStatus = BudgetStatus(
        budget = Budget(monthKey = "2026-10", totalLimitMinor = 500000L),
        totalSpentMinor = 450000L,
        wasteSpentMinor = 140000L,
        totalProgressPct = 90.0,
        wasteProgressPct = null,
        isOverTotal = false,
        isOverWaste = false,
        daysRemainingInMonth = 19,
    )

    @Before
    fun setUp() {
        every { settingsRepository.observeSettings() } returns flowOf(userSettings)
        every { expenseRepository.observeBetween(any(), any()) } returns flowOf(emptyList())
        every { incomeRepository.observeBetween(any(), any()) } returns flowOf(emptyList())

        every { getPeriodSummaryUseCase.invoke(match { it is Period.Week }, any()) } returns flowOf(
            PeriodSummaryResult(
                summary = weekSummary,
                insights = listOf(insight),
                budgetStatus = null,
            )
        )
        every { getPeriodSummaryUseCase.invoke(match { it is Period.Month }, any()) } returns flowOf(
            PeriodSummaryResult(
                summary = monthSummary,
                insights = listOf(insight),
                budgetStatus = budgetStatus,
            )
        )
        every { getPeriodSummaryUseCase.invoke(match { it is Period.Custom }, any()) } returns flowOf(
            PeriodSummaryResult(
                summary = customSummary,
                insights = listOf(insight),
                budgetStatus = null,
            )
        )
    }

    private fun createViewModel(): AnalysisViewModel {
        return AnalysisViewModel(
            getPeriodSummaryUseCase = getPeriodSummaryUseCase,
            settingsRepository = settingsRepository,
            expenseRepository = expenseRepository,
            incomeRepository = incomeRepository,
        ).apply {
            today = fixedToday
        }
    }

    @Test
    fun `initial state defaults to current week with week summary and canNavigateNext false`() = runTest {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(AnalysisPeriodMode.WEEK, state.mode)
        assertFalse(state.canNavigateNext)
        assertNotNull(state.summary)
        assertEquals(142000L, state.summary?.totalMinor)
        assertEquals(31.0, state.summary?.wasteRatioPct ?: 0.0, 0.01)
        assertEquals(1, state.insights.size)
        assertNull(state.selectedBarIndex)
        assertFalse(state.isTableVisible)
    }

    @Test
    fun `selecting Month mode changes mode and emits month summary with budget progress`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(AnalysisEvent.SelectPeriodMode(AnalysisPeriodMode.MONTH))

        val state = viewModel.uiState.value
        assertEquals(AnalysisPeriodMode.MONTH, state.mode)
        assertFalse(state.canNavigateNext)
        assertEquals(450000L, state.summary?.totalMinor)
        assertNotNull(state.budgetStatus)
        assertEquals(90.0, state.budgetStatus?.totalProgressPct ?: 0.0, 0.01)
    }

    @Test
    fun `selecting Custom mode and preset updates date range`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(AnalysisEvent.SelectPeriodMode(AnalysisPeriodMode.CUSTOM))

        assertEquals(AnalysisPeriodMode.CUSTOM, viewModel.uiState.value.mode)

        viewModel.onEvent(AnalysisEvent.SelectPreset(DateRangePreset.LAST_7_DAYS))
        val state = viewModel.uiState.value
        assertEquals(DateRangePreset.LAST_7_DAYS, state.selectedPreset)
        assertEquals(fixedToday.minusDays(6), state.customStartDate)
        assertEquals(fixedToday, state.customEndDate)
    }

    @Test
    fun `selecting Custom date range directly updates custom range and marks preset as CUSTOM`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(AnalysisEvent.SelectPeriodMode(AnalysisPeriodMode.CUSTOM))

        val start = fixedToday.minusDays(14)
        val end = fixedToday.minusDays(1)
        viewModel.onEvent(AnalysisEvent.SelectCustomDateRange(start, end))

        val state = viewModel.uiState.value
        assertEquals(DateRangePreset.CUSTOM, state.selectedPreset)
        assertEquals(start, state.customStartDate)
        assertEquals(end, state.customEndDate)
        assertFalse(state.showDateRangePicker)
        assertTrue(state.canNavigateNext) // end is before today
    }

    @Test
    fun `open and dismiss date range picker dialog toggles showDateRangePicker`() = runTest {
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.showDateRangePicker)

        viewModel.onEvent(AnalysisEvent.OpenDateRangePicker)
        assertTrue(viewModel.uiState.value.showDateRangePicker)

        viewModel.onEvent(AnalysisEvent.DismissDateRangePicker)
        assertFalse(viewModel.uiState.value.showDateRangePicker)
    }

    @Test
    fun `open and dismiss export dialog toggles showExportDialog`() = runTest {
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.showExportDialog)

        viewModel.onEvent(AnalysisEvent.OpenExportDialog)
        assertTrue(viewModel.uiState.value.showExportDialog)

        viewModel.onEvent(AnalysisEvent.DismissExportDialog)
        assertFalse(viewModel.uiState.value.showExportDialog)
    }

    @Test
    fun `getExportFileName formats correctly for PDF and CSV`() = runTest {
        val viewModel = createViewModel()
        val pdfName = viewModel.getExportFileName(isPdf = true)
        val csvName = viewModel.getExportFileName(isPdf = false)

        assertTrue(pdfName.startsWith("SpendSense_Report_"))
        assertTrue(pdfName.endsWith(".pdf"))

        assertTrue(csvName.startsWith("SpendSense_Report_"))
        assertTrue(csvName.endsWith(".csv"))
    }

    @Test
    fun `selecting chart type updates selectedChartType`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(AnalysisEvent.SelectChartType(ChartType.LINE))
        assertEquals(ChartType.LINE, viewModel.uiState.value.selectedChartType)

        viewModel.onEvent(AnalysisEvent.SelectChartType(ChartType.BAR))
        assertEquals(ChartType.BAR, viewModel.uiState.value.selectedChartType)
    }

    @Test
    fun `selecting category filter updates filter and clears pie selection`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(AnalysisEvent.SelectPieSlice("cat_123"))
        assertEquals("cat_123", viewModel.uiState.value.selectedPieSliceId)

        viewModel.onEvent(AnalysisEvent.SelectCategoryTypeFilter(com.supernovea.spendsense.domain.model.ExpenseType.NEED))
        assertEquals(com.supernovea.spendsense.domain.model.ExpenseType.NEED, viewModel.uiState.value.selectedCategoryTypeFilter)
        assertNull(viewModel.uiState.value.selectedPieSliceId)
    }

    @Test
    fun `changing period mode clears all chart selections`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(AnalysisEvent.SelectBar(2))
        viewModel.onEvent(AnalysisEvent.SelectPieSlice("slice_1"))
        viewModel.onEvent(AnalysisEvent.SelectLinePoint(3))

        viewModel.onEvent(AnalysisEvent.SelectPeriodMode(AnalysisPeriodMode.MONTH))

        assertNull(viewModel.uiState.value.selectedBarIndex)
        assertNull(viewModel.uiState.value.selectedPieSliceId)
        assertNull(viewModel.uiState.value.selectedLinePointIndex)
    }
}
