/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ExpenseWithCategoryEntity(
    @Embedded
    val expense: ExpenseEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id",
    )
    val category: CategoryEntity,
)
