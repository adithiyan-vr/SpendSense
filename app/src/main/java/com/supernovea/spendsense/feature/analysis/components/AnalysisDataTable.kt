/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.DayTotal
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.WeekTotal
import com.supernovea.spendsense.feature.analysis.AnalysisPeriodMode
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AnalysisDataTable(
    summary: PeriodSummary,
    mode: AnalysisPeriodMode,
    modifier: Modifier = Modifier,
) {
    val rows: List<ChartBarData> = when (mode) {
        AnalysisPeriodMode.WEEK -> summary.dailyTotals.map { day: DayTotal ->
            val label = "${day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${day.date.dayOfMonth}"
            ChartBarData(
                label = label,
                fullLabel = label,
                needMinor = day.needMinor,
                wasteMinor = day.wasteMinor,
                totalMinor = day.totalMinor,
            )
        }
        AnalysisPeriodMode.MONTH -> summary.weeklyTotals.map { week: WeekTotal ->
            ChartBarData(
                label = "W${week.weekNumber}",
                fullLabel = "Week ${week.weekNumber}",
                needMinor = week.needMinor,
                wasteMinor = week.wasteMinor,
                totalMinor = week.totalMinor,
            )
        }
        AnalysisPeriodMode.CUSTOM -> {
            if (summary.weeklyTotals.isNotEmpty()) {
                summary.weeklyTotals.map { week: WeekTotal ->
                    ChartBarData(
                        label = "W${week.weekNumber}",
                        fullLabel = "Week ${week.weekNumber}",
                        needMinor = week.needMinor,
                        wasteMinor = week.wasteMinor,
                        totalMinor = week.totalMinor,
                    )
                }
            } else {
                summary.dailyTotals.map { day: DayTotal ->
                    val label = "${day.date.dayOfMonth} ${day.date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}"
                    ChartBarData(
                        label = label,
                        fullLabel = label,
                        needMinor = day.needMinor,
                        wasteMinor = day.wasteMinor,
                        totalMinor = day.totalMinor,
                    )
                }
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.analysis_col_period),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1.2f),
                )
                Text(
                    text = stringResource(id = R.string.analysis_col_need),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.semantic.need,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(id = R.string.analysis_col_waste),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.semantic.waste,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(id = R.string.analysis_col_total),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 1.dp,
            )

            // Content Rows
            rows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (index % 2 == 0) MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = row.label,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1.2f),
                    )
                    Text(
                        text = Money.format(row.needMinor),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.semantic.need,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = Money.format(row.wasteMinor),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.semantic.waste,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = Money.format(row.totalMinor),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
