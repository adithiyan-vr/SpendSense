/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

/**
 * Fundamental classification of spending:
 * - [NEED]: Essential or obligatory expense (Required).
 * - [WASTE]: Avoidable or low-value expense (Non-Required / Mal).
 */
enum class ExpenseType {
    NEED,
    WASTE,
}
