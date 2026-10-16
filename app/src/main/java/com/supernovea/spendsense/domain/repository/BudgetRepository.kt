/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.repository

import com.supernovea.spendsense.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeByMonth(monthKey: String): Flow<Budget?>
    suspend fun getByMonth(monthKey: String): Budget?
    fun observeAll(): Flow<List<Budget>>
    suspend fun setBudget(budget: Budget)
    suspend fun deleteBudget(monthKey: String)
}
