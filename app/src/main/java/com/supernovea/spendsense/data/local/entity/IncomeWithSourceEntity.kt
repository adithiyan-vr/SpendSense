/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class IncomeWithSourceEntity(
    @Embedded
    val income: IncomeEntity,
    @Relation(
        parentColumn = "source_id",
        entityColumn = "id",
    )
    val source: IncomeSourceEntity,
)
