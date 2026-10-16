/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.domain.model.ExpenseType

enum class ChartType {
    PIE,
    LINE,
    BAR,
}

@Composable
fun ChartTypeSwitcher(
    selectedChartType: ChartType,
    onChartTypeSelected: (ChartType) -> Unit,
    selectedCategoryTypeFilter: ExpenseType?,
    onCategoryTypeFilterSelected: (ExpenseType?) -> Unit,
    showTypeFilter: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Chart Type Switcher Chips [ Pie | Line | Bar ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = selectedChartType == ChartType.PIE,
                onClick = { onChartTypeSelected(ChartType.PIE) },
                label = { Text(text = stringResource(id = R.string.chart_type_pie)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )

            FilterChip(
                selected = selectedChartType == ChartType.LINE,
                onClick = { onChartTypeSelected(ChartType.LINE) },
                label = { Text(text = stringResource(id = R.string.chart_type_line)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )

            FilterChip(
                selected = selectedChartType == ChartType.BAR,
                onClick = { onChartTypeSelected(ChartType.BAR) },
                label = { Text(text = stringResource(id = R.string.chart_type_bar)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }

        // Sub-filter [ All | Need | Waste ] (shown when PIE is active)
        AnimatedVisibility(
            visible = showTypeFilter && selectedChartType == ChartType.PIE,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = selectedCategoryTypeFilter == null,
                    onClick = { onCategoryTypeFilterSelected(null) },
                    label = { Text(text = stringResource(id = R.string.chart_filter_all)) },
                )

                FilterChip(
                    selected = selectedCategoryTypeFilter == ExpenseType.NEED,
                    onClick = { onCategoryTypeFilterSelected(ExpenseType.NEED) },
                    label = { Text(text = stringResource(id = R.string.chart_filter_need)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.semantic.needContainer,
                        selectedLabelColor = MaterialTheme.semantic.onNeedContainer,
                    ),
                )

                FilterChip(
                    selected = selectedCategoryTypeFilter == ExpenseType.WASTE,
                    onClick = { onCategoryTypeFilterSelected(ExpenseType.WASTE) },
                    label = { Text(text = stringResource(id = R.string.chart_filter_waste)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.semantic.wasteContainer,
                        selectedLabelColor = MaterialTheme.semantic.onWasteContainer,
                    ),
                )
            }
        }
    }
}
