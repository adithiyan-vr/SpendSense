/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.CategoryRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class BudgetAndCategoryUseCasesTest {

    private val budgetRepository = mockk<BudgetRepository>()
    private val expenseRepository = mockk<ExpenseRepository>()
    private val categoryRepository = mockk<CategoryRepository>()

    private val category = Category(
        id = 1L,
        name = "Food",
        iconKey = "ic_food",
        colorHex = "#FF5722",
        defaultType = ExpenseType.NEED,
        isArchived = false,
        isDefault = true,
    )

    private suspend inline fun <reified T : Throwable> assertThrowsSuspend(crossinline block: suspend () -> Unit) {
        var caught = false
        try {
            block()
        } catch (e: Throwable) {
            if (e is T) {
                caught = true
            } else {
                throw AssertionError("Expected ${T::class.simpleName} but got ${e::class.simpleName}", e)
            }
        }
        if (!caught) {
            throw AssertionError("Expected ${T::class.simpleName} was not thrown")
        }
    }

    @Test
    fun `GetBudgetStatusUseCase validates budget limits when setting budget`() = runTest {
        val useCase = GetBudgetStatusUseCase(budgetRepository, expenseRepository)

        // Total <= 0 throws
        assertThrowsSuspend<IllegalArgumentException> {
            useCase.setBudget(Budget("2026-10", 0L))
        }

        // Waste > Total throws
        assertThrowsSuspend<IllegalArgumentException> {
            useCase.setBudget(Budget("2026-10", 50000L, wasteLimitMinor = 60000L))
        }

        // Valid budget succeeds
        coEvery { budgetRepository.setBudget(any()) } returns Unit
        useCase.setBudget(Budget("2026-10", 50000L, wasteLimitMinor = 20000L))
        coVerify(exactly = 1) { budgetRepository.setBudget(any()) }
    }

    @Test
    fun `GetBudgetStatusUseCase calculates progress and overflow accurately`() = runTest {
        val useCase = GetBudgetStatusUseCase(budgetRepository, expenseRepository)
        val month = YearMonth.of(2026, 10)

        val budget = Budget(
            monthKey = "2026-10",
            totalLimitMinor = 100000L,
            wasteLimitMinor = 30000L,
        )

        val expenses = listOf(
            ExpenseWithCategory(
                expense = Expense(
                    id = 1L,
                    amountMinor = 70000L,
                    type = ExpenseType.NEED,
                    categoryId = 1L,
                    occurredAt = Instant.now(),
                    occurredDate = LocalDate.of(2026, 10, 5),
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                ),
                category = category,
            ),
            ExpenseWithCategory(
                expense = Expense(
                    id = 2L,
                    amountMinor = 35000L, // Waste exceeds 30000L!
                    type = ExpenseType.WASTE,
                    categoryId = 1L,
                    occurredAt = Instant.now(),
                    occurredDate = LocalDate.of(2026, 10, 10),
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                ),
                category = category,
            ),
        )

        every { budgetRepository.observeByMonth("2026-10") } returns flowOf(budget)
        every { expenseRepository.observeBetween(any(), any()) } returns flowOf(expenses)

        val status = useCase(month, today = LocalDate.of(2026, 10, 15)).first()

        assertNotNull(status)
        assertEquals(105000L, status?.totalSpentMinor)
        assertEquals(35000L, status?.wasteSpentMinor)
        assertEquals(105.0, status?.totalProgressPct ?: 0.0, 0.001)
        assertTrue(status?.isOverTotal == true)
        assertTrue(status?.isOverWaste == true)
        assertEquals(16, status?.daysRemainingInMonth)
    }

    @Test
    fun `GetBudgetStatusUseCase returns null when no budget is configured`() = runTest {
        val useCase = GetBudgetStatusUseCase(budgetRepository, expenseRepository)
        every { budgetRepository.observeByMonth(any()) } returns flowOf(null)
        every { expenseRepository.observeBetween(any(), any()) } returns flowOf(emptyList())

        val status = useCase(YearMonth.of(2026, 10)).first()
        assertNull(status)
    }

    @Test
    fun `ManageCategoriesUseCase validates name and delegates CRUD operations`() = runTest {
        val useCase = ManageCategoriesUseCase(categoryRepository)

        // Empty name throws
        assertThrowsSuspend<IllegalArgumentException> {
            useCase.addCategory("   ", "icon", "#fff", ExpenseType.NEED)
        }

        // Add category
        val categorySlot = slot<Category>()
        coEvery { categoryRepository.upsertCategory(capture(categorySlot)) } returns 5L

        val id = useCase.addCategory("Books", "ic_book", "#3F51B5", ExpenseType.NEED)
        assertEquals(5L, id)
        assertEquals("Books", categorySlot.captured.name)
        assertEquals(ExpenseType.NEED, categorySlot.captured.defaultType)

        // Archive category
        coEvery { categoryRepository.setArchived(5L, true) } returns Unit
        useCase.archiveCategory(5L, true)
        coVerify(exactly = 1) { categoryRepository.setArchived(5L, true) }

        // Reorder categories
        val list = listOf(category)
        coEvery { categoryRepository.updateSortOrder(list) } returns Unit
        useCase.reorderCategories(list)
        coVerify(exactly = 1) { categoryRepository.updateSortOrder(list) }
    }
}
