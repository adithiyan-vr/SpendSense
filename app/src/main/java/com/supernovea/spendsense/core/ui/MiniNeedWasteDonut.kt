/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.core.designsystem.semantic
import kotlin.math.min

/**
 * Compact mini donut chart for Need vs Waste distribution (used on Home and metric cards).
 * Complies with FR-097 (Mini donut Need vs Waste on Home).
 */
@Composable
fun MiniNeedWasteDonut(
    needMinor: Long,
    wasteMinor: Long,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    val total = needMinor + wasteMinor
    val needColor = MaterialTheme.semantic.need
    val wasteColor = MaterialTheme.semantic.waste
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant

    val needFraction = if (total > 0L) (needMinor.toDouble() / total.toDouble()).toFloat() else 0f
    val wasteFraction = if (total > 0L) (wasteMinor.toDouble() / total.toDouble()).toFloat() else 0f
    val bothPresent = needMinor > 0L && wasteMinor > 0L
    val gapDegrees = if (bothPresent) 2f else 0f

    val description = "Need vs Waste: ${(needFraction * 100).toInt()}% Need, ${(wasteFraction * 100).toInt()}% Waste"

    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = description },
    ) {
        val radius = min(this.size.width, this.size.height) / 2f
        val ringThickness = radius * 0.28f
        val centerRadius = radius - (ringThickness / 2f)
        val arcSize = Size(centerRadius * 2f, centerRadius * 2f)
        val topLeft = Offset(center.x - centerRadius, center.y - centerRadius)

        if (total <= 0L) {
            drawArc(
                color = emptyColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = ringThickness, cap = StrokeCap.Butt),
            )
        } else {
            val totalSweep = 360f - (if (bothPresent) gapDegrees * 2f else 0f)
            val needSweep = needFraction * totalSweep
            val wasteSweep = wasteFraction * totalSweep

            var currentAngle = -90f
            if (needMinor > 0L) {
                drawArc(
                    color = needColor,
                    startAngle = currentAngle,
                    sweepAngle = needSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = ringThickness, cap = StrokeCap.Butt),
                )
                currentAngle += needSweep + gapDegrees
            }
            if (wasteMinor > 0L) {
                drawArc(
                    color = wasteColor,
                    startAngle = currentAngle,
                    sweepAngle = wasteSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = ringThickness, cap = StrokeCap.Butt),
                )
            }
        }
    }
}
