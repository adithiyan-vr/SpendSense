/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class MoneyTest {

    @Test
    fun `format returns clean amount without decimals when paise is zero`() {
        assertEquals("₹520", Money.format(52000L))
        assertEquals("₹0", Money.format(0L))
        assertEquals("₹10", Money.format(1000L))
    }

    @Test
    fun `format includes two decimal places when paise is non-zero`() {
        assertEquals("₹520.50", Money.format(52050L))
        assertEquals("₹520.05", Money.format(52005L))
        assertEquals("₹0.99", Money.format(99L))
    }

    @Test
    fun `format respects alwaysShowDecimals flag`() {
        assertEquals("₹520.00", Money.format(52000L, alwaysShowDecimals = true))
        assertEquals("₹0.00", Money.format(0L, alwaysShowDecimals = true))
    }

    @Test
    fun `format formats Indian number groupings correctly`() {
        assertEquals("₹1,00,000", Money.format(10000000L))
        assertEquals("₹12,34,567.89", Money.format(123456789L))
    }

    @Test
    fun `format handles negative amounts`() {
        assertEquals("-₹500", Money.format(-50000L))
        assertEquals("-₹500.50", Money.format(-50050L))
    }

    @Test
    fun `format respects custom currency symbol`() {
        assertEquals("$520", Money.format(52000L, currencySymbol = "$"))
        assertEquals("€100", Money.format(10000L, currencySymbol = "€"))
    }

    @Test
    fun `parseToMinor parses clean numeric strings`() {
        assertEquals(52000L, Money.parseToMinor("520"))
        assertEquals(52050L, Money.parseToMinor("520.50"))
        assertEquals(52050L, Money.parseToMinor("520.5"))
        assertEquals(52005L, Money.parseToMinor("520.05"))
    }

    @Test
    fun `parseToMinor rounds half-up to two decimal places`() {
        assertEquals(52056L, Money.parseToMinor("520.556"))
        assertEquals(52055L, Money.parseToMinor("520.554"))
    }

    @Test
    fun `parseToMinor strips currency symbols and commas`() {
        assertEquals(10000000L, Money.parseToMinor("₹1,00,000"))
        assertEquals(123456L, Money.parseToMinor("$ 1,234.56"))
    }

    @Test
    fun `parseToMinor rejects invalid strings and negative or zero amounts`() {
        assertNull(Money.parseToMinor(""))
        assertNull(Money.parseToMinor("   "))
        assertNull(Money.parseToMinor("abc"))
        assertNull(Money.parseToMinor("-50"))
        assertNull(Money.parseToMinor("0"))
    }

    @Test
    fun `parseToMinor allows zero when explicitly enabled`() {
        assertEquals(0L, Money.parseToMinor("0", allowZero = true))
        assertEquals(0L, Money.parseToMinor("0.00", allowZero = true))
    }

    @Test
    fun `minorToMajor and majorToMinor round trip accurately`() {
        val minor = 123456L
        val major = Money.minorToMajor(minor)
        assertEquals(BigDecimal("1234.56"), major)
        assertEquals(minor, Money.majorToMinor(major))
    }

    @Test
    fun `toInputString formats correctly for editable fields`() {
        assertEquals("520", Money.toInputString(52000L))
        assertEquals("520.50", Money.toInputString(52050L))
        assertEquals("0", Money.toInputString(0L))
        assertEquals("-50", Money.toInputString(-5000L))
    }
}
