/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.repository

import com.supernovea.spendsense.core.di.IoDispatcher
import com.supernovea.spendsense.data.local.dao.IncomeDao
import com.supernovea.spendsense.data.local.mapper.toDomain
import com.supernovea.spendsense.data.local.mapper.toEntity
import com.supernovea.spendsense.domain.model.Income
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.repository.IncomeRepository
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
class IncomeRepositoryImpl @Inject constructor(
    private val incomeDao: IncomeDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : IncomeRepository {

    override fun observeBetween(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<IncomeWithSource>> {
        return incomeDao.observeBetween(startDate.toEpochDay(), endDate.toEpochDay())
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeAll(): Flow<List<IncomeWithSource>> {
        return incomeDao.observeAll()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeById(id: Long): Flow<IncomeWithSource?> {
        return incomeDao.observeById(id)
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override suspend fun getById(id: Long): IncomeWithSource? = withContext(ioDispatcher) {
        incomeDao.getById(id)?.toDomain()
    }

    override fun observeTotalBetween(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<Long> {
        return incomeDao.observeTotalBetween(startDate.toEpochDay(), endDate.toEpochDay())
            .flowOn(ioDispatcher)
    }

    override fun observeTotalAllTime(): Flow<Long> {
        return incomeDao.observeTotalAllTime().flowOn(ioDispatcher)
    }

    override suspend fun insertIncome(income: Income): Long = withContext(ioDispatcher) {
        incomeDao.insert(income.toEntity())
    }

    override suspend fun updateIncome(income: Income) = withContext(ioDispatcher) {
        incomeDao.update(income.toEntity())
    }

    override suspend fun softDeleteIncome(id: Long, timestamp: Instant) = withContext(ioDispatcher) {
        incomeDao.softDelete(id, timestamp.toEpochMilli())
    }

    override suspend fun restoreIncome(id: Long, timestamp: Instant) = withContext(ioDispatcher) {
        incomeDao.restore(id, timestamp.toEpochMilli())
    }

    override suspend fun hardDeleteIncome(id: Long) = withContext(ioDispatcher) {
        incomeDao.hardDelete(id)
    }

    override suspend fun purgeDeletedBefore(cutoff: Instant): Int = withContext(ioDispatcher) {
        incomeDao.purgeDeletedBefore(cutoff.toEpochMilli())
    }

    override suspend fun deleteAllIncomes(): Int = withContext(ioDispatcher) {
        incomeDao.deleteAll()
    }

    override suspend fun count(): Int = withContext(ioDispatcher) {
        incomeDao.count()
    }
}
