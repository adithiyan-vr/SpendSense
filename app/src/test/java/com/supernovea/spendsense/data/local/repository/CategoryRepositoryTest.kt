/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.repository

import com.supernovea.spendsense.data.local.dao.CategoryDao
import com.supernovea.spendsense.data.local.entity.CategoryEntity
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CategoryRepositoryTest {

    private val categoryDao = mockk<CategoryDao>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CategoryRepositoryImpl

    @Before
    fun setup() {
        repository = CategoryRepositoryImpl(categoryDao, testDispatcher)
    }

    @Test
    fun `observeActive maps entities to domain categories`() = runTest(testDispatcher) {
        val entities = listOf(
            CategoryEntity(
                id = 1L,
                name = "Food",
                iconKey = "restaurant",
                colorHex = "#F59E0B",
                defaultType = "NEED",
                isArchived = false,
                isDefault = true,
                sortOrder = 1,
            ),
        )

        every { categoryDao.observeActive() } returns flowOf(entities)

        val result = repository.observeActive().first()

        assertEquals(1, result.size)
        assertEquals("Food", result[0].name)
        assertEquals(ExpenseType.NEED, result[0].defaultType)
    }

    @Test
    fun `upsertCategory inserts entity and returns id`() = runTest(testDispatcher) {
        val category = Category(
            id = 0L,
            name = "Books",
            iconKey = "school",
            colorHex = "#6366F1",
            defaultType = ExpenseType.NEED,
        )

        coEvery { categoryDao.insert(any()) } returns 7L

        val id = repository.upsertCategory(category)

        assertEquals(7L, id)
        coVerify { categoryDao.insert(match { it.name == "Books" && it.defaultType == "NEED" }) }
    }
}
