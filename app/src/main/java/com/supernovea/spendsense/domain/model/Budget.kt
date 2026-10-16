/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

data class Budget(
    val monthKey: String, // "yyyy-MM", e.g. "2026-10"
    val totalLimitMinor: Long,
    val wasteLimitMinor: Long? = null,
)
