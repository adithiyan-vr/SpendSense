/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.budget

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.BudgetStatus
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.GetBudgetStatusUseCase
import com.supernovea.spendsense.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.YearMonth

class BudgetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getBudgetStatusUseCase = mockk<GetBudgetStatusUseCase>(relaxed = true)
    private val budgetRepository = mockk<BudgetRepository>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)

    private val currentMonth = YearMonth.of(2026, 10)
    private val existingBudget = Budget(
        monthKey = "2026-10",
        totalLimitMinor = 5000000L, // 50,000
        wasteLimitMinor = 1500000L, // 15,000
    )

    private val budgetStatus = BudgetStatus(
        budget = existingBudget,
        totalSpentMinor = 3000000L,
        wasteSpentMinor = 500000L,
        totalProgressPct = 60.0,
        wasteProgressPct = 33.33,
        isOverTotal = false,
        isOverWaste = false,
        daysRemainingInMonth = 19,
    )

    @Before
    fun setup() {
        every { settingsRepository.observeSettings() } returns flowOf(UserSettings(currencySymbol = "₹"))
        coEvery { budgetRepository.getByMonth("2026-10") } returns existingBudget
        every { getBudgetStatusUseCase(YearMonth.of(2026, 10)) } returns flowOf(budgetStatus)
    }

    @Test
    fun `initial state loads existing budget and populates inputs`() = runTest {
        val viewModel = BudgetViewModel(getBudgetStatusUseCase, budgetRepository, settingsRepository)
        viewModel.onEvent(BudgetEvent.SelectMonth(currentMonth))

        val state = viewModel.uiState.value
        assertEquals(currentMonth, state.selectedMonth)
        assertEquals("50000", state.totalLimitInput)
        assertEquals("15000", state.wasteLimitInput)
        assertEquals("₹", state.currencySymbol)
        assertEquals(budgetStatus, state.status)
    }

    @Test
    fun `navigation to previous and next month updates selectedMonth`() = runTest {
        val viewModel = BudgetViewModel(getBudgetStatusUseCase, budgetRepository, settingsRepository)
        viewModel.onEvent(BudgetEvent.SelectMonth(currentMonth))

        viewModel.onEvent(BudgetEvent.PreviousMonth)
        assertEquals(YearMonth.of(2026, 9), viewModel.uiState.value.selectedMonth)

        viewModel.onEvent(BudgetEvent.NextMonth)
        assertEquals(YearMonth.of(2026, 10), viewModel.uiState.value.selectedMonth)
    }

    @Test
    fun `saving valid budget invokes useCase setBudget and sets userMessage`() = runTest {
        val viewModel = BudgetViewModel(getBudgetStatusUseCase, budgetRepository, settingsRepository)
        viewModel.onEvent(BudgetEvent.SelectMonth(currentMonth))
        viewModel.onEvent(BudgetEvent.TotalLimitChanged("60000"))
        viewModel.onEvent(BudgetEvent.WasteLimitChanged("10000"))
        viewModel.onEvent(BudgetEvent.SaveBudget)

        coVerify {
            getBudgetStatusUseCase.setBudget(
                Budget(
                    monthKey = "2026-10",
                    totalLimitMinor = 6000000L,
                    wasteLimitMinor = 1000000L,
                )
            )
        }
        assertEquals("Budget saved", viewModel.uiState.value.userMessage)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `saving invalid total limit sets errorMessage`() = runTest {
        val viewModel = BudgetViewModel(getBudgetStatusUseCase, budgetRepository, settingsRepository)
        viewModel.onEvent(BudgetEvent.SelectMonth(currentMonth))
        viewModel.onEvent(BudgetEvent.TotalLimitChanged("0"))
        viewModel.onEvent(BudgetEvent.SaveBudget)

        assertNotNull(viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { getBudgetStatusUseCase.setBudget(any()) }
    }

    @Test
    fun `saving waste limit exceeding total limit sets errorMessage`() = runTest {
        val viewModel = BudgetViewModel(getBudgetStatusUseCase, budgetRepository, settingsRepository)
        viewModel.onEvent(BudgetEvent.SelectMonth(currentMonth))
        viewModel.onEvent(BudgetEvent.TotalLimitChanged("30000"))
        viewModel.onEvent(BudgetEvent.WasteLimitChanged("40000"))
        viewModel.onEvent(BudgetEvent.SaveBudget)

        assertEquals("Waste limit cannot exceed total budget", viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { getBudgetStatusUseCase.setBudget(any()) }
    }

    @Test
    fun `deleteBudget removes budget and clears inputs`() = runTest {
        val viewModel = BudgetViewModel(getBudgetStatusUseCase, budgetRepository, settingsRepository)
        viewModel.onEvent(BudgetEvent.SelectMonth(currentMonth))
        viewModel.onEvent(BudgetEvent.DeleteBudget)

        coVerify { budgetRepository.deleteBudget("2026-10") }
        assertEquals("", viewModel.uiState.value.totalLimitInput)
        assertEquals("", viewModel.uiState.value.wasteLimitInput)
        assertEquals("Budget removed", viewModel.uiState.value.userMessage)
    }
}
