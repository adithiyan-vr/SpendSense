/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.supernovea.spendsense.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE month_key = :monthKey")
    fun observeByMonth(monthKey: String): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE month_key = :monthKey")
    suspend fun getByMonth(monthKey: String): BudgetEntity?

    @Query("SELECT * FROM budgets ORDER BY month_key DESC")
    fun observeAll(): Flow<List<BudgetEntity>>

    @Upsert
    suspend fun upsert(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE month_key = :monthKey")
    suspend fun deleteByMonth(monthKey: String)
}
