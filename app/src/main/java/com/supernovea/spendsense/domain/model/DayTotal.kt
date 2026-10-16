/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

import java.time.LocalDate

/**
 * Aggregated total for a single calendar day.
 */
data class DayTotal(
    val date: LocalDate,
    val totalMinor: Long,
    val needMinor: Long,
    val wasteMinor: Long,
)
