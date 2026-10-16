/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.domain.analytics.PeriodCalculator
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.GetPeriodSummaryUseCase
import com.supernovea.spendsense.feature.analysis.components.ChartType
import com.supernovea.spendsense.feature.analysis.components.DateRangePreset
import com.supernovea.spendsense.feature.analysis.components.TrendSeriesBuilder
import com.supernovea.spendsense.feature.analysis.export.CsvReportGenerator
import com.supernovea.spendsense.feature.analysis.export.PdfReportGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val getPeriodSummaryUseCase: GetPeriodSummaryUseCase,
    private val settingsRepository: SettingsRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
) : ViewModel() {

    internal var today: LocalDate = LocalDate.now()
        set(value) {
            field = value
            _anchorDate.value = value
            _customEndDate.value = value
            _customStartDate.value = value.minusDays(29)
        }

    private val _mode = MutableStateFlow(AnalysisPeriodMode.WEEK)
    private val _anchorDate = MutableStateFlow(today)
    private val _customStartDate = MutableStateFlow(today.minusDays(29))
    private val _customEndDate = MutableStateFlow(today)
    private val _selectedPreset = MutableStateFlow<DateRangePreset?>(DateRangePreset.LAST_30_DAYS)

    private val _selectedChartType = MutableStateFlow(ChartType.PIE)
    private val _selectedCategoryTypeFilter = MutableStateFlow<ExpenseType?>(null)
    private val _selectedPieSliceId = MutableStateFlow<String?>(null)
    private val _selectedLinePointIndex = MutableStateFlow<Int?>(null)
    private val _selectedBarIndex = MutableStateFlow<Int?>(null)
    private val _isTableVisible = MutableStateFlow(false)

    private val _showDateRangePicker = MutableStateFlow(false)
    private val _showExportDialog = MutableStateFlow(false)
    private val _isExporting = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(AnalysisUiState(isLoading = true))
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    private data class PeriodConfig(
        val mode: AnalysisPeriodMode,
        val anchorDate: LocalDate,
        val customStartDate: LocalDate,
        val customEndDate: LocalDate,
        val selectedPreset: DateRangePreset?,
        val settings: UserSettings,
    )

    private data class ChartSelection(
        val chartType: ChartType,
        val categoryFilter: ExpenseType?,
        val pieSliceId: String?,
        val linePointIndex: Int?,
    )

    private data class DialogsState(
        val showDateRangePicker: Boolean,
        val showExportDialog: Boolean,
        val isExporting: Boolean,
        val userMessage: String?,
    )

    private data class UiControlsState(
        val chart: ChartSelection,
        val barIndex: Int?,
        val isTable: Boolean,
        val dialogs: DialogsState,
    )

    private val chartSelectionsFlow = combine(
        _selectedChartType,
        _selectedCategoryTypeFilter,
        _selectedPieSliceId,
        _selectedLinePointIndex,
    ) { chartType, categoryFilter, pieSliceId, linePointIndex ->
        ChartSelection(chartType, categoryFilter, pieSliceId, linePointIndex)
    }

    private val dialogsFlow = combine(
        _showDateRangePicker,
        _showExportDialog,
        _isExporting,
        _userMessage,
    ) { showDatePicker, showExport, isExporting, userMessage ->
        DialogsState(showDatePicker, showExport, isExporting, userMessage)
    }

    private val uiControlsFlow = combine(
        chartSelectionsFlow,
        _selectedBarIndex,
        _isTableVisible,
        dialogsFlow,
    ) { chart, barIndex, isTable, dialogs ->
        UiControlsState(chart, barIndex, isTable, dialogs)
    }

    private data class CustomRangeState(
        val startDate: LocalDate,
        val endDate: LocalDate,
        val preset: DateRangePreset?,
    )

    private val customRangeFlow = combine(
        _customStartDate,
        _customEndDate,
        _selectedPreset,
    ) { start, end, preset ->
        CustomRangeState(start, end, preset)
    }

    init {
        viewModelScope.launch {
            combine(
                _mode,
                _anchorDate,
                customRangeFlow,
                settingsRepository.observeSettings(),
            ) { mode, anchor, customRange, settings ->
                PeriodConfig(mode, anchor, customRange.startDate, customRange.endDate, customRange.preset, settings)
            }.flatMapLatest { config ->
                val period: Period = when (config.mode) {
                    AnalysisPeriodMode.WEEK -> PeriodCalculator.getWeekPeriod(config.anchorDate, config.settings.weekStartDay)
                    AnalysisPeriodMode.MONTH -> PeriodCalculator.getMonthPeriod(config.anchorDate)
                    AnalysisPeriodMode.CUSTOM -> Period.Custom(config.customStartDate, config.customEndDate)
                }

                val label = PeriodCalculator.formatPeriodLabel(period)

                val canNavigateNext = when (period) {
                    is Period.Week -> {
                        val todayWeek = PeriodCalculator.getWeekPeriod(today, config.settings.weekStartDay)
                        period.start < todayWeek.start
                    }
                    is Period.Month -> {
                        val todayMonth = YearMonth.from(today)
                        period.ym < todayMonth
                    }
                    is Period.Custom -> {
                        period.endDate < today
                    }
                }

                combine(
                    getPeriodSummaryUseCase(period, today),
                    uiControlsFlow,
                ) { result, controls ->
                    val trendSeries = TrendSeriesBuilder.build(result.summary, config.mode)

                    AnalysisUiState(
                        mode = config.mode,
                        anchorDate = config.anchorDate,
                        customStartDate = config.customStartDate,
                        customEndDate = config.customEndDate,
                        selectedPreset = config.selectedPreset,
                        period = period,
                        periodLabel = label,
                        canNavigateNext = canNavigateNext,
                        summary = result.summary,
                        insights = result.insights,
                        budgetStatus = result.budgetStatus,
                        isLoading = false,
                        selectedChartType = controls.chart.chartType,
                        selectedCategoryTypeFilter = controls.chart.categoryFilter,
                        selectedPieSliceId = controls.chart.pieSliceId,
                        selectedLinePointIndex = controls.chart.linePointIndex,
                        selectedBarIndex = controls.barIndex,
                        trendSeriesData = trendSeries,
                        isTableVisible = controls.isTable,
                        showDateRangePicker = controls.dialogs.showDateRangePicker,
                        showExportDialog = controls.dialogs.showExportDialog,
                        isExporting = controls.dialogs.isExporting,
                        userMessage = controls.dialogs.userMessage,
                        userProfile = config.settings.userProfile,
                        currencySymbol = config.settings.currencySymbol,
                    )
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onEvent(event: AnalysisEvent) {
        when (event) {
            is AnalysisEvent.SelectPeriodMode -> {
                _mode.value = event.mode
                _selectedBarIndex.value = null
                _selectedPieSliceId.value = null
                _selectedLinePointIndex.value = null
            }
            AnalysisEvent.NavigatePrevious -> {
                _selectedBarIndex.value = null
                _selectedPieSliceId.value = null
                _selectedLinePointIndex.value = null
                when (_mode.value) {
                    AnalysisPeriodMode.WEEK -> _anchorDate.value = _anchorDate.value.minusWeeks(1)
                    AnalysisPeriodMode.MONTH -> _anchorDate.value = _anchorDate.value.minusMonths(1)
                    AnalysisPeriodMode.CUSTOM -> {
                        val currentPeriod = Period.Custom(_customStartDate.value, _customEndDate.value)
                        val prev = PeriodCalculator.getPreviousPeriod(currentPeriod) as Period.Custom
                        _customStartDate.value = prev.startDate
                        _customEndDate.value = prev.endDate
                        _selectedPreset.value = null
                    }
                }
            }
            AnalysisEvent.NavigateNext -> {
                val state = uiState.value
                if (state.canNavigateNext) {
                    _selectedBarIndex.value = null
                    _selectedPieSliceId.value = null
                    _selectedLinePointIndex.value = null
                    when (_mode.value) {
                        AnalysisPeriodMode.WEEK -> _anchorDate.value = _anchorDate.value.plusWeeks(1)
                        AnalysisPeriodMode.MONTH -> _anchorDate.value = _anchorDate.value.plusMonths(1)
                        AnalysisPeriodMode.CUSTOM -> {
                            val currentPeriod = Period.Custom(_customStartDate.value, _customEndDate.value)
                            val next = PeriodCalculator.getNextPeriod(currentPeriod) as Period.Custom
                            val clampedEnd = if (next.endDate.isAfter(today)) today else next.endDate
                            _customStartDate.value = next.startDate
                            _customEndDate.value = clampedEnd
                            _selectedPreset.value = null
                        }
                    }
                }
            }
            is AnalysisEvent.SelectChartType -> {
                _selectedChartType.value = event.chartType
            }
            is AnalysisEvent.SelectCategoryTypeFilter -> {
                _selectedCategoryTypeFilter.value = event.type
                _selectedPieSliceId.value = null
            }
            is AnalysisEvent.SelectPieSlice -> {
                _selectedPieSliceId.value = event.sliceId
            }
            is AnalysisEvent.SelectLinePoint -> {
                _selectedLinePointIndex.value = event.index
            }
            is AnalysisEvent.SelectBar -> {
                _selectedBarIndex.value = event.index
            }
            AnalysisEvent.ToggleTableView -> {
                _isTableVisible.value = !_isTableVisible.value
            }
            is AnalysisEvent.SelectPreset -> {
                _selectedPreset.value = event.preset
                if (event.preset == DateRangePreset.CUSTOM) {
                    _showDateRangePicker.value = true
                } else {
                    val range = event.preset.calculateRange(today)
                    if (range != null) {
                        _customStartDate.value = range.startDate
                        _customEndDate.value = range.endDate
                    }
                }
            }
            is AnalysisEvent.SelectCustomDateRange -> {
                _customStartDate.value = event.startDate
                _customEndDate.value = event.endDate
                _selectedPreset.value = DateRangePreset.CUSTOM
                _showDateRangePicker.value = false
            }
            AnalysisEvent.OpenDateRangePicker -> {
                _showDateRangePicker.value = true
            }
            AnalysisEvent.DismissDateRangePicker -> {
                _showDateRangePicker.value = false
            }
            AnalysisEvent.OpenExportDialog -> {
                _showExportDialog.value = true
            }
            AnalysisEvent.DismissExportDialog -> {
                _showExportDialog.value = false
            }
            is AnalysisEvent.UserMessage -> {
                _userMessage.value = event.message
            }
        }
    }

    suspend fun exportCsv(includeNotes: Boolean): String = withContext(Dispatchers.IO) {
        val range = PeriodCalculator.getDateRange(uiState.value.period)
        val expenses = expenseRepository.observeBetween(range.start, range.end).first()
        val incomes = incomeRepository.observeBetween(range.start, range.end).first()
        CsvReportGenerator.generate(expenses, incomes, includeNotes)
    }

    suspend fun exportPdf(outputStream: OutputStream): Unit = withContext(Dispatchers.IO) {
        val state = uiState.value
        val summary = state.summary ?: return@withContext
        val range = PeriodCalculator.getDateRange(state.period)
        PdfReportGenerator.generate(
            outputStream = outputStream,
            summary = summary,
            insights = state.insights,
            userProfile = state.userProfile,
            currencySymbol = state.currencySymbol,
            periodLabel = state.periodLabel,
            startDate = range.start,
            endDate = range.end,
        )
    }

    fun getExportFileName(isPdf: Boolean): String {
        val range = PeriodCalculator.getDateRange(uiState.value.period)
        val ext = if (isPdf) "pdf" else "csv"
        return "SpendSense_Report_${range.start}_to_${range.end}.$ext"
    }

    suspend fun prepareShareFile(context: Context, isPdf: Boolean, includeNotes: Boolean): Uri = withContext(Dispatchers.IO) {
        val exportDir = File(context.cacheDir, "exports").apply { if (!exists()) mkdirs() }
        val fileName = getExportFileName(isPdf)
        val file = File(exportDir, fileName)

        if (isPdf) {
            file.outputStream().use { os ->
                exportPdf(os)
            }
        } else {
            val csv = exportCsv(includeNotes)
            file.writeText(csv, Charsets.UTF_8)
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    }
}
