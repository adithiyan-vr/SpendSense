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
import com.supernovea.spendsense.data.local.entity.IncomeEntity
import com.supernovea.spendsense.data.local.entity.IncomeWithSourceEntity
import kotlinx.coroutines.flow.Flow

data class SourceSumTuple(
    val sourceId: Long,
    val totalMinor: Long,
)

@Dao
interface IncomeDao {

    @Transaction
    @Query("SELECT * FROM incomes WHERE deleted_at IS NULL AND received_date BETWEEN :startDay AND :endDay ORDER BY received_at DESC")
    fun observeBetween(startDay: Long, endDay: Long): Flow<List<IncomeWithSourceEntity>>

    @Transaction
    @Query("SELECT * FROM incomes WHERE deleted_at IS NULL ORDER BY received_at DESC")
    fun observeAll(): Flow<List<IncomeWithSourceEntity>>

    @Transaction
    @Query("SELECT * FROM incomes WHERE id = :id AND deleted_at IS NULL")
    fun observeById(id: Long): Flow<IncomeWithSourceEntity?>

    @Transaction
    @Query("SELECT * FROM incomes WHERE id = :id")
    suspend fun getById(id: Long): IncomeWithSourceEntity?

    @Query("SELECT COALESCE(SUM(amount_minor), 0) FROM incomes WHERE deleted_at IS NULL AND received_date BETWEEN :startDay AND :endDay")
    fun observeTotalBetween(startDay: Long, endDay: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount_minor), 0) FROM incomes WHERE deleted_at IS NULL")
    fun observeTotalAllTime(): Flow<Long>

    @Query("SELECT source_id as sourceId, COALESCE(SUM(amount_minor), 0) as totalMinor FROM incomes WHERE deleted_at IS NULL AND received_date BETWEEN :startDay AND :endDay GROUP BY source_id")
    fun observeSumBySourceBetween(startDay: Long, endDay: Long): Flow<List<SourceSumTuple>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: IncomeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incomes: List<IncomeEntity>): List<Long>

    @Update
    suspend fun update(income: IncomeEntity)

    @Query("UPDATE incomes SET deleted_at = :now, updated_at = :now WHERE id = :id")
    suspend fun softDelete(id: Long, now: Long)

    @Query("UPDATE incomes SET deleted_at = NULL, updated_at = :now WHERE id = :id")
    suspend fun restore(id: Long, now: Long)

    @Query("DELETE FROM incomes WHERE id = :id")
    suspend fun hardDelete(id: Long)

    @Query("DELETE FROM incomes WHERE deleted_at IS NOT NULL AND deleted_at < :cutoff")
    suspend fun purgeDeletedBefore(cutoff: Long): Int

    @Query("DELETE FROM incomes")
    suspend fun deleteAll(): Int

    @Query("SELECT COUNT(*) FROM incomes WHERE deleted_at IS NULL")
    suspend fun count(): Int
}
