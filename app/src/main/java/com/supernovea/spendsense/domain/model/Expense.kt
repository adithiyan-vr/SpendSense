/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

import java.time.Instant
import java.time.LocalDate

data class Expense(
    val id: Long = 0,
    val amountMinor: Long,
    val type: ExpenseType,
    val categoryId: Long,
    val note: String? = null,
    val occurredAt: Instant,
    val occurredDate: LocalDate,
    val paymentMethod: PaymentMethod = PaymentMethod.OTHER,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
)
