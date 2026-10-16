/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.util.Money
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Custom Compose Donut/Pie Chart composable.
 * Features:
 * - Donut geometry: inner hole ~72% of radius, ring thickness ~28%.
 * - 12 o'clock start (-90f), clockwise, 2° slice gaps.
 * - Interactive slice selection via Canvas tap with atan2 hit-testing.
 * - Center text: Formatted Total when unselected, or Slice details (Name, %, Amount) when selected.
 * - Interactive legend with click-to-highlight and expandable "Others" list.
 * - TalkBack accessibility support.
 */
@Composable
fun PieChart(
    slices: List<PieSlice>,
    totalMinor: Long,
    selectedSliceId: String?,
    onSliceSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    chartSize: Dp = 220.dp,
    showLegend: Boolean = true,
) {
    if (slices.isEmpty()) return

    val selectedSlice = slices.find { it.id == selectedSliceId }

    // Entry animation: sweep in over 600ms
    var animationTarget by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(slices) {
        animationTarget = 1f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "PieChartSweepAnimation",
    )

    var isOthersExpanded by remember { mutableStateOf(false) }

    val chartTalkBackSummary = buildString {
        append("Spending breakdown chart. Total: ${Money.format(totalMinor)}. ")
        slices.forEach { slice ->
            append("${slice.label}: ${slice.percentage} percent, ${Money.format(slice.amountMinor)}. ")
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Chart Canvas + Center Text Box
            Box(
                modifier = Modifier
                    .size(chartSize)
                    .semantics {
                        contentDescription = chartTalkBackSummary
                        role = Role.Image
                    },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(
                    modifier = Modifier
                        .size(chartSize)
                        .pointerInput(slices, selectedSliceId) {
                            detectTapGestures { offset ->
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                val dx = offset.x - cx
                                val dy = offset.y - cy
                                val dist = sqrt(dx * dx + dy * dy)
                                val outerR = min(size.width, size.height) / 2f
                                val innerR = outerR * 0.60f

                                if (dist in innerR..(outerR * 1.15f)) {
                                    var degrees = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    if (degrees < -90f) {
                                        degrees += 360f
                                    }
                                    val hit = slices.find { slice ->
                                        degrees >= slice.startAngle && degrees <= (slice.startAngle + slice.sweepAngle)
                                    }
                                    if (hit != null) {
                                        onSliceSelected(if (selectedSliceId == hit.id) null else hit.id)
                                    } else {
                                        onSliceSelected(null)
                                    }
                                } else {
                                    onSliceSelected(null)
                                }
                            }
                        },
                ) {
                    val radius = min(size.width, size.height) / 2f
                    val ringThickness = radius * 0.28f
                    val centerRadius = radius - (ringThickness / 2f)
                    val arcSize = Size(centerRadius * 2f, centerRadius * 2f)
                    val topLeft = Offset(center.x - centerRadius, center.y - centerRadius)

                    slices.forEach { slice ->
                        val isSelected = slice.id == selectedSliceId
                        val isAnySelected = selectedSlice != null

                        val effectiveColor = when {
                            isSelected -> slice.color
                            isAnySelected -> slice.color.copy(alpha = 0.35f)
                            else -> slice.color
                        }

                        val strokeWidth = if (isSelected) ringThickness * 1.18f else ringThickness
                        val effectiveSweep = slice.sweepAngle * animatedProgress

                        drawArc(
                            color = effectiveColor,
                            startAngle = slice.startAngle,
                            sweepAngle = effectiveSweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                        )
                    }
                }

                // Center Label Content
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clickable { onSliceSelected(null) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (selectedSlice != null) {
                        Text(
                            text = selectedSlice.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "${selectedSlice.percentage}%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = selectedSlice.color,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = Money.format(selectedSlice.amountMinor),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.chart_total),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = Money.format(totalMinor),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(id = R.string.chart_tap_to_filter),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            // Legend Section
            if (showLegend) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    slices.forEach { slice ->
                        val isSelected = slice.id == selectedSliceId

                        Surface(
                            onClick = {
                                onSliceSelected(if (isSelected) null else slice.id)
                                if (slice.isOthers) {
                                    isOthersExpanded = !isOthersExpanded
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) {
                                slice.color.copy(alpha = 0.12f)
                            } else {
                                Color.Transparent
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "${slice.label}, ${slice.percentage} percent, ${Money.format(slice.amountMinor)}"
                                },
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(slice.color),
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = slice.label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )

                                        if (slice.isOthers && slice.mergedItems.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = if (isOthersExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Text(
                                            text = "${slice.percentage}%",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) slice.color else MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = Money.format(slice.amountMinor),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }

                                // Expandable Others details
                                if (slice.isOthers && slice.mergedItems.isNotEmpty()) {
                                    AnimatedVisibility(
                                        visible = isOthersExpanded,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically(),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 22.dp, top = 6.dp, end = 4.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            slice.mergedItems.forEach { merged ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(
                                                        text = "• ${merged.label}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    )
                                                    Text(
                                                        text = Money.format(merged.amountMinor),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Medium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        }
    }
}
