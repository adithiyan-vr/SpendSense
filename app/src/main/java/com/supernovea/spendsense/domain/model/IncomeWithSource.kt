/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

data class IncomeWithSource(
    val income: Income,
    val source: IncomeSource,
)
