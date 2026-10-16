/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.PaymentMethod
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
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class ExpenseUseCasesTest {

    private val expenseRepository = mockk<ExpenseRepository>()
    private val categoryRepository = mockk<CategoryRepository>()

    private val category = Category(
        id = 1L,
        name = "Groceries",
        iconKey = "ic_food",
        colorHex = "#4CAF50",
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
    fun `AddExpenseUseCase validates positive amount`() = runTest {
        val useCase = AddExpenseUseCase(expenseRepository, categoryRepository)

        assertThrowsSuspend<IllegalArgumentException> {
            useCase(
                amountMinor = 0L,
                type = ExpenseType.NEED,
                categoryId = 1L,
            )
        }

        assertThrowsSuspend<IllegalArgumentException> {
            useCase(
                amountMinor = -500L,
                type = ExpenseType.NEED,
                categoryId = 1L,
            )
        }
    }

    @Test
    fun `AddExpenseUseCase checks that category exists`() = runTest {
        val useCase = AddExpenseUseCase(expenseRepository, categoryRepository)
        coEvery { categoryRepository.getById(999L) } returns null

        assertThrowsSuspend<IllegalArgumentException> {
            useCase(
                amountMinor = 1000L,
                type = ExpenseType.NEED,
                categoryId = 999L,
            )
        }
    }

    @Test
    fun `AddExpenseUseCase derives occurredDate from occurredAt in local time zone`() = runTest {
        val useCase = AddExpenseUseCase(expenseRepository, categoryRepository)
        coEvery { categoryRepository.getById(1L) } returns category

        val expenseSlot = slot<Expense>()
        coEvery { expenseRepository.insertExpense(capture(expenseSlot)) } returns 42L

        val occurredAt = Instant.parse("2026-10-12T18:30:00Z")
        val zoneId = ZoneId.of("Asia/Kolkata") // +05:30 -> 2026-10-13 00:00:00

        val insertedId = useCase(
            amountMinor = 52000L,
            type = ExpenseType.NEED,
            categoryId = 1L,
            note = "  Weekly groceries  ",
            occurredAt = occurredAt,
            paymentMethod = PaymentMethod.UPI,
            zoneId = zoneId,
        )

        assertEquals(42L, insertedId)
        val captured = expenseSlot.captured
        assertEquals(52000L, captured.amountMinor)
        assertEquals(LocalDate.of(2026, 10, 13), captured.occurredDate)
        assertEquals("Weekly groceries", captured.note)
        assertEquals(PaymentMethod.UPI, captured.paymentMethod)
    }

    @Test
    fun `UpdateExpenseUseCase enforces positive amount and updates date and updatedAt`() = runTest {
        val useCase = UpdateExpenseUseCase(expenseRepository)

        val invalidExpense = Expense(
            id = 1L,
            amountMinor = 0L,
            type = ExpenseType.NEED,
            categoryId = 1L,
            occurredAt = Instant.now(),
            occurredDate = LocalDate.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        )

        assertThrowsSuspend<IllegalArgumentException> {
            useCase(invalidExpense)
        }

        val expenseSlot = slot<Expense>()
        coEvery { expenseRepository.updateExpense(capture(expenseSlot)) } returns Unit

        val validExpense = invalidExpense.copy(
            amountMinor = 15000L,
            occurredAt = Instant.parse("2026-10-12T00:00:00Z"),
        )

        useCase(validExpense, ZoneId.of("UTC"))

        val captured = expenseSlot.captured
        assertEquals(15000L, captured.amountMinor)
        assertEquals(LocalDate.of(2026, 10, 12), captured.occurredDate)
        coVerify(exactly = 1) { expenseRepository.updateExpense(any()) }
    }

    @Test
    fun `DeleteExpenseUseCase soft-deletes with deletedAt timestamp`() = runTest {
        val useCase = DeleteExpenseUseCase(expenseRepository)
        val now = Instant.parse("2026-10-12T12:00:00Z")
        coEvery { expenseRepository.softDeleteExpense(1L, now) } returns Unit

        useCase(1L, now)

        coVerify(exactly = 1) { expenseRepository.softDeleteExpense(1L, now) }
    }

    @Test
    fun `RestoreExpenseUseCase clears deletedAt timestamp`() = runTest {
        val useCase = RestoreExpenseUseCase(expenseRepository)
        coEvery { expenseRepository.restoreExpense(1L, any()) } returns Unit

        useCase(1L)

        coVerify(exactly = 1) { expenseRepository.restoreExpense(1L, any()) }
    }

    @Test
    fun `GetExpensesUseCase delegates range and id queries to repository`() = runTest {
        val useCase = GetExpensesUseCase(expenseRepository)
        val startDate = LocalDate.of(2026, 10, 1)
        val endDate = LocalDate.of(2026, 10, 31)

        val dummyExpenseWithCategory = ExpenseWithCategory(
            expense = Expense(
                id = 1L,
                amountMinor = 5000L,
                type = ExpenseType.NEED,
                categoryId = 1L,
                occurredAt = Instant.now(),
                occurredDate = LocalDate.of(2026, 10, 12),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            category = category,
        )

        every {
            expenseRepository.observeBetween(startDate, endDate)
        } returns flowOf(listOf(dummyExpenseWithCategory))

        val result = useCase.getBetween(startDate, endDate).first()
        assertEquals(1, result.size)
        assertEquals(1L, result[0].expense.id)
    }
}
