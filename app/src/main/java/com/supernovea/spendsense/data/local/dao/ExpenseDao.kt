/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.supernovea.spendsense.data.local.entity.ExpenseEntity
import com.supernovea.spendsense.data.local.entity.ExpenseWithCategoryEntity
import kotlinx.coroutines.flow.Flow

data class TypeSumTuple(
    val type: String,
    val totalMinor: Long,
)

@Dao
interface ExpenseDao {

    @Transaction
    @Query("SELECT * FROM expenses WHERE deleted_at IS NULL AND occurred_date BETWEEN :startDay AND :endDay ORDER BY occurred_at DESC")
    fun observeBetween(startDay: Long, endDay: Long): Flow<List<ExpenseWithCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM expenses WHERE deleted_at IS NULL ORDER BY occurred_at DESC")
    fun observeAll(): Flow<List<ExpenseWithCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :id AND deleted_at IS NULL")
    fun observeById(id: Long): Flow<ExpenseWithCategoryEntity?>

    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Long): ExpenseWithCategoryEntity?

    @Query("SELECT type, COALESCE(SUM(amount_minor), 0) as totalMinor FROM expenses WHERE deleted_at IS NULL AND occurred_date BETWEEN :startDay AND :endDay GROUP BY type")
    fun observeSumByTypeBetween(startDay: Long, endDay: Long): Flow<List<TypeSumTuple>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ExpenseEntity>): List<Long>

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("UPDATE expenses SET deleted_at = :now, updated_at = :now WHERE id = :id")
    suspend fun softDelete(id: Long, now: Long)

    @Query("UPDATE expenses SET deleted_at = NULL, updated_at = :now WHERE id = :id")
    suspend fun restore(id: Long, now: Long)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun hardDelete(id: Long)

    @Query("DELETE FROM expenses WHERE deleted_at IS NOT NULL AND deleted_at < :cutoff")
    suspend fun purgeDeletedBefore(cutoff: Long): Int

    @Query("DELETE FROM expenses")
    suspend fun deleteAll(): Int

    @Query("SELECT COUNT(*) FROM expenses WHERE deleted_at IS NULL")
    suspend fun count(): Int
}
