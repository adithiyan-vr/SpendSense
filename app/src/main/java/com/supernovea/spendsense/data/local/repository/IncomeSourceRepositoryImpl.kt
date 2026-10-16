/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.repository

import com.supernovea.spendsense.core.di.IoDispatcher
import com.supernovea.spendsense.data.local.dao.IncomeSourceDao
import com.supernovea.spendsense.data.local.mapper.toDomain
import com.supernovea.spendsense.data.local.mapper.toEntity
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeSourceRepositoryImpl @Inject constructor(
    private val incomeSourceDao: IncomeSourceDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : IncomeSourceRepository {

    override fun observeAll(): Flow<List<IncomeSource>> {
        return incomeSourceDao.observeAll()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeActive(): Flow<List<IncomeSource>> {
        return incomeSourceDao.observeActive()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun getById(id: Long): IncomeSource? = withContext(ioDispatcher) {
        incomeSourceDao.getById(id)?.toDomain()
    }

    override suspend fun getByName(name: String): IncomeSource? = withContext(ioDispatcher) {
        incomeSourceDao.getByName(name)?.toDomain()
    }

    override suspend fun getByDefaultKey(defaultKey: String): IncomeSource? = withContext(ioDispatcher) {
        incomeSourceDao.getByDefaultKey(defaultKey)?.toDomain()
    }

    override suspend fun upsertIncomeSource(source: IncomeSource): Long = withContext(ioDispatcher) {
        incomeSourceDao.insert(source.toEntity())
    }

    override suspend fun updateIncomeSource(source: IncomeSource) = withContext(ioDispatcher) {
        incomeSourceDao.update(source.toEntity())
    }

    override suspend fun setArchived(id: Long, isArchived: Boolean) = withContext(ioDispatcher) {
        incomeSourceDao.setArchived(id, isArchived)
    }

    override suspend fun deleteIncomeSource(source: IncomeSource) = withContext(ioDispatcher) {
        incomeSourceDao.delete(source.toEntity())
    }

    override suspend fun count(): Int = withContext(ioDispatcher) {
        incomeSourceDao.count()
    }
}
