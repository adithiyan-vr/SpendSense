/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

/**
 * Utility functions for handling money amounts stored in minor units (paise).
 * All monetary values in SpendSense are stored as [Long] minor units (1 INR = 100 paise)
 * to avoid floating-point inaccuracies.
 */
object Money {

    /**
     * Formats an amount in paise to a display string with currency symbol.
     * Implements Indian numbering system grouping (e.g. ₹1,00,000).
     * Examples (with ₹):
     * - 52000L, alwaysShowDecimals = false -> "₹520"
     * - 52050L, alwaysShowDecimals = false -> "₹520.50"
     * - 52000L, alwaysShowDecimals = true  -> "₹520.00"
     * - 10000000L                          -> "₹1,00,000"
     * - -50000L                            -> "-₹500"
     */
    fun format(
        amountMinor: Long,
        currencySymbol: String = "₹",
        alwaysShowDecimals: Boolean = false,
    ): String {
        val isNegative = amountMinor < 0
        val absMinor = abs(amountMinor)
        val hasFraction = absMinor % 100 != 0L

        val majorWhole = absMinor / 100
        val fractionPart = absMinor % 100

        val formattedInt = formatIndianGrouping(majorWhole.toString())

        val numberString = if (alwaysShowDecimals || hasFraction) {
            String.format(Locale.US, "%s.%02d", formattedInt, fractionPart)
        } else {
            formattedInt
        }

        val prefix = if (isNegative) "-$currencySymbol" else currencySymbol
        return "$prefix$numberString"
    }

    private fun formatIndianGrouping(digits: String): String {
        if (digits.length <= 3) return digits
        val lastThree = digits.takeLast(3)
        val remaining = digits.dropLast(3)
        val groupedRemaining = remaining.reversed().chunked(2).joinToString(",").reversed()
        return "$groupedRemaining,$lastThree"
    }

    /**
     * Parses a user input string into minor units (paise) safely using [BigDecimal].
     * Strips currency symbols, spaces, and commas. Rounds HALF_UP to 2 decimal places.
     *
     * @param input Raw text from user (e.g. "520", "520.50", "₹1,234.56")
     * @param allowZero If true, 0 is considered valid and returns 0L. If false, returns null for <= 0.
     * @return Amount in paise, or null if input is invalid or negative/zero (when allowZero = false).
     */
    fun parseToMinor(input: String, allowZero: Boolean = false): Long? {
        val cleaned = input
            .replace("₹", "")
            .replace("$", "")
            .replace("€", "")
            .replace("£", "")
            .replace(",", "")
            .trim()

        if (cleaned.isEmpty()) return null

        return try {
            val decimal = BigDecimal(cleaned).setScale(2, RoundingMode.HALF_UP)
            val minor = decimal.movePointRight(2).toLong()
            if (minor < 0L || (!allowZero && minor == 0L)) {
                null
            } else {
                minor
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Converts minor units to [BigDecimal] major units (rupees).
     */
    fun minorToMajor(amountMinor: Long): BigDecimal {
        return BigDecimal.valueOf(amountMinor).movePointLeft(2)
    }

    /**
     * Converts a [BigDecimal] amount in rupees to minor units (paise), rounding HALF_UP to 2 decimal places.
     */
    fun majorToMinor(major: BigDecimal): Long {
        return major.setScale(2, RoundingMode.HALF_UP).movePointRight(2).toLong()
    }

    /**
     * Formats an amount in paise to a clean string suitable for an editable text field.
     * E.g. 52000L -> "520", 52050L -> "520.50".
     */
    fun toInputString(amountMinor: Long): String {
        val absMinor = abs(amountMinor)
        val hasFraction = absMinor % 100 != 0L
        val major = BigDecimal.valueOf(absMinor).movePointLeft(2)
        val prefix = if (amountMinor < 0) "-" else ""
        return if (hasFraction) {
            "$prefix${major.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
        } else {
            "$prefix${major.toBigInteger().toString()}"
        }
    }

    /**
     * Convenience alias for [format].
     */
    fun formatPaise(amountMinor: Long, currencySymbol: String = "₹"): String {
        return format(amountMinor, currencySymbol)
    }

    /**
     * Parses user rupee text string into paise minor units.
     */
    fun rupeesToPaise(input: String): Long {
        return parseToMinor(input, allowZero = true) ?: 0L
    }

    /**
     * Converts paise minor units to [BigDecimal] rupees.
     */
    fun paiseToBigDecimal(amountMinor: Long): BigDecimal {
        return minorToMajor(amountMinor)
    }
}
