/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.util.CompactCurrencyFormatter
import com.supernovea.spendsense.core.util.Money
import kotlin.math.abs
import kotlin.math.max

/**
 * Straight-segment line chart supporting two series: Income vs Expense.
 * Features:
 * - Straight line segments (no cubic splines to preserve data truthfulness).
 * - Compact Indian currency Y-axis ticks (K, L, Cr).
 * - Interactive touch scrubbing and markers.
 * - Single data point notice ("Add more entries to see a trend").
 * - High-contrast legend and TalkBack accessible descriptions.
 */
@Composable
fun AnalysisLineChart(
    data: TrendSeriesData,
    selectedIndex: Int?,
    onPointSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (data.points.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val incomeColor = MaterialTheme.semantic.income
    val expenseColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    val scrubberColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val textStyle = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    val selectedPoint = selectedIndex?.let { data.points.getOrNull(it) }

    val chartTalkBackSummary = buildString {
        append("Income and expense trend chart. ")
        val totalExpense = data.points.sumOf { it.expenseMinor }
        val totalIncome = data.points.sumOf { it.incomeMinor }
        append("Total expense: ${Money.format(totalExpense)}, Total income: ${Money.format(totalIncome)}. ")
        if (data.isSingleDataPoint) {
            append("Add more entries to see a trend.")
        }
    }

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
            // Chart Title & Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.chart_income_vs_expense),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Expense legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(expenseColor),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.tab_expense),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    // Income legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(incomeColor),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.tab_income),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Line Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .semantics {
                        contentDescription = chartTalkBackSummary
                        role = Role.Image
                    },
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .pointerInput(data.points) {
                            detectTapGestures { offset ->
                                val leftPadding = 48.dp.toPx()
                                val rightPadding = 16.dp.toPx()
                                val plotWidth = size.width - leftPadding - rightPadding
                                val stepX = if (data.points.size > 1) {
                                    plotWidth / (data.points.size - 1)
                                } else {
                                    plotWidth / 2f
                                }

                                val nearestIdx = data.points.indices.minByOrNull { i ->
                                    val pointX = leftPadding + (i * stepX)
                                    abs(offset.x - pointX)
                                }

                                if (nearestIdx != null) {
                                    onPointSelected(if (selectedIndex == nearestIdx) null else nearestIdx)
                                } else {
                                    onPointSelected(null)
                                }
                            }
                        }
                        .pointerInput(data.points) {
                            detectDragGestures(
                                onDragEnd = {},
                                onDragCancel = {},
                                onDrag = { change, _ ->
                                    change.consume()
                                    val leftPadding = 48.dp.toPx()
                                    val rightPadding = 16.dp.toPx()
                                    val plotWidth = size.width - leftPadding - rightPadding
                                    val stepX = if (data.points.size > 1) {
                                        plotWidth / (data.points.size - 1)
                                    } else {
                                        plotWidth / 2f
                                    }

                                    val nearestIdx = data.points.indices.minByOrNull { i ->
                                        val pointX = leftPadding + (i * stepX)
                                        abs(change.position.x - pointX)
                                    }

                                    if (nearestIdx != null) {
                                        onPointSelected(nearestIdx)
                                    }
                                },
                            )
                        },
                ) {
                    val leftPadding = 48.dp.toPx()
                    val rightPadding = 16.dp.toPx()
                    val topPadding = 16.dp.toPx()
                    val bottomPadding = 32.dp.toPx()

                    val plotWidth = size.width - leftPadding - rightPadding
                    val plotHeight = size.height - topPadding - bottomPadding

                    val maxY = max(1L, data.maxYMinor)
                    val ticks = data.niceYTicks

                    // 1. Draw horizontal gridlines and Y-axis labels
                    ticks.forEach { tick ->
                        val ratio = (tick.toFloat() / maxY.toFloat()).coerceIn(0f, 1f)
                        val yPos = topPadding + plotHeight * (1f - ratio)

                        // Gridline
                        drawLine(
                            color = gridColor,
                            start = Offset(leftPadding, yPos),
                            end = Offset(size.width - rightPadding, yPos),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                        )

                        // Y-axis label
                        val labelText = CompactCurrencyFormatter.formatPaise(tick, includeSymbol = true)
                        val textLayout = textMeasurer.measure(labelText, style = textStyle)
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(leftPadding - textLayout.size.width - 6.dp.toPx(), yPos - textLayout.size.height / 2f),
                        )
                    }

                    if (data.points.isEmpty()) return@Canvas

                    val stepX = if (data.points.size > 1) {
                        plotWidth / (data.points.size - 1)
                    } else {
                        plotWidth / 2f
                    }

                    // 2. Draw X-axis labels and scrubber indicator
                    data.points.forEachIndexed { i, point ->
                        val xPos = leftPadding + (i * stepX)
                        val textLayout = textMeasurer.measure(point.xLabel, style = textStyle)
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(xPos - textLayout.size.width / 2f, size.height - bottomPadding + 8.dp.toPx()),
                        )

                        // Highlight vertical dashed scrubber guideline if selected
                        if (i == selectedIndex) {
                            drawLine(
                                color = scrubberColor,
                                start = Offset(xPos, topPadding),
                                end = Offset(xPos, topPadding + plotHeight),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                            )
                        }
                    }

                    // 3. Draw Straight Line Segments & Markers for Expense Series
                    val expensePath = Path()
                    val expensePoints = data.points.mapIndexed { i, pt ->
                        val x = leftPadding + (i * stepX)
                        val yRatio = (pt.expenseMinor.toFloat() / maxY.toFloat()).coerceIn(0f, 1f)
                        val y = topPadding + plotHeight * (1f - yRatio)
                        Offset(x, y)
                    }

                    expensePoints.forEachIndexed { i, pt ->
                        if (i == 0) expensePath.moveTo(pt.x, pt.y) else expensePath.lineTo(pt.x, pt.y)
                    }

                    drawPath(
                        path = expensePath,
                        color = expenseColor,
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )

                    expensePoints.forEachIndexed { i, pt ->
                        val isSelected = i == selectedIndex
                        val markerRadius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx()

                        drawCircle(
                            color = expenseColor,
                            radius = markerRadius,
                            center = pt,
                        )
                        if (isSelected) {
                            drawCircle(
                                color = Color.White,
                                radius = 2.5.dp.toPx(),
                                center = pt,
                            )
                        }
                    }

                    // 4. Draw Straight Line Segments & Markers for Income Series (if any income)
                    if (data.hasIncomeData) {
                        val incomePath = Path()
                        val incomePoints = data.points.mapIndexed { i, pt ->
                            val x = leftPadding + (i * stepX)
                            val yRatio = (pt.incomeMinor.toFloat() / maxY.toFloat()).coerceIn(0f, 1f)
                            val y = topPadding + plotHeight * (1f - yRatio)
                            Offset(x, y)
                        }

                        incomePoints.forEachIndexed { i, pt ->
                            if (i == 0) incomePath.moveTo(pt.x, pt.y) else incomePath.lineTo(pt.x, pt.y)
                        }

                        drawPath(
                            path = incomePath,
                            color = incomeColor,
                            style = Stroke(
                                width = 2.5.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                            ),
                        )

                        incomePoints.forEachIndexed { i, pt ->
                            val isSelected = i == selectedIndex
                            val markerRadius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx()

                            drawCircle(
                                color = incomeColor,
                                radius = markerRadius,
                                center = pt,
                            )
                            if (isSelected) {
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.5.dp.toPx(),
                                    center = pt,
                                )
                            }
                        }
                    }
                }
            }

            // Single Data Point Notice
            if (data.isSingleDataPoint) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.chart_single_point_notice),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Interactive Point Tooltip Card (shown when scrubbed or tapped)
            AnimatedVisibility(
                visible = selectedPoint != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                if (selectedPoint != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        ) {
                            Text(
                                text = selectedPoint.fullDateLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.tab_expense),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = Money.format(selectedPoint.expenseMinor),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = expenseColor,
                                    )
                                }

                                if (data.hasIncomeData) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = stringResource(id = R.string.tab_income),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = Money.format(selectedPoint.incomeMinor),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = incomeColor,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
