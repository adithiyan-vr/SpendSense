/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.repository

import com.supernovea.spendsense.core.di.IoDispatcher
import com.supernovea.spendsense.data.local.dao.BudgetDao
import com.supernovea.spendsense.data.local.mapper.toDomain
import com.supernovea.spendsense.data.local.mapper.toEntity
import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.repository.BudgetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BudgetRepository {

    override fun observeByMonth(monthKey: String): Flow<Budget?> {
        return budgetDao.observeByMonth(monthKey)
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override suspend fun getByMonth(monthKey: String): Budget? = withContext(ioDispatcher) {
        budgetDao.getByMonth(monthKey)?.toDomain()
    }

    override fun observeAll(): Flow<List<Budget>> {
        return budgetDao.observeAll()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun setBudget(budget: Budget) = withContext(ioDispatcher) {
        budgetDao.upsert(budget.toEntity())
    }

    override suspend fun deleteBudget(monthKey: String) = withContext(ioDispatcher) {
        budgetDao.deleteByMonth(monthKey)
    }
}
