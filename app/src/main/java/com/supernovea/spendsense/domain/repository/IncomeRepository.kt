/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.repository

import com.supernovea.spendsense.domain.model.Income
import com.supernovea.spendsense.domain.model.IncomeWithSource
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

interface IncomeRepository {
    fun observeBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<IncomeWithSource>>
    fun observeAll(): Flow<List<IncomeWithSource>>
    fun observeById(id: Long): Flow<IncomeWithSource?>
    suspend fun getById(id: Long): IncomeWithSource?
    fun observeTotalBetween(startDate: LocalDate, endDate: LocalDate): Flow<Long>
    fun observeTotalAllTime(): Flow<Long>
    suspend fun insertIncome(income: Income): Long
    suspend fun updateIncome(income: Income)
    suspend fun softDeleteIncome(id: Long, timestamp: Instant = Instant.now())
    suspend fun restoreIncome(id: Long, timestamp: Instant = Instant.now())
    suspend fun hardDeleteIncome(id: Long)
    suspend fun purgeDeletedBefore(cutoff: Instant): Int
    suspend fun deleteAllIncomes(): Int
    suspend fun count(): Int
}
