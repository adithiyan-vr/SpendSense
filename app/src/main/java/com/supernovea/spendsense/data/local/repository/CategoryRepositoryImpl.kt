/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.repository

import com.supernovea.spendsense.core.di.IoDispatcher
import com.supernovea.spendsense.data.local.dao.CategoryDao
import com.supernovea.spendsense.data.local.mapper.toDomain
import com.supernovea.spendsense.data.local.mapper.toEntity
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CategoryRepository {

    override fun observeAll(): Flow<List<Category>> {
        return categoryDao.observeAll()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeActive(): Flow<List<Category>> {
        return categoryDao.observeActive()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun getById(id: Long): Category? = withContext(ioDispatcher) {
        categoryDao.getById(id)?.toDomain()
    }

    override suspend fun getByName(name: String): Category? = withContext(ioDispatcher) {
        categoryDao.getByName(name)?.toDomain()
    }

    override suspend fun upsertCategory(category: Category): Long = withContext(ioDispatcher) {
        categoryDao.insert(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) = withContext(ioDispatcher) {
        categoryDao.update(category.toEntity())
    }

    override suspend fun updateSortOrder(categories: List<Category>): Unit = withContext(ioDispatcher) {
        val entities = categories.mapIndexed { index, cat ->
            cat.copy(sortOrder = index).toEntity()
        }
        categoryDao.insertAll(entities)
        Unit
    }

    override suspend fun setArchived(id: Long, isArchived: Boolean) = withContext(ioDispatcher) {
        val current = categoryDao.getById(id) ?: return@withContext
        categoryDao.update(current.copy(isArchived = isArchived))
    }

    override suspend fun deleteCategory(category: Category) = withContext(ioDispatcher) {
        categoryDao.delete(category.toEntity())
    }

    override suspend fun count(): Int = withContext(ioDispatcher) {
        categoryDao.count()
    }
}
