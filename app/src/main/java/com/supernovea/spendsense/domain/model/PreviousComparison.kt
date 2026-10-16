/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

/**
 * Period-over-period comparison metrics.
 */
data class PreviousComparison(
    val previousTotalMinor: Long,
    val previousNeedMinor: Long,
    val previousWasteMinor: Long,
    val previousWasteRatioPct: Double,
    /**
     * Percentage change in total spend: ((Current - Previous) / Previous) * 100.
     * Null if previous period total was 0 (per PRD §8).
     */
    val totalDeltaPct: Double?,
    /**
     * Change in waste ratio in percentage points: Current Waste Ratio - Previous Waste Ratio.
     */
    val wasteRatioDeltaPts: Double,
    val previousIncomeMinor: Long = 0L,
    val previousNetMinor: Long = 0L,
    val previousSavingsRatePct: Double? = null,
    val incomeDeltaPct: Double? = null,
    val netDeltaPct: Double? = null,
    val savingsRateDeltaPts: Double? = null,
)
