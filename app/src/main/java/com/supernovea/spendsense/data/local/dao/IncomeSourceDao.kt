/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.supernovea.spendsense.data.local.entity.IncomeSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeSourceDao {

    @Query("SELECT * FROM income_sources ORDER BY sort_order ASC, name ASC")
    fun observeAll(): Flow<List<IncomeSourceEntity>>

    @Query("SELECT * FROM income_sources WHERE is_archived = 0 ORDER BY sort_order ASC, name ASC")
    fun observeActive(): Flow<List<IncomeSourceEntity>>

    @Query("SELECT * FROM income_sources WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): IncomeSourceEntity?

    @Query("SELECT * FROM income_sources WHERE default_key = :defaultKey LIMIT 1")
    suspend fun getByDefaultKey(defaultKey: String): IncomeSourceEntity?

    @Query("SELECT * FROM income_sources WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): IncomeSourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(source: IncomeSourceEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(sources: List<IncomeSourceEntity>): List<Long>

    @Update
    suspend fun update(source: IncomeSourceEntity)

    @Delete
    suspend fun delete(source: IncomeSourceEntity)

    @Query("UPDATE income_sources SET is_archived = :isArchived WHERE id = :id")
    suspend fun setArchived(id: Long, isArchived: Boolean)

    @Query("SELECT COUNT(*) FROM income_sources")
    suspend fun count(): Int
}
