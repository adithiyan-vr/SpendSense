/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to manage categories: listing, creating custom categories,
 * updating, archiving, and reordering.
 */
class ManageCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {

    fun getCategories(includeArchived: Boolean = false): Flow<List<Category>> {
        return if (includeArchived) {
            categoryRepository.observeAll()
        } else {
            categoryRepository.observeActive()
        }
    }

    suspend fun getById(id: Long): Category? {
        return categoryRepository.getById(id)
    }

    suspend fun addCategory(
        name: String,
        iconKey: String,
        colorHex: String,
        defaultType: ExpenseType,
    ): Long {
        val trimmedName = name.trim()
        require(trimmedName.isNotEmpty()) { "Category name cannot be empty." }

        val category = Category(
            id = 0L,
            name = trimmedName,
            iconKey = iconKey.trim(),
            colorHex = colorHex.trim(),
            defaultType = defaultType,
            isArchived = false,
            isDefault = false,
        )
        return categoryRepository.upsertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        val trimmedName = category.name.trim()
        require(trimmedName.isNotEmpty()) { "Category name cannot be empty." }
        categoryRepository.updateCategory(category.copy(name = trimmedName))
    }

    suspend fun archiveCategory(id: Long, isArchived: Boolean = true) {
        categoryRepository.setArchived(id, isArchived)
    }

    suspend fun reorderCategories(categories: List<Category>) {
        categoryRepository.updateSortOrder(categories)
    }
}
