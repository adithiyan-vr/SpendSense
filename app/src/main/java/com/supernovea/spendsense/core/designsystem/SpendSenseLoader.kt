/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.designsystem

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R

/**
 * Minimum display duration for the startup loader on cold starts (1 full 1500ms cycle).
 */
const val LOADER_MIN_DISPLAY_MS = 1500L

/**
 * Checks whether system animations are disabled ("reduce animations" setting).
 */
fun isReduceMotionEnabled(context: Context): Boolean {
    return try {
        val durationScale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        val transitionScale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.TRANSITION_ANIMATION_SCALE,
            1f
        )
        durationScale == 0f || transitionScale == 0f
    } catch (_: Exception) {
        false
    }
}

/**
 * Startup loader animation composable for SpendSense.
 *
 * Spec:
 * - Two round dots: teal (top) and orange (bottom).
 * - Stretch along a 17 degree counter-clockwise tilt into pills, hold, then retract.
 * - Left cap centers never move.
 * - 1500 ms loop with keyframe easing:
 *   - 0-750 ms: cubic-bezier(0.4, 0, 0.2, 1) [FastOutSlowIn]
 *   - 750-900 ms: hold at p = 1
 *   - 900-1500 ms: cubic-bezier(0.4, 0, 0.6, 1)
 * - 120 ms initial fade-in only on first appearance.
 * - Reduced motion shows static full-length state (p = 1).
 */
@Composable
fun SpendSenseLoader(
    modifier: Modifier = Modifier,
    tealColor: Color = BrandTeal,
    orangeColor: Color = BrandOrange,
    reduceMotionOverride: Boolean? = null,
) {
    val context = LocalContext.current
    val isReducedMotion = reduceMotionOverride ?: remember(context) { isReduceMotionEnabled(context) }
    val contentDesc = stringResource(id = R.string.loading_spendsense)

    val density = LocalDensity.current
    val maxThicknessPx = remember(density) { with(density) { 48.dp.toPx() } }

    if (isReducedMotion) {
        // Static full-length state (p = 1) with no animation
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .semantics { contentDescription = contentDesc }
        ) {
            val t = LoaderGeometry.calculateT(size.width, maxThicknessPx)
            val state = LoaderGeometry.at(1f, t, size.width, size.height)

            // Teal bar
            rotate(
                degrees = state.teal.tiltDegrees,
                pivot = Offset(state.teal.leftCapCenterX, state.teal.leftCapCenterY)
            ) {
                drawRoundRect(
                    color = tealColor,
                    topLeft = Offset(
                        state.teal.leftCapCenterX - state.teal.thickness / 2f,
                        state.teal.leftCapCenterY - state.teal.thickness / 2f
                    ),
                    size = Size(state.teal.length, state.teal.thickness),
                    cornerRadius = CornerRadius(state.teal.cornerRadius, state.teal.cornerRadius)
                )
            }

            // Orange bar
            rotate(
                degrees = state.orange.tiltDegrees,
                pivot = Offset(state.orange.leftCapCenterX, state.orange.leftCapCenterY)
            ) {
                drawRoundRect(
                    color = orangeColor,
                    topLeft = Offset(
                        state.orange.leftCapCenterX - state.orange.thickness / 2f,
                        state.orange.leftCapCenterY - state.orange.thickness / 2f
                    ),
                    size = Size(state.orange.length, state.orange.thickness),
                    cornerRadius = CornerRadius(state.orange.cornerRadius, state.orange.cornerRadius)
                )
            }
        }
    } else {
        val transition = rememberInfiniteTransition(label = "SpendSenseLoaderTransition")
        val p by transition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1500
                    0f at 0 using CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                    1f at 750
                    1f at 900 using CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
                    0f at 1500
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "loaderProgress"
        )

        val introAlpha = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            introAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 120, easing = LinearEasing)
            )
        }

        val alphaValue = introAlpha.value

        Canvas(
            modifier = modifier
                .fillMaxSize()
                .semantics { contentDescription = contentDesc }
        ) {
            val t = LoaderGeometry.calculateT(size.width, maxThicknessPx)
            val state = LoaderGeometry.at(p, t, size.width, size.height)

            // Teal bar
            rotate(
                degrees = state.teal.tiltDegrees,
                pivot = Offset(state.teal.leftCapCenterX, state.teal.leftCapCenterY)
            ) {
                drawRoundRect(
                    color = tealColor,
                    topLeft = Offset(
                        state.teal.leftCapCenterX - state.teal.thickness / 2f,
                        state.teal.leftCapCenterY - state.teal.thickness / 2f
                    ),
                    size = Size(state.teal.length, state.teal.thickness),
                    cornerRadius = CornerRadius(state.teal.cornerRadius, state.teal.cornerRadius),
                    alpha = alphaValue
                )
            }

            // Orange bar
            rotate(
                degrees = state.orange.tiltDegrees,
                pivot = Offset(state.orange.leftCapCenterX, state.orange.leftCapCenterY)
            ) {
                drawRoundRect(
                    color = orangeColor,
                    topLeft = Offset(
                        state.orange.leftCapCenterX - state.orange.thickness / 2f,
                        state.orange.leftCapCenterY - state.orange.thickness / 2f
                    ),
                    size = Size(state.orange.length, state.orange.thickness),
                    cornerRadius = CornerRadius(state.orange.cornerRadius, state.orange.cornerRadius),
                    alpha = alphaValue
                )
            }
        }
    }
}

/**
 * Fullscreen startup loader screen for cold starts.
 */
@Composable
fun SpendSenseLoaderScreen(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrandCharcoal,
    tealColor: Color = BrandTeal,
    orangeColor: Color = BrandOrange,
    reduceMotionOverride: Boolean? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        SpendSenseLoader(
            tealColor = tealColor,
            orangeColor = orangeColor,
            reduceMotionOverride = reduceMotionOverride,
        )
    }
}
