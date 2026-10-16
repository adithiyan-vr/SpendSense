/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.ui

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PieSliceBuilderTest {

    private val samplePalette = listOf(
        Color(0xFF2563EB),
        Color(0xFF7C3AED),
        Color(0xFFDB2777),
        Color(0xFF059669),
        Color(0xFFD97706),
        Color(0xFF0891B2),
        Color(0xFF4F46E5),
        Color(0xFFDC2626),
    )

    @Test
    fun `empty items returns empty slice list`() {
        val slices = PieSliceBuilder.build(emptyList(), samplePalette)
        assertTrue(slices.isEmpty())
    }

    @Test
    fun `all zero amounts returns empty slice list`() {
        val items = listOf(
            PieInputItem(id = "1", label = "A", amountMinor = 0L),
            PieInputItem(id = "2", label = "B", amountMinor = 0L),
        )
        val slices = PieSliceBuilder.build(items, samplePalette)
        assertTrue(slices.isEmpty())
    }

    @Test
    fun `single item receives 100 percent and full sweep without gaps`() {
        val items = listOf(
            PieInputItem(id = "1", label = "Food", amountMinor = 50_000L),
        )
        val slices = PieSliceBuilder.build(items, samplePalette)
        assertEquals(1, slices.size)
        val slice = slices.first()
        assertEquals("Food", slice.label)
        assertEquals(100, slice.percentage)
        assertEquals(50_000L, slice.amountMinor)
        assertEquals(-90f, slice.startAngle)
        assertEquals(360f, slice.sweepAngle)
        assertFalse(slice.isOthers)
    }

    @Test
    fun `three equal items sum to exactly 100 percent via largest remainder method`() {
        val items = listOf(
            PieInputItem(id = "1", label = "A", amountMinor = 10_000L),
            PieInputItem(id = "2", label = "B", amountMinor = 10_000L),
            PieInputItem(id = "3", label = "C", amountMinor = 10_000L),
        )
        val slices = PieSliceBuilder.build(items, samplePalette)
        assertEquals(3, slices.size)
        val totalPercentage = slices.sumOf { it.percentage }
        assertEquals(100, totalPercentage)
    }

    @Test
    fun `irregular fractions sum to exactly 100 percent`() {
        val items = listOf(
            PieInputItem(id = "1", label = "Food", amountMinor = 143_250L),
            PieInputItem(id = "2", label = "Rent", amountMinor = 286_500L),
            PieInputItem(id = "3", label = "Fun", amountMinor = 71_625L),
            PieInputItem(id = "4", label = "Bills", amountMinor = 43_100L),
            PieInputItem(id = "5", label = "Travel", amountMinor = 29_800L),
        )
        val slices = PieSliceBuilder.build(items, samplePalette)
        assertEquals(items.size, slices.size)
        val totalPercentage = slices.sumOf { it.percentage }
        assertEquals(100, totalPercentage)
        assertEquals(items.sumOf { it.amountMinor }, slices.sumOf { it.amountMinor })
    }

    @Test
    fun `exactly 7 items triggers N+1 exception showing all 7 by name`() {
        val items = (1..7).map { i ->
            PieInputItem(id = "$i", label = "Cat $i", amountMinor = (10 - i) * 10_000L)
        }
        val slices = PieSliceBuilder.build(items, samplePalette)
        assertEquals(7, slices.size)
        assertFalse(slices.any { it.isOthers })
        assertEquals(100, slices.sumOf { it.percentage })
    }

    @Test
    fun `8 items merges bottom 2 into Others`() {
        val items = (1..8).map { i ->
            PieInputItem(id = "$i", label = "Cat $i", amountMinor = (10 - i) * 10_000L)
        }
        val slices = PieSliceBuilder.build(items, samplePalette, othersLabel = "Others")
        assertEquals(7, slices.size) // 6 top + 1 Others
        val othersSlice = slices.find { it.isOthers }
        assertTrue(othersSlice != null)
        assertEquals("Others", othersSlice?.label)
        assertEquals(2, othersSlice?.mergedItems?.size)
        assertEquals(100, slices.sumOf { it.percentage })
        assertEquals(items.sumOf { it.amountMinor }, slices.sumOf { it.amountMinor })
    }

    @Test
    fun `slice angles start at 12 oclock and respect 2 degree gap`() {
        val items = listOf(
            PieInputItem(id = "1", label = "A", amountMinor = 60_000L),
            PieInputItem(id = "2", label = "B", amountMinor = 40_000L),
        )
        val slices = PieSliceBuilder.build(items, samplePalette)
        assertEquals(2, slices.size)
        assertEquals(-90f, slices[0].startAngle)
        // Next slice starts after slice 0 sweep + 2 degree gap
        val expectedStart1 = slices[0].startAngle + slices[0].sweepAngle + PieSliceBuilder.SLICE_GAP_DEGREES
        assertEquals(expectedStart1, slices[1].startAngle, 0.01f)
    }

    @Test
    fun `distinct colors assigned to each slice`() {
        val items = (1..5).map { i ->
            PieInputItem(id = "$i", label = "Cat $i", amountMinor = 10_000L)
        }
        val slices = PieSliceBuilder.build(items, samplePalette)
        val distinctColors = slices.map { it.color }.distinct()
        assertEquals(5, distinctColors.size)
    }
}
