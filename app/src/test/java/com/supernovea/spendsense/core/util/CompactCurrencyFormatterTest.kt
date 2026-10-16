/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CompactCurrencyFormatterTest {

    @Test
    fun `formatPaise formats zero correctly`() {
        assertEquals("₹0", CompactCurrencyFormatter.formatPaise(0L))
        assertEquals("0", CompactCurrencyFormatter.formatPaise(0L, includeSymbol = false))
    }

    @Test
    fun `formatPaise formats sub-thousand correctly`() {
        assertEquals("₹500", CompactCurrencyFormatter.formatPaise(50_000L))
        assertEquals("500", CompactCurrencyFormatter.formatPaise(50_000L, includeSymbol = false))
        assertEquals("₹999", CompactCurrencyFormatter.formatPaise(99_900L))
    }

    @Test
    fun `formatPaise formats thousands with K suffix`() {
        assertEquals("₹1K", CompactCurrencyFormatter.formatPaise(100_000L))
        assertEquals("₹1.2K", CompactCurrencyFormatter.formatPaise(120_000L))
        assertEquals("₹15K", CompactCurrencyFormatter.formatPaise(1_500_000L))
        assertEquals("15K", CompactCurrencyFormatter.formatPaise(1_500_000L, includeSymbol = false))
        assertEquals("₹99.9K", CompactCurrencyFormatter.formatPaise(9_990_000L))
    }

    @Test
    fun `formatPaise formats lakhs with L suffix`() {
        assertEquals("₹1L", CompactCurrencyFormatter.formatPaise(10_000_000L))
        assertEquals("₹3.4L", CompactCurrencyFormatter.formatPaise(34_000_000L))
        assertEquals("3.4L", CompactCurrencyFormatter.formatPaise(34_000_000L, includeSymbol = false))
        assertEquals("₹25L", CompactCurrencyFormatter.formatPaise(250_000_000L))
    }

    @Test
    fun `formatPaise formats crores with Cr suffix`() {
        assertEquals("₹1Cr", CompactCurrencyFormatter.formatPaise(1_000_000_000L))
        assertEquals("₹1.1Cr", CompactCurrencyFormatter.formatPaise(1_100_000_000L))
        assertEquals("1.1Cr", CompactCurrencyFormatter.formatPaise(1_100_000_000L, includeSymbol = false))
    }

    @Test
    fun `formatPaise handles negative amounts`() {
        assertEquals("-₹500", CompactCurrencyFormatter.formatPaise(-50_000L))
        assertEquals("-₹1.2K", CompactCurrencyFormatter.formatPaise(-120_000L))
        assertEquals("-1.2K", CompactCurrencyFormatter.formatPaise(-120_000L, includeSymbol = false))
        assertEquals("-₹3.4L", CompactCurrencyFormatter.formatPaise(-34_000_000L))
        assertEquals("-₹1.1Cr", CompactCurrencyFormatter.formatPaise(-1_100_000_000L))
    }

    @Test
    fun `formatRupees formats double values accurately`() {
        assertEquals("₹1.5K", CompactCurrencyFormatter.formatRupees(1500.0))
        assertEquals("2.5L", CompactCurrencyFormatter.formatRupees(250000.0, includeSymbol = false))
    }
}
