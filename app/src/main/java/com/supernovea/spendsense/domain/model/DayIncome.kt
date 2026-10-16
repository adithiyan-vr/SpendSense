/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

import java.time.LocalDate

/**
 * Aggregated total income received for a single calendar day.
 */
data class DayIncome(
    val date: LocalDate,
    val totalMinor: Long,
)
