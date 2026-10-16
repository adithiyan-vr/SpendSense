/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.designsystem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoaderGeometryTest {

    private val testT = 40f
    private val containerW = 360f
    private val containerH = 640f

    @Test
    fun `p equals 0 gives perfect circles of diameter 1 point 2 T`() {
        val state = LoaderGeometry.at(p = 0f, t = testT, containerWidth = containerW, containerHeight = containerH)

        val expectedDiameter = 1.2f * testT
        val expectedRadius = expectedDiameter / 2f

        // Teal bar
        assertEquals(expectedDiameter, state.teal.length, 0.001f)
        assertEquals(expectedDiameter, state.teal.thickness, 0.001f)
        assertEquals(expectedRadius, state.teal.cornerRadius, 0.001f)

        // Orange bar
        assertEquals(expectedDiameter, state.orange.length, 0.001f)
        assertEquals(expectedDiameter, state.orange.thickness, 0.001f)
        assertEquals(expectedRadius, state.orange.cornerRadius, 0.001f)
    }

    @Test
    fun `p equals 1 gives teal 5 point 5 T and orange 4 point 5 T at thickness T`() {
        val state = LoaderGeometry.at(p = 1f, t = testT, containerWidth = containerW, containerHeight = containerH)

        // Thickness
        assertEquals(testT, state.teal.thickness, 0.001f)
        assertEquals(testT, state.orange.thickness, 0.001f)
        assertEquals(testT / 2f, state.teal.cornerRadius, 0.001f)
        assertEquals(testT / 2f, state.orange.cornerRadius, 0.001f)

        // Lengths
        assertEquals(5.5f * testT, state.teal.length, 0.001f)
        assertEquals(4.5f * testT, state.orange.length, 0.001f)
    }

    @Test
    fun `thickness is strictly monotonic decreasing as progress increases`() {
        var prevThickness = Float.MAX_VALUE
        for (i in 0..100) {
            val p = i / 100f
            val currentThickness = LoaderGeometry.calculateThickness(p, testT)
            assertTrue("Thickness must decrease or remain equal as p grows", currentThickness <= prevThickness)
            prevThickness = currentThickness
        }
    }

    @Test
    fun `left cap centers never move across any progress value`() {
        val stateAt0 = LoaderGeometry.at(p = 0f, t = testT, containerWidth = containerW, containerHeight = containerH)
        val testProgressValues = listOf(0.1f, 0.25f, 0.5f, 0.75f, 0.9f, 1.0f)

        for (p in testProgressValues) {
            val state = LoaderGeometry.at(p = p, t = testT, containerWidth = containerW, containerHeight = containerH)

            assertEquals("Teal cap center X must not move", stateAt0.teal.leftCapCenterX, state.teal.leftCapCenterX, 0.0001f)
            assertEquals("Teal cap center Y must not move", stateAt0.teal.leftCapCenterY, state.teal.leftCapCenterY, 0.0001f)
            assertEquals("Orange cap center X must not move", stateAt0.orange.leftCapCenterX, state.orange.leftCapCenterX, 0.0001f)
            assertEquals("Orange cap center Y must not move", stateAt0.orange.leftCapCenterY, state.orange.leftCapCenterY, 0.0001f)
        }
    }

    @Test
    fun `orange left cap is positioned 1 point 8 T directly below teal left cap`() {
        val state = LoaderGeometry.at(p = 0.5f, t = testT, containerWidth = containerW, containerHeight = containerH)

        assertEquals(state.teal.leftCapCenterX, state.orange.leftCapCenterX, 0.0001f)
        assertEquals(state.teal.leftCapCenterY + 1.8f * testT, state.orange.leftCapCenterY, 0.001f)
    }

    @Test
    fun `tilt degrees is minus 17 degrees for both bars`() {
        val state = LoaderGeometry.at(p = 0.5f, t = testT, containerWidth = containerW, containerHeight = containerH)

        assertEquals(-17f, state.teal.tiltDegrees, 0.001f)
        assertEquals(-17f, state.orange.tiltDegrees, 0.001f)
    }

    @Test
    fun `calculateT respects width fraction and caps at max thickness`() {
        // Below cap: 360 * 0.115 = 41.4, max 48
        val tBelow = LoaderGeometry.calculateT(360f, 48f)
        assertEquals(41.4f, tBelow, 0.001f)

        // Above cap: 600 * 0.115 = 69.0, capped at 48
        val tAbove = LoaderGeometry.calculateT(600f, 48f)
        assertEquals(48f, tAbove, 0.001f)
    }
}
