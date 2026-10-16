/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

import java.time.LocalDate
import java.time.YearMonth

/**
 * Represents an analytical evaluation period.
 * Can be a single week starting at [start] or a calendar month [ym].
 */
sealed interface Period {
    data class Week(val start: LocalDate) : Period
    data class Month(val ym: YearMonth) : Period
    data class Custom(val startDate: LocalDate, val endDate: LocalDate) : Period {
        init {
            require(!startDate.isAfter(endDate)) {
                "startDate ($startDate) cannot be after endDate ($endDate)"
            }
        }
    }
}
