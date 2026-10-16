/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

/**
 * Breakdown of spending for a category within an evaluation period.
 */
data class CategoryShare(
    val category: Category,
    val totalMinor: Long,
    val needMinor: Long,
    val wasteMinor: Long,
    val sharePct: Double,
    val defaultType: ExpenseType,
)
