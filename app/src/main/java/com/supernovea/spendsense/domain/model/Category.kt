/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

data class Category(
    val id: Long = 0,
    val defaultKey: String? = null,
    val groupKey: String = "other",
    val name: String,
    val iconKey: String,
    val colorHex: String,
    val defaultType: ExpenseType,
    val isArchived: Boolean = false,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
)
