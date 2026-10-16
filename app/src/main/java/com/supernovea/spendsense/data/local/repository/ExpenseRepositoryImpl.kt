/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.repository

import com.supernovea.spendsense.core.di.IoDispatcher
import com.supernovea.spendsense.data.local.dao.ExpenseDao
import com.supernovea.spendsense.data.local.mapper.toDomain
import com.supernovea.spendsense.data.local.mapper.toEntity
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ExpenseRepository {

    override fun observeBetween(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<ExpenseWithCategory>> {
        return expenseDao.observeBetween(startDate.toEpochDay(), endDate.toEpochDay())
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeAll(): Flow<List<ExpenseWithCategory>> {
        return expenseDao.observeAll()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeById(id: Long): Flow<ExpenseWithCategory?> {
        return expenseDao.observeById(id)
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override suspend fun getById(id: Long): ExpenseWithCategory? = withContext(ioDispatcher) {
        expenseDao.getById(id)?.toDomain()
    }

    override suspend fun insertExpense(expense: Expense): Long = withContext(ioDispatcher) {
        expenseDao.insert(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) = withContext(ioDispatcher) {
        expenseDao.update(expense.toEntity())
    }

    override suspend fun softDeleteExpense(id: Long, timestamp: Instant) = withContext(ioDispatcher) {
        expenseDao.softDelete(id, timestamp.toEpochMilli())
    }

    override suspend fun restoreExpense(id: Long, timestamp: Instant) = withContext(ioDispatcher) {
        expenseDao.restore(id, timestamp.toEpochMilli())
    }

    override suspend fun hardDeleteExpense(id: Long) = withContext(ioDispatcher) {
        expenseDao.hardDelete(id)
    }

    override suspend fun purgeDeletedBefore(cutoff: Instant): Int = withContext(ioDispatcher) {
        expenseDao.purgeDeletedBefore(cutoff.toEpochMilli())
    }

    override suspend fun deleteAllExpenses(): Int = withContext(ioDispatcher) {
        expenseDao.deleteAll()
    }

    override suspend fun count(): Int = withContext(ioDispatcher) {
        expenseDao.count()
    }
}
