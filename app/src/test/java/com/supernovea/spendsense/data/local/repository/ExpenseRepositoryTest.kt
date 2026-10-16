/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.repository

import com.supernovea.spendsense.data.local.dao.ExpenseDao
import com.supernovea.spendsense.data.local.entity.CategoryEntity
import com.supernovea.spendsense.data.local.entity.ExpenseEntity
import com.supernovea.spendsense.data.local.entity.ExpenseWithCategoryEntity
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.PaymentMethod
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
import java.time.Instant
import java.time.LocalDate

class ExpenseRepositoryTest {

    private val expenseDao = mockk<ExpenseDao>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ExpenseRepositoryImpl

    @Before
    fun setup() {
        repository = ExpenseRepositoryImpl(expenseDao, testDispatcher)
    }

    @Test
    fun `observeBetween queries dao with correct epoch days and maps entities`() = runTest(testDispatcher) {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 7)

        val sampleRelation = ExpenseWithCategoryEntity(
            expense = ExpenseEntity(
                id = 1L,
                amountMinor = 12000L,
                type = "NEED",
                categoryId = 1L,
                occurredAt = 1770000000000L,
                occurredDate = start.toEpochDay(),
                paymentMethod = "UPI",
                createdAt = 1770000000000L,
                updatedAt = 1770000000000L,
            ),
            category = CategoryEntity(
                id = 1L,
                name = "Food",
                iconKey = "restaurant",
                colorHex = "#F59E0B",
                defaultType = "NEED",
            ),
        )

        every {
            expenseDao.observeBetween(start.toEpochDay(), end.toEpochDay())
        } returns flowOf(listOf(sampleRelation))

        val result = repository.observeBetween(start, end).first()

        assertEquals(1, result.size)
        assertEquals(12000L, result[0].expense.amountMinor)
        assertEquals(ExpenseType.NEED, result[0].expense.type)
        assertEquals("Food", result[0].category.name)
    }

    @Test
    fun `insertExpense delegates to dao`() = runTest(testDispatcher) {
        val expense = Expense(
            id = 0L,
            amountMinor = 5000L,
            type = ExpenseType.WASTE,
            categoryId = 8L,
            occurredAt = Instant.now(),
            occurredDate = LocalDate.now(),
            paymentMethod = PaymentMethod.CASH,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        )

        coEvery { expenseDao.insert(any()) } returns 42L

        val insertedId = repository.insertExpense(expense)

        assertEquals(42L, insertedId)
        coVerify { expenseDao.insert(match { it.amountMinor == 5000L && it.type == "WASTE" }) }
    }

    @Test
    fun `softDeleteExpense delegates to dao with epoch millis`() = runTest(testDispatcher) {
        val timestamp = Instant.ofEpochMilli(1770000000000L)
        coEvery { expenseDao.softDelete(99L, 1770000000000L) } returns Unit

        repository.softDeleteExpense(99L, timestamp)

        coVerify { expenseDao.softDelete(99L, 1770000000000L) }
    }
}
