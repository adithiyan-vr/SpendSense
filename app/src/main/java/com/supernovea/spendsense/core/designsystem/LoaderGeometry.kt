/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.designsystem

data class BarGeometry(
    val length: Float,
    val thickness: Float,
    val cornerRadius: Float,
    val leftCapCenterX: Float,
    val leftCapCenterY: Float,
    val tiltDegrees: Float = -17f,
)

data class LoaderState(
    val teal: BarGeometry,
    val orange: BarGeometry,
)

object LoaderGeometry {
    const val TILT_DEGREES: Float = -17f
    const val WIDTH_FRACTION: Float = 0.115f
    const val MAX_THICKNESS_DP: Float = 48f

    fun calculateT(containerWidth: Float, maxT: Float = 48f): Float {
        val calculated = containerWidth * WIDTH_FRACTION
        return minOf(calculated, maxT)
    }

    fun calculateThickness(p: Float, t: Float): Float {
        return (1.2f - 0.2f * p.coerceIn(0f, 1f)) * t
    }

    fun at(p: Float, t: Float, containerWidth: Float, containerHeight: Float): LoaderState {
        val clampedP = p.coerceIn(0f, 1f)
        val thickness = calculateThickness(clampedP, t)
        val cornerRadius = thickness / 2f

        val tealLength = (1.2f * t) + (5.5f * t - 1.2f * t) * clampedP
        val orangeLength = (1.2f * t) + (4.5f * t - 1.2f * t) * clampedP

        val centerX = containerWidth / 2f - 1.5f * t
        val tealCapY = containerHeight / 2f - 0.9f * t
        val orangeCapY = tealCapY + 1.8f * t

        val tealBar = BarGeometry(
            length = tealLength,
            thickness = thickness,
            cornerRadius = cornerRadius,
            leftCapCenterX = centerX,
            leftCapCenterY = tealCapY,
            tiltDegrees = TILT_DEGREES,
        )

        val orangeBar = BarGeometry(
            length = orangeLength,
            thickness = thickness,
            cornerRadius = cornerRadius,
            leftCapCenterX = centerX,
            leftCapCenterY = orangeCapY,
            tiltDegrees = TILT_DEGREES,
        )

        return LoaderState(teal = tealBar, orange = orangeBar)
    }
}
