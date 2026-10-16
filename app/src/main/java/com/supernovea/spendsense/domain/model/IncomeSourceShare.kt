/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

/**
 * Breakdown of income received for an income source within an evaluation period.
 */
data class IncomeSourceShare(
    val source: IncomeSource,
    val totalMinor: Long,
    val sharePct: Double,
)
