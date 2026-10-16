/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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

data class ChartBarData(
    val label: String,
    val fullLabel: String,
    val needMinor: Long,
    val wasteMinor: Long,
    val totalMinor: Long,
)

@Composable
fun AnalysisStackedBarChart(
    summary: PeriodSummary,
    mode: AnalysisPeriodMode,
    selectedIndex: Int?,
    onBarSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val barItems: List<ChartBarData> = when (mode) {
        AnalysisPeriodMode.WEEK -> summary.dailyTotals.map { day: DayTotal ->
            val shortDay = day.date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
            val fullDay = "${day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${day.date.dayOfMonth} ${day.date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}"
            ChartBarData(
                label = shortDay,
                fullLabel = fullDay,
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
                    val shortDay = "${day.date.dayOfMonth}"
                    val fullDay = "${day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${day.date.dayOfMonth} ${day.date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}"
                    ChartBarData(
                        label = shortDay,
                        fullLabel = fullDay,
                        needMinor = day.needMinor,
                        wasteMinor = day.wasteMinor,
                        totalMinor = day.totalMinor,
                    )
                }
            }
        }
    }

    val maxTotal = maxOf(1L, barItems.maxOfOrNull { it.totalMinor } ?: 1L)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            val chartTitle = when (mode) {
                AnalysisPeriodMode.WEEK -> stringResource(id = R.string.analysis_spending_by_day)
                AnalysisPeriodMode.MONTH -> stringResource(id = R.string.analysis_spending_by_week)
                AnalysisPeriodMode.CUSTOM -> if (summary.weeklyTotals.isNotEmpty()) {
                    stringResource(id = R.string.analysis_spending_by_week)
                } else {
                    stringResource(id = R.string.analysis_spending_by_day)
                }
            }

            Text(
                text = chartTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tooltip Banner if a bar is selected
            AnimatedVisibility(
                visible = selectedIndex != null && selectedIndex in barItems.indices,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                if (selectedIndex != null && selectedIndex in barItems.indices) {
                    val item = barItems[selectedIndex]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = item.fullLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Total: ${Money.format(item.totalMinor)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = "Need: ${Money.format(item.needMinor)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.semantic.need,
                                )
                                Text(
                                    text = "Waste: ${Money.format(item.wasteMinor)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.semantic.waste,
                                )
                            }
                        }
                    }
                }
            }

            // Stacked Bar Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                barItems.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    val heightRatio = if (item.totalMinor > 0) {
                        (item.totalMinor.toFloat() / maxTotal.toFloat()).coerceIn(0.08f, 1f)
                    } else {
                        0.03f
                    }
                    val animatedHeightFraction by animateFloatAsState(
                        targetValue = heightRatio,
                        animationSpec = tween(durationMillis = 350),
                        label = "bar_height_$index"
                    )

                    val barDesc = stringResource(
                        id = R.string.cd_chart_bar,
                        item.fullLabel,
                        Money.format(item.totalMinor),
                        Money.format(item.needMinor),
                        Money.format(item.wasteMinor),
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        // Bar Track
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            val barShape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
                            Box(
                                modifier = Modifier
                                    .width(if (barItems.size <= 7) 24.dp else 18.dp)
                                    .fillMaxHeight(animatedHeightFraction)
                                    .clip(barShape)
                                    .background(
                                        if (item.totalMinor == 0L) {
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .then(
                                        if (isSelected) {
                                            Modifier.border(2.dp, MaterialTheme.colorScheme.primary, barShape)
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clickable(role = Role.Button) {
                                        onBarSelected(if (isSelected) null else index)
                                    }
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = barDesc
                                    },
                            ) {
                                if (item.totalMinor > 0L) {
                                    val needRatio = item.needMinor.toFloat() / item.totalMinor.toFloat()
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        // Top: Waste
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight((1f - needRatio).coerceAtLeast(0.001f))
                                                .background(MaterialTheme.semantic.waste)
                                        )
                                        // Bottom: Need
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(needRatio.coerceAtLeast(0.001f))
                                                .background(MaterialTheme.semantic.need)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // X-axis label
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.semantic.need)
                        .width(10.dp)
                        .height(10.dp)
                )
                Text(
                    text = stringResource(id = R.string.type_need),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.semantic.waste)
                        .width(10.dp)
                        .height(10.dp)
                )
                Text(
                    text = stringResource(id = R.string.type_waste),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
