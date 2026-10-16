/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.history

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.usecase.DeleteExpenseUseCase
import com.supernovea.spendsense.domain.usecase.DeleteIncomeUseCase
import com.supernovea.spendsense.domain.usecase.GetExpensesUseCase
import com.supernovea.spendsense.domain.usecase.GetIncomesUseCase
import com.supernovea.spendsense.domain.usecase.ManageCategoriesUseCase
import com.supernovea.spendsense.domain.usecase.RestoreExpenseUseCase
import com.supernovea.spendsense.domain.usecase.RestoreIncomeUseCase
import com.supernovea.spendsense.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getExpensesUseCase = mockk<GetExpensesUseCase>()
    private val getIncomesUseCase = mockk<GetIncomesUseCase>()
    private val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>(relaxed = true)
    private val deleteIncomeUseCase = mockk<DeleteIncomeUseCase>(relaxed = true)
    private val restoreExpenseUseCase = mockk<RestoreExpenseUseCase>(relaxed = true)
    private val restoreIncomeUseCase = mockk<RestoreIncomeUseCase>(relaxed = true)
    private val manageCategoriesUseCase = mockk<ManageCategoriesUseCase>()

    private val foodCategory = Category(
        id = 1L,
        name = "Food",
        iconKey = "restaurant",
        colorHex = "#F59E0B",
        defaultType = ExpenseType.NEED,
        isDefault = true,
    )

    private val gamingCategory = Category(
        id = 2L,
        name = "Gaming",
        iconKey = "sports_esports",
        colorHex = "#14B8A6",
        defaultType = ExpenseType.WASTE,
        isDefault = true,
    )

    private val expense1 = Expense(
        id = 1L,
        amountMinor = 8000L,
        type = ExpenseType.NEED,
        categoryId = 1L,
        note = "Canteen lunch",
        occurredAt = Instant.parse("2026-10-12T12:30:00Z"),
        occurredDate = LocalDate.parse("2026-10-12"),
        paymentMethod = PaymentMethod.UPI,
        createdAt = Instant.parse("2026-10-12T12:30:00Z"),
        updatedAt = Instant.parse("2026-10-12T12:30:00Z"),
    )

    private val expense2 = Expense(
        id = 2L,
        amountMinor = 12000L,
        type = ExpenseType.WASTE,
        categoryId = 2L,
        note = "Game skin",
        occurredAt = Instant.parse("2026-10-12T14:00:00Z"),
        occurredDate = LocalDate.parse("2026-10-12"),
        paymentMethod = PaymentMethod.UPI,
        createdAt = Instant.parse("2026-10-12T14:00:00Z"),
        updatedAt = Instant.parse("2026-10-12T14:00:00Z"),
    )

    private val expense3 = Expense(
        id = 3L,
        amountMinor = 5000L,
        type = ExpenseType.NEED,
        categoryId = 1L,
        note = "Groceries milk",
        occurredAt = Instant.parse("2026-10-11T09:00:00Z"),
        occurredDate = LocalDate.parse("2026-10-11"),
        paymentMethod = PaymentMethod.CASH,
        createdAt = Instant.parse("2026-10-11T09:00:00Z"),
        updatedAt = Instant.parse("2026-10-11T09:00:00Z"),
    )

    private val allItems = listOf(
        ExpenseWithCategory(expense1, foodCategory),
        ExpenseWithCategory(expense2, gamingCategory),
        ExpenseWithCategory(expense3, foodCategory),
    )

    @Before
    fun setUp() {
        every { manageCategoriesUseCase.getCategories(includeArchived = false) } returns flowOf(listOf(foodCategory, gamingCategory))
        every { getExpensesUseCase.getAll() } returns flowOf(allItems)
        every { getIncomesUseCase.observeAll() } returns flowOf(emptyList())
    }

    private fun createViewModel(): HistoryViewModel {
        return HistoryViewModel(
            getExpensesUseCase = getExpensesUseCase,
            getIncomesUseCase = getIncomesUseCase,
            deleteExpenseUseCase = deleteExpenseUseCase,
            deleteIncomeUseCase = deleteIncomeUseCase,
            restoreExpenseUseCase = restoreExpenseUseCase,
            restoreIncomeUseCase = restoreIncomeUseCase,
            manageCategoriesUseCase = manageCategoriesUseCase,
        )
    }

    @Test
    fun `initial state groups expenses by occurredDate descending with correct day totals`() = runTest {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(3, state.totalItemCount)
        assertEquals(2, state.dayGroups.size)

        // First day: 2026-10-12
        val day1 = state.dayGroups[0]
        assertEquals(LocalDate.parse("2026-10-12"), day1.date)
        assertEquals(2, day1.items.size)
        assertEquals(20000L, day1.totalMinor) // 80 + 120
        assertEquals(8000L, day1.needMinor)
        assertEquals(12000L, day1.wasteMinor)

        // Second day: 2026-10-11
        val day2 = state.dayGroups[1]
        assertEquals(LocalDate.parse("2026-10-11"), day2.date)
        assertEquals(1, day2.items.size)
        assertEquals(5000L, day2.totalMinor)
    }

    @Test
    fun `search query filters expenses by note or category name`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(HistoryEvent.OnSearchQueryChanged("skin"))
        val state = viewModel.uiState.value
        assertEquals(1, state.dayGroups.size)
        assertEquals(1, state.dayGroups[0].items.size)
        assertEquals(2L, state.dayGroups[0].items[0].id)

        // Search by category name
        viewModel.onEvent(HistoryEvent.OnSearchQueryChanged("Food"))
        val stateFood = viewModel.uiState.value
        assertEquals(2, stateFood.dayGroups.size)
    }

    @Test
    fun `type filter isolates Need or Waste expenses`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(HistoryEvent.OnTypeFilterSelected(ExpenseType.WASTE))
        val stateWaste = viewModel.uiState.value
        assertEquals(1, stateWaste.dayGroups.size)
        assertEquals(1, stateWaste.dayGroups[0].items.size)
        assertEquals(2L, stateWaste.dayGroups[0].items[0].id)

        viewModel.onEvent(HistoryEvent.OnTypeFilterSelected(ExpenseType.NEED))
        val stateNeed = viewModel.uiState.value
        assertEquals(2, stateNeed.dayGroups.size)
        assertTrue(stateNeed.dayGroups.all { g -> g.items.all { (it as HistoryTransaction.ExpenseItem).expenseWithCategory.expense.type == ExpenseType.NEED } })
    }

    @Test
    fun `delete expense invokes deleteUseCase and undo restores it`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(HistoryEvent.OnDeleteExpense(expense1))
        coVerify(exactly = 1) { deleteExpenseUseCase.invoke(1L, any()) }
        assertTrue(viewModel.uiState.value.canUndoDelete)
        assertEquals("Expense deleted", viewModel.uiState.value.snackbarMessage)

        viewModel.onEvent(HistoryEvent.OnUndoDelete)
        coVerify(exactly = 1) { restoreExpenseUseCase.invoke(1L, any()) }
        assertFalse(viewModel.uiState.value.canUndoDelete)
    }
}
