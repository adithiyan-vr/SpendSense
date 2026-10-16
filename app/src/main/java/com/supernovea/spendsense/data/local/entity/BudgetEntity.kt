/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    @ColumnInfo(name = "month_key")
    val monthKey: String,
    @ColumnInfo(name = "total_limit_minor")
    val totalLimitMinor: Long,
    @ColumnInfo(name = "waste_limit_minor")
    val wasteLimitMinor: Long? = null,
)
