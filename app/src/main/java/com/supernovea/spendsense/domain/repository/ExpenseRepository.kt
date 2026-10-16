/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.repository

import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

interface ExpenseRepository {
    fun observeBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<ExpenseWithCategory>>
    fun observeAll(): Flow<List<ExpenseWithCategory>>
    fun observeById(id: Long): Flow<ExpenseWithCategory?>
    suspend fun getById(id: Long): ExpenseWithCategory?
    suspend fun insertExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun softDeleteExpense(id: Long, timestamp: Instant = Instant.now())
    suspend fun restoreExpense(id: Long, timestamp: Instant = Instant.now())
    suspend fun hardDeleteExpense(id: Long)
    suspend fun purgeDeletedBefore(cutoff: Instant): Int
    suspend fun deleteAllExpenses(): Int
    suspend fun count(): Int
}
