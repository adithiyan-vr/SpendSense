/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.ui.CategoryIconProvider
import com.supernovea.spendsense.core.ui.EmptyState
import com.supernovea.spendsense.core.ui.InsightCard
import com.supernovea.spendsense.core.ui.NeedWasteBar
import com.supernovea.spendsense.core.ui.PieChart
import com.supernovea.spendsense.core.ui.PieInputItem
import com.supernovea.spendsense.core.ui.PieSliceBuilder
import com.supernovea.spendsense.feature.analysis.components.AnalysisBudgetProgress
import com.supernovea.spendsense.feature.analysis.components.AnalysisCategoryBreakdown
import com.supernovea.spendsense.feature.analysis.components.AnalysisDataTable
import com.supernovea.spendsense.feature.analysis.components.AnalysisDateRangePickerDialog
import com.supernovea.spendsense.feature.analysis.components.AnalysisLineChart
import com.supernovea.spendsense.feature.analysis.components.AnalysisStackedBarChart
import com.supernovea.spendsense.feature.analysis.components.AnalysisStatsRow
import com.supernovea.spendsense.feature.analysis.components.AnalysisSummaryHeader
import com.supernovea.spendsense.feature.analysis.components.ChartType
import com.supernovea.spendsense.feature.analysis.components.ChartTypeSwitcher
import com.supernovea.spendsense.feature.analysis.components.DateRangePresetChips
import com.supernovea.spendsense.feature.analysis.components.ExportReportDialog
import com.supernovea.spendsense.feature.analysis.components.PeriodNavigator
import com.supernovea.spendsense.feature.analysis.components.PeriodSegmentedToggle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalysisViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var pendingExportIsPdf by remember { mutableStateOf(true) }
    var pendingExportIncludeNotes by remember { mutableStateOf(false) }

    val pdfSaveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        viewModel.exportPdf(os)
                    }
                    viewModel.onEvent(AnalysisEvent.UserMessage(context.getString(R.string.export_success_saved)))
                } catch (e: Exception) {
                    viewModel.onEvent(AnalysisEvent.UserMessage(context.getString(R.string.export_failed, e.message ?: "")))
                }
            }
        }
    }

    val csvSaveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val csv = viewModel.exportCsv(pendingExportIncludeNotes)
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(csv.toByteArray(Charsets.UTF_8))
                    }
                    viewModel.onEvent(AnalysisEvent.UserMessage(context.getString(R.string.export_success_saved)))
                } catch (e: Exception) {
                    viewModel.onEvent(AnalysisEvent.UserMessage(context.getString(R.string.export_failed, e.message ?: "")))
                }
            }
        }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.onEvent(AnalysisEvent.UserMessage(null))
        }
    }

    if (uiState.showDateRangePicker) {
        AnalysisDateRangePickerDialog(
            initialStartDate = uiState.customStartDate,
            initialEndDate = uiState.customEndDate,
            onDateRangeSelected = { start, end ->
                viewModel.onEvent(AnalysisEvent.SelectCustomDateRange(start, end))
            },
            onDismiss = {
                viewModel.onEvent(AnalysisEvent.DismissDateRangePicker)
            },
        )
    }

    if (uiState.showExportDialog) {
        ExportReportDialog(
            periodLabel = uiState.periodLabel,
            onSaveToDevice = { isPdf, includeNotes ->
                pendingExportIsPdf = isPdf
                pendingExportIncludeNotes = includeNotes
                val fileName = viewModel.getExportFileName(isPdf)
                if (isPdf) {
                    pdfSaveLauncher.launch(fileName)
                } else {
                    csvSaveLauncher.launch(fileName)
                }
                viewModel.onEvent(AnalysisEvent.DismissExportDialog)
            },
            onShareReport = { isPdf, includeNotes ->
                scope.launch {
                    try {
                        val uri = viewModel.prepareShareFile(context, isPdf, includeNotes)
                        val mime = if (isPdf) "application/pdf" else "text/csv"
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = mime
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.export_action_share)).apply {
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(chooser)
                        viewModel.onEvent(AnalysisEvent.DismissExportDialog)
                        viewModel.onEvent(AnalysisEvent.UserMessage(context.getString(R.string.export_success_shared)))
                    } catch (e: Exception) {
                        viewModel.onEvent(AnalysisEvent.UserMessage(context.getString(R.string.export_failed, e.message ?: "")))
                    }
                }
            },
            onDismiss = {
                viewModel.onEvent(AnalysisEvent.DismissExportDialog)
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.analysis_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    val canExport = uiState.summary != null && (uiState.summary!!.totalMinor > 0L || uiState.summary!!.incomeMinor > 0L)
                    IconButton(
                        onClick = { viewModel.onEvent(AnalysisEvent.OpenExportDialog) },
                        enabled = canExport,
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = stringResource(id = R.string.cd_export_report),
                            tint = if (canExport) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Period Toggle and Navigator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                PeriodSegmentedToggle(
                    selectedMode = uiState.mode,
                    onModeSelected = { mode ->
                        viewModel.onEvent(AnalysisEvent.SelectPeriodMode(mode))
                    },
                )

                if (uiState.mode == AnalysisPeriodMode.CUSTOM) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DateRangePresetChips(
                        selectedPreset = uiState.selectedPreset,
                        onPresetSelected = { preset ->
                            viewModel.onEvent(AnalysisEvent.SelectPreset(preset))
                        },
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                PeriodNavigator(
                    periodLabel = uiState.periodLabel,
                    canNavigateNext = uiState.canNavigateNext,
                    onPreviousClick = {
                        viewModel.onEvent(AnalysisEvent.NavigatePrevious)
                    },
                    onNextClick = {
                        viewModel.onEvent(AnalysisEvent.NavigateNext)
                    },
                    onLabelClick = if (uiState.mode == AnalysisPeriodMode.CUSTOM) {
                        { viewModel.onEvent(AnalysisEvent.OpenDateRangePicker) }
                    } else null,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                val summary = uiState.summary
                if (summary == null || (summary.totalMinor == 0L && summary.incomeMinor == 0L)) {
                    val periodModeName = when (uiState.mode) {
                        AnalysisPeriodMode.WEEK -> stringResource(id = R.string.period_week).lowercase()
                        AnalysisPeriodMode.MONTH -> stringResource(id = R.string.period_month).lowercase()
                        AnalysisPeriodMode.CUSTOM -> stringResource(id = R.string.period_custom).lowercase()
                    }

                    EmptyState(
                        icon = Icons.Default.PieChart,
                        title = stringResource(id = R.string.analysis_empty_title, periodModeName),
                        subtitle = stringResource(id = R.string.analysis_empty_subtitle),
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // 1. Summary Header Card
                        item {
                            AnalysisSummaryHeader(
                                summary = summary,
                                mode = uiState.mode,
                            )
                        }

                        // 2. Need vs Waste Ratio Bar
                        if (summary.totalMinor > 0L) {
                            item {
                                NeedWasteBar(
                                    needMinor = summary.needMinor,
                                    wasteMinor = summary.wasteMinor,
                                )
                            }
                        }

                        // 3. Chart Type Switcher Chips [ Pie | Line | Bar ] & Sub-filter [ All | Need | Waste ]
                        item {
                            ChartTypeSwitcher(
                                selectedChartType = uiState.selectedChartType,
                                onChartTypeSelected = { chartType ->
                                    viewModel.onEvent(AnalysisEvent.SelectChartType(chartType))
                                },
                                selectedCategoryTypeFilter = uiState.selectedCategoryTypeFilter,
                                onCategoryTypeFilterSelected = { filter ->
                                    viewModel.onEvent(AnalysisEvent.SelectCategoryTypeFilter(filter))
                                },
                                showTypeFilter = summary.topCategories.isNotEmpty(),
                            )
                        }

                        // 4. Active Chart View
                        item {
                            when (uiState.selectedChartType) {
                                ChartType.PIE -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        // Spending by Category Donut
                                        val filteredCategories = if (uiState.selectedCategoryTypeFilter != null) {
                                            summary.topCategories.filter { it.defaultType == uiState.selectedCategoryTypeFilter }
                                        } else {
                                            summary.topCategories
                                        }
                                        val chartPalette = MaterialTheme.semantic.chartPalette
                                        val pieItems = remember(filteredCategories, chartPalette) {
                                            filteredCategories.map { share ->
                                                PieInputItem(
                                                    id = share.category.id.toString(),
                                                    label = share.category.name,
                                                    amountMinor = share.totalMinor,
                                                    iconKey = share.category.iconKey,
                                                    preferredColorHex = share.category.colorHex,
                                                    preferredColor = CategoryIconProvider.parseColorHex(share.category.colorHex),
                                                )
                                            }
                                        }
                                        val slices = remember(pieItems, chartPalette) {
                                            PieSliceBuilder.build(
                                                items = pieItems,
                                                palette = chartPalette,
                                            )
                                        }
                                        val pieTotal = filteredCategories.sumOf { it.totalMinor }

                                        if (slices.isNotEmpty()) {
                                            Text(
                                                text = stringResource(id = R.string.chart_spending_by_category),
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            PieChart(
                                                slices = slices,
                                                totalMinor = pieTotal,
                                                selectedSliceId = uiState.selectedPieSliceId,
                                                onSliceSelected = { sliceId ->
                                                    viewModel.onEvent(AnalysisEvent.SelectPieSlice(sliceId))
                                                },
                                            )
                                        }

                                        // Income by Source Donut (shown when income exists)
                                        if (summary.incomeMinor > 0L && summary.incomeBySource.isNotEmpty()) {
                                            val incomeItems = remember(summary.incomeBySource, chartPalette) {
                                                summary.incomeBySource.map { share ->
                                                    PieInputItem(
                                                        id = share.source.id.toString(),
                                                        label = share.source.name,
                                                        amountMinor = share.totalMinor,
                                                        iconKey = share.source.iconKey,
                                                        preferredColorHex = share.source.colorHex,
                                                        preferredColor = CategoryIconProvider.parseColorHex(share.source.colorHex),
                                                    )
                                                }
                                            }
                                            val incomeSlices = remember(incomeItems, chartPalette) {
                                                PieSliceBuilder.build(
                                                    items = incomeItems,
                                                    palette = chartPalette,
                                                )
                                            }

                                            if (incomeSlices.isNotEmpty()) {
                                                Text(
                                                    text = stringResource(id = R.string.chart_income_by_source),
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.padding(top = 8.dp),
                                                )
                                                PieChart(
                                                    slices = incomeSlices,
                                                    totalMinor = summary.incomeMinor,
                                                    selectedSliceId = null,
                                                    onSliceSelected = {},
                                                )
                                            }
                                        }
                                    }
                                }
                                ChartType.LINE -> {
                                    AnalysisLineChart(
                                        data = uiState.trendSeriesData,
                                        selectedIndex = uiState.selectedLinePointIndex,
                                        onPointSelected = { index ->
                                            viewModel.onEvent(AnalysisEvent.SelectLinePoint(index))
                                        },
                                    )
                                }
                                ChartType.BAR -> {
                                    AnalysisStackedBarChart(
                                        summary = summary,
                                        mode = uiState.mode,
                                        selectedIndex = uiState.selectedBarIndex,
                                        onBarSelected = { index ->
                                            viewModel.onEvent(AnalysisEvent.SelectBar(index))
                                        },
                                    )
                                }
                            }
                        }

                        // 5. View as Table Toggle Button
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.onEvent(AnalysisEvent.ToggleTableView) },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TableChart,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 6.dp),
                                    )
                                    Text(
                                        text = if (uiState.isTableVisible) {
                                            stringResource(id = R.string.analysis_hide_table)
                                        } else {
                                            stringResource(id = R.string.analysis_view_as_table)
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                        }

                        // 6. Accessible Data Table (Collapsible)
                        if (uiState.isTableVisible) {
                            item {
                                AnalysisDataTable(
                                    summary = summary,
                                    mode = uiState.mode,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }

                        // 7. Budget Progress (Month Mode only)
                        if (uiState.mode == AnalysisPeriodMode.MONTH && uiState.budgetStatus != null) {
                            item {
                                AnalysisBudgetProgress(
                                    budgetStatus = uiState.budgetStatus!!,
                                )
                            }
                        }

                        // 8. Key Analytics Metrics
                        item {
                            AnalysisStatsRow(summary = summary)
                        }

                        // 9. Top Categories Breakdown
                        if (summary.topCategories.isNotEmpty()) {
                            item {
                                AnalysisCategoryBreakdown(categories = summary.topCategories)
                            }
                        }

                        // 10. Insights Section
                        if (uiState.insights.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(id = R.string.analysis_insights_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                            }

                            items(
                                items = uiState.insights,
                                key = { it.title },
                            ) { insight ->
                                InsightCard(insight = insight)
                            }
                        }
                    }
                }
            }
        }
    }
}
