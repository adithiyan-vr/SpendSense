/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.addexpense

import androidx.lifecycle.SavedStateHandle
import com.supernovea.spendsense.core.navigation.Screen
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.AddExpenseUseCase
import com.supernovea.spendsense.domain.usecase.AddIncomeUseCase
import com.supernovea.spendsense.domain.usecase.GetExpensesUseCase
import com.supernovea.spendsense.domain.usecase.GetIncomesUseCase
import com.supernovea.spendsense.domain.usecase.ManageCategoriesUseCase
import com.supernovea.spendsense.domain.usecase.ManageIncomeSourcesUseCase
import com.supernovea.spendsense.domain.usecase.UpdateExpenseUseCase
import com.supernovea.spendsense.domain.usecase.UpdateIncomeUseCase
import com.supernovea.spendsense.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class AddExpenseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val addExpenseUseCase = mockk<AddExpenseUseCase>(relaxed = true)
    private val updateExpenseUseCase = mockk<UpdateExpenseUseCase>(relaxed = true)
    private val getExpensesUseCase = mockk<GetExpensesUseCase>()
    private val addIncomeUseCase = mockk<AddIncomeUseCase>(relaxed = true)
    private val updateIncomeUseCase = mockk<UpdateIncomeUseCase>(relaxed = true)
    private val getIncomesUseCase = mockk<GetIncomesUseCase>()
    private val manageIncomeSourcesUseCase = mockk<ManageIncomeSourcesUseCase>()
    private val manageCategoriesUseCase = mockk<ManageCategoriesUseCase>()
    private val settingsRepository = mockk<SettingsRepository>()

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

    private val categories = listOf(foodCategory, gamingCategory)

    @Before
    fun setUp() {
        every { manageCategoriesUseCase.getCategories(includeArchived = false) } returns flowOf(categories)
        every { settingsRepository.observeSettings() } returns flowOf(UserSettings())
        every { manageIncomeSourcesUseCase.observeActive() } returns flowOf(emptyList())
    }

    private fun createViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()): AddExpenseViewModel {
        return AddExpenseViewModel(
            savedStateHandle = savedStateHandle,
            addExpenseUseCase = addExpenseUseCase,
            updateExpenseUseCase = updateExpenseUseCase,
            getExpensesUseCase = getExpensesUseCase,
            manageCategoriesUseCase = manageCategoriesUseCase,
            addIncomeUseCase = addIncomeUseCase,
            updateIncomeUseCase = updateIncomeUseCase,
            getIncomesUseCase = getIncomesUseCase,
            manageIncomeSourcesUseCase = manageIncomeSourcesUseCase,
            settingsRepository = settingsRepository,
        )
    }

    @Test
    fun `initial state has zero amount, loaded categories, and first category auto-selected`() = runTest {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertEquals("0", state.amountInput)
        assertEquals(0L, state.amountMinor)
        assertEquals(ExpenseType.NEED, state.selectedType)
        assertEquals(1L, state.selectedCategoryId)
        assertEquals(2, state.categories.size)
        assertFalse(state.isValid)
    }

    @Test
    fun `digit clicks update amount input and parsed minor amount`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(AddExpenseEvent.OnDigitClick('1'))
        assertEquals("1", viewModel.uiState.value.amountInput)
        assertEquals(100L, viewModel.uiState.value.amountMinor)

        viewModel.onEvent(AddExpenseEvent.OnDigitClick('2'))
        viewModel.onEvent(AddExpenseEvent.OnDigitClick('0'))
        assertEquals("120", viewModel.uiState.value.amountInput)
        assertEquals(12000L, viewModel.uiState.value.amountMinor)
        assertTrue(viewModel.uiState.value.isValid)
    }

    @Test
    fun `decimal clicks allow at most two decimal places and prevent duplicate decimals`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(AddExpenseEvent.OnDigitClick('5'))
        viewModel.onEvent(AddExpenseEvent.OnDecimalClick)
        viewModel.onEvent(AddExpenseEvent.OnDecimalClick) // duplicate ignored
        assertEquals("5.", viewModel.uiState.value.amountInput)

        viewModel.onEvent(AddExpenseEvent.OnDigitClick('7'))
        viewModel.onEvent(AddExpenseEvent.OnDigitClick('5'))
        assertEquals("5.75", viewModel.uiState.value.amountInput)
        assertEquals(575L, viewModel.uiState.value.amountMinor)

        // Third decimal digit ignored
        viewModel.onEvent(AddExpenseEvent.OnDigitClick('9'))
        assertEquals("5.75", viewModel.uiState.value.amountInput)
        assertEquals(575L, viewModel.uiState.value.amountMinor)
    }

    @Test
    fun `backspace removes last char and resets to zero when empty`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(AddExpenseEvent.OnDigitClick('9'))
        viewModel.onEvent(AddExpenseEvent.OnDigitClick('5'))
        assertEquals("95", viewModel.uiState.value.amountInput)

        viewModel.onEvent(AddExpenseEvent.OnBackspaceClick)
        assertEquals("9", viewModel.uiState.value.amountInput)
        assertEquals(900L, viewModel.uiState.value.amountMinor)

        viewModel.onEvent(AddExpenseEvent.OnBackspaceClick)
        assertEquals("0", viewModel.uiState.value.amountInput)
        assertEquals(0L, viewModel.uiState.value.amountMinor)
    }

    @Test
    fun `clear resets amount to zero`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(AddExpenseEvent.OnDigitClick('5'))
        viewModel.onEvent(AddExpenseEvent.OnDigitClick('0'))
        viewModel.onEvent(AddExpenseEvent.OnClearClick)

        assertEquals("0", viewModel.uiState.value.amountInput)
        assertEquals(0L, viewModel.uiState.value.amountMinor)
    }

    @Test
    fun `selecting category auto-updates default type when user has not manually set type`() = runTest {
        val viewModel = createViewModel()

        // Initially Food (NEED)
        assertEquals(ExpenseType.NEED, viewModel.uiState.value.selectedType)

        // Select Gaming (defaultType = WASTE)
        viewModel.onEvent(AddExpenseEvent.OnCategorySelected(gamingCategory))
        assertEquals(ExpenseType.WASTE, viewModel.uiState.value.selectedType)
        assertEquals(gamingCategory.id, viewModel.uiState.value.selectedCategoryId)
    }

    @Test
    fun `user manually selecting type is preserved even if category changes`() = runTest {
        val viewModel = createViewModel()

        // User explicitly marks Need
        viewModel.onEvent(AddExpenseEvent.OnTypeSelected(ExpenseType.NEED))

        // Select Gaming (which defaults to WASTE)
        viewModel.onEvent(AddExpenseEvent.OnCategorySelected(gamingCategory))

        // Should stay NEED because user explicitly chose it
        assertEquals(ExpenseType.NEED, viewModel.uiState.value.selectedType)
    }

    @Test
    fun `save with zero amount sets error message and does not call use case`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(AddExpenseEvent.OnSaveClick)

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSaved)
        coVerify(exactly = 0) { addExpenseUseCase.invoke(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `save with valid amount and category invokes addExpenseUseCase and flags isSaved`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(AddExpenseEvent.OnDigitClick('1'))
        viewModel.onEvent(AddExpenseEvent.OnDigitClick('5'))
        viewModel.onEvent(AddExpenseEvent.OnDigitClick('0'))
        viewModel.onEvent(AddExpenseEvent.OnNoteChanged("Canteen lunch"))
        viewModel.onEvent(AddExpenseEvent.OnPaymentMethodSelected(PaymentMethod.UPI))

        viewModel.onEvent(AddExpenseEvent.OnSaveClick)

        assertTrue(viewModel.uiState.value.isSaved)
        coVerify(exactly = 1) {
            addExpenseUseCase.invoke(
                amountMinor = 15000L,
                type = ExpenseType.NEED,
                categoryId = 1L,
                note = "Canteen lunch",
                occurredAt = any(),
                paymentMethod = PaymentMethod.UPI,
            )
        }
    }

    @Test
    fun `edit mode loads existing expense and populates state`() = runTest {
        val existingExpense = Expense(
            id = 42L,
            amountMinor = 25000L,
            type = ExpenseType.WASTE,
            categoryId = 2L,
            note = "Game battle pass",
            occurredAt = Instant.parse("2026-10-12T10:00:00Z"),
            occurredDate = LocalDate.parse("2026-10-12"),
            paymentMethod = PaymentMethod.CARD,
            createdAt = Instant.parse("2026-10-12T10:00:00Z"),
            updatedAt = Instant.parse("2026-10-12T10:00:00Z"),
        )
        val expenseWithCategory = ExpenseWithCategory(existingExpense, gamingCategory)

        every { getExpensesUseCase.getById(42L) } returns flowOf(expenseWithCategory)

        val handle = SavedStateHandle(mapOf(Screen.AddExpense.ARG_EXPENSE_ID to "42"))
        val viewModel = createViewModel(handle)

        val state = viewModel.uiState.value
        assertTrue(state.isEditing)
        assertEquals(42L, state.editingExpenseId)
        assertEquals("250", state.amountInput)
        assertEquals(25000L, state.amountMinor)
        assertEquals(ExpenseType.WASTE, state.selectedType)
        assertEquals(2L, state.selectedCategoryId)
        assertEquals("Game battle pass", state.note)
        assertEquals(PaymentMethod.CARD, state.paymentMethod)

        // Save in edit mode invokes updateExpenseUseCase
        viewModel.onEvent(AddExpenseEvent.OnSaveClick)
        assertTrue(viewModel.uiState.value.isSaved)
        coVerify(exactly = 1) { updateExpenseUseCase.invoke(any()) }
    }
}
