/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis

import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.feature.analysis.components.ChartType
import com.supernovea.spendsense.feature.analysis.components.DateRangePreset
import java.time.LocalDate

sealed interface AnalysisEvent {
    data class SelectPeriodMode(val mode: AnalysisPeriodMode) : AnalysisEvent
    data object NavigatePrevious : AnalysisEvent
    data object NavigateNext : AnalysisEvent
    data class SelectChartType(val chartType: ChartType) : AnalysisEvent
    data class SelectCategoryTypeFilter(val type: ExpenseType?) : AnalysisEvent
    data class SelectPieSlice(val sliceId: String?) : AnalysisEvent
    data class SelectLinePoint(val index: Int?) : AnalysisEvent
    data class SelectBar(val index: Int?) : AnalysisEvent
    data object ToggleTableView : AnalysisEvent

    data class SelectPreset(val preset: DateRangePreset) : AnalysisEvent
    data class SelectCustomDateRange(val startDate: LocalDate, val endDate: LocalDate) : AnalysisEvent
    data object OpenDateRangePicker : AnalysisEvent
    data object DismissDateRangePicker : AnalysisEvent
    data object OpenExportDialog : AnalysisEvent
    data object DismissExportDialog : AnalysisEvent
    data class UserMessage(val message: String?) : AnalysisEvent
}
