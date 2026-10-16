/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.repository

import com.supernovea.spendsense.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>
    fun observeActive(): Flow<List<Category>>
    suspend fun getById(id: Long): Category?
    suspend fun getByName(name: String): Category?
    suspend fun upsertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun updateSortOrder(categories: List<Category>)
    suspend fun setArchived(id: Long, isArchived: Boolean)
    suspend fun deleteCategory(category: Category)
    suspend fun count(): Int
}
