/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Master Brand Logo composable for SpendSense.
 *
 * Renders the two tilted rounded pills (Teal and Orange) on an optional charcoal background.
 * Based directly on the reference geometry (1024 reference canvas):
 * - Teal bar: thickness 103, length 565, center (512, 423), rotated 15.5 deg counter-clockwise.
 * - Orange bar: thickness 103, length 381, center (423, 624), rotated 15.5 deg counter-clockwise.
 *
 * @param size The width and height of the square logo canvas.
 * @param modifier Optional Modifier.
 * @param showBackground If true, draws the solid charcoal background.
 */
@Composable
fun SpendSenseLogo(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    showBackground: Boolean = true,
    backgroundColor: Color = Color(0xFF1B1D23),
    tealColor: Color = Color(0xFF2AC1AE),
    orangeColor: Color = Color(0xFFF5943B),
) {
    Canvas(
        modifier = modifier.size(size)
    ) {
        val w = this.size.width
        val s = w / 1024f

        if (showBackground) {
            drawRect(
                color = backgroundColor,
                topLeft = Offset.Zero,
                size = this.size
            )
        }

        // Top bar (Teal)
        // Center: (512 * s, 423 * s), length: 565 * s, thickness: 103 * s
        val tealCenterX = 512f * s
        val tealCenterY = 423f * s
        val tealLength = 565f * s
        val tealThickness = 103f * s
        val tealRadius = tealThickness / 2f

        rotate(degrees = -15.5f, pivot = Offset(tealCenterX, tealCenterY)) {
            drawRoundRect(
                color = tealColor,
                topLeft = Offset(tealCenterX - tealLength / 2f, tealCenterY - tealThickness / 2f),
                size = Size(tealLength, tealThickness),
                cornerRadius = CornerRadius(tealRadius, tealRadius)
            )
        }

        // Bottom bar (Orange)
        // Center: (423 * s, 624 * s), length: 381 * s, thickness: 103 * s
        val orangeCenterX = 423f * s
        val orangeCenterY = 624f * s
        val orangeLength = 381f * s
        val orangeThickness = 103f * s
        val orangeRadius = orangeThickness / 2f

        rotate(degrees = -15.5f, pivot = Offset(orangeCenterX, orangeCenterY)) {
            drawRoundRect(
                color = orangeColor,
                topLeft = Offset(orangeCenterX - orangeLength / 2f, orangeCenterY - orangeThickness / 2f),
                size = Size(orangeLength, orangeThickness),
                cornerRadius = CornerRadius(orangeRadius, orangeRadius)
            )
        }
    }
}
