/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.repository

import com.supernovea.spendsense.domain.model.IncomeSource
import kotlinx.coroutines.flow.Flow

interface IncomeSourceRepository {
    fun observeAll(): Flow<List<IncomeSource>>
    fun observeActive(): Flow<List<IncomeSource>>
    suspend fun getById(id: Long): IncomeSource?
    suspend fun getByName(name: String): IncomeSource?
    suspend fun getByDefaultKey(defaultKey: String): IncomeSource?
    suspend fun upsertIncomeSource(source: IncomeSource): Long
    suspend fun updateIncomeSource(source: IncomeSource)
    suspend fun setArchived(id: Long, isArchived: Boolean)
    suspend fun deleteIncomeSource(source: IncomeSource)
    suspend fun count(): Int
}
