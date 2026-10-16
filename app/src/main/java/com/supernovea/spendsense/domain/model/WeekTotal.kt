/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

import java.time.LocalDate

/**
 * Aggregated total for a single week segment within a monthly analysis view.
 */
data class WeekTotal(
    val weekNumber: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalMinor: Long,
    val needMinor: Long,
    val wasteMinor: Long,
)
