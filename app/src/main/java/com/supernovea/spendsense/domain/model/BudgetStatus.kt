/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

data class BudgetStatus(
    val budget: Budget,
    val totalSpentMinor: Long,
    val wasteSpentMinor: Long,
    val totalProgressPct: Double,
    val wasteProgressPct: Double?,
    val isOverTotal: Boolean,
    val isOverWaste: Boolean,
    val daysRemainingInMonth: Int,
)
