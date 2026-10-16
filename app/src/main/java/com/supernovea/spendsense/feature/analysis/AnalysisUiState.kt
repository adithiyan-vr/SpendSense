/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis

import com.supernovea.spendsense.domain.model.BudgetStatus
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.Insight
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.UserProfileType
import com.supernovea.spendsense.feature.analysis.components.ChartType
import com.supernovea.spendsense.feature.analysis.components.DateRangePreset
import com.supernovea.spendsense.feature.analysis.components.TrendSeriesData
import java.time.LocalDate

enum class AnalysisPeriodMode {
    WEEK,
    MONTH,
    CUSTOM,
}

data class AnalysisUiState(
    val mode: AnalysisPeriodMode = AnalysisPeriodMode.WEEK,
    val anchorDate: LocalDate = LocalDate.now(),
    val customStartDate: LocalDate = LocalDate.now().minusDays(29),
    val customEndDate: LocalDate = LocalDate.now(),
    val selectedPreset: DateRangePreset? = DateRangePreset.LAST_30_DAYS,
    val period: Period = Period.Week(anchorDate),
    val periodLabel: String = "",
    val canNavigateNext: Boolean = false,
    val summary: PeriodSummary? = null,
    val insights: List<Insight> = emptyList(),
    val budgetStatus: BudgetStatus? = null,
    val isLoading: Boolean = true,
    val selectedChartType: ChartType = ChartType.PIE,
    val selectedCategoryTypeFilter: ExpenseType? = null,
    val selectedPieSliceId: String? = null,
    val selectedLinePointIndex: Int? = null,
    val selectedBarIndex: Int? = null,
    val trendSeriesData: TrendSeriesData = TrendSeriesData(
        points = emptyList(),
        maxYMinor = 0L,
        niceYTicks = listOf(0L),
        hasIncomeData = false,
        hasExpenseData = false,
        isSingleDataPoint = false,
    ),
    val isTableVisible: Boolean = false,
    val showDateRangePicker: Boolean = false,
    val showExportDialog: Boolean = false,
    val isExporting: Boolean = false,
    val userMessage: String? = null,
    val userProfile: UserProfileType = UserProfileType.STUDENT,
    val currencySymbol: String = "₹",
)
