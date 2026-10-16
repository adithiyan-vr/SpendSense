/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

/**
 * Compact Indian currency formatter for chart axes, data markers, and condensed metric cards.
 * Formats values into Indian standard denominations:
 * - >= 1 Crore (1,00,00,000): "Cr" (e.g., 1.1Cr)
 * - >= 1 Lakh (1,00,000): "L" (e.g., 3.4L)
 * - >= 1 Thousand (1,000): "K" (e.g., 1.2K)
 * - < 1 Thousand: whole value (e.g., 500)
 */
object CompactCurrencyFormatter {

    private const val CRORE = 10_000_000.0
    private const val LAKH = 100_000.0
    private const val THOUSAND = 1_000.0

    private val symbols = DecimalFormatSymbols(Locale.US)
    private val decimalFormat = DecimalFormat("#.#", symbols)

    /**
     * Formats an amount in paise (minor units) to a compact string.
     *
     * @param amountMinor Amount in paise (Long)
     * @param currencySymbol Currency symbol (default: "₹")
     * @param includeSymbol Whether to prefix with currency symbol (default: true)
     */
    fun formatPaise(
        amountMinor: Long,
        currencySymbol: String = "₹",
        includeSymbol: Boolean = true,
    ): String {
        val rupeeValue = amountMinor / 100.0
        return formatRupees(rupeeValue, currencySymbol, includeSymbol)
    }

    /**
     * Formats a major currency amount (rupees) to a compact string.
     *
     * @param amountRupees Amount in rupees (Double)
     * @param currencySymbol Currency symbol (default: "₹")
     * @param includeSymbol Whether to prefix with currency symbol (default: true)
     */
    fun formatRupees(
        amountRupees: Double,
        currencySymbol: String = "₹",
        includeSymbol: Boolean = true,
    ): String {
        val isNegative = amountRupees < 0
        val absValue = abs(amountRupees)

        val (scaledValue, suffix) = when {
            absValue >= CRORE -> Pair(absValue / CRORE, "Cr")
            absValue >= LAKH -> Pair(absValue / LAKH, "L")
            absValue >= THOUSAND -> Pair(absValue / THOUSAND, "K")
            else -> Pair(absValue, "")
        }

        val formattedNum = if (suffix.isEmpty()) {
            absValue.toLong().toString()
        } else {
            decimalFormat.format(scaledValue) + suffix
        }

        val prefix = when {
            isNegative && includeSymbol -> "-$currencySymbol"
            isNegative -> "-"
            includeSymbol -> currencySymbol
            else -> ""
        }

        return "$prefix$formattedNum"
    }
}
