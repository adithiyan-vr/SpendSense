/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

import java.time.Instant
import java.time.LocalDate

data class Income(
    val id: Long = 0,
    val amountMinor: Long,
    val sourceId: Long,
    val note: String? = null,
    val receivedAt: Instant,
    val receivedDate: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
    val recurringRuleId: Long? = null,
)
