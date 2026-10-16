/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.settings

import com.supernovea.spendsense.core.backup.ImportMode
import com.supernovea.spendsense.core.backup.ImportResult
import com.supernovea.spendsense.core.backup.JsonBackupManager
import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.DeleteAllDataUseCase
import com.supernovea.spendsense.util.MainDispatcherRule
import com.supernovea.spendsense.work.WorkScheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val deleteAllDataUseCase = mockk<DeleteAllDataUseCase>(relaxed = true)
    private val jsonBackupManager = mockk<JsonBackupManager>(relaxed = true)
    private val expenseRepository = mockk<ExpenseRepository>(relaxed = true)
    private val incomeRepository = mockk<com.supernovea.spendsense.domain.repository.IncomeRepository>(relaxed = true)
    private val workScheduler = mockk<WorkScheduler>(relaxed = true)

    private val defaultSettings = UserSettings(
        currencySymbol = "₹",
        weekStartDay = WeekStartDay.MONDAY,
        themeMode = ThemeMode.SYSTEM,
        reminderEnabled = false,
        weeklySummaryEnabled = true,
        monthlySummaryEnabled = true,
    )

    private fun createViewModel() = SettingsViewModel(
        settingsRepository = settingsRepository,
        deleteAllDataUseCase = deleteAllDataUseCase,
        jsonBackupManager = jsonBackupManager,
        expenseRepository = expenseRepository,
        incomeRepository = incomeRepository,
        workScheduler = workScheduler,
    )

    @Before
    fun setup() {
        every { settingsRepository.observeSettings() } returns flowOf(defaultSettings)
        coEvery { settingsRepository.getSettings() } returns defaultSettings
        every { expenseRepository.observeAll() } returns flowOf(emptyList())
        every { incomeRepository.observeAll() } returns flowOf(emptyList())
    }

    @Test
    fun `initial state loads settings from repository`() = runTest {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertEquals("₹", state.currencySymbol)
        assertEquals(WeekStartDay.MONDAY, state.weekStartDay)
        assertEquals(ThemeMode.SYSTEM, state.themeMode)
        assertFalse(state.dailyReminderEnabled)
        assertTrue(state.weeklySummaryEnabled)
        assertTrue(state.monthlySummaryEnabled)
    }

    @Test
    fun `change currency symbol updates settings repository`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.ChangeCurrencySymbol("$"))

        coVerify { settingsRepository.setCurrencySymbol("$") }
    }

    @Test
    fun `change week start day updates settings repository`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.ChangeWeekStartDay(WeekStartDay.SUNDAY))

        coVerify { settingsRepository.setWeekStartDay(WeekStartDay.SUNDAY) }
    }

    @Test
    fun `change theme mode updates settings repository`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.ChangeThemeMode(ThemeMode.DARK))

        coVerify { settingsRepository.setThemeMode(ThemeMode.DARK) }
    }

    @Test
    fun `toggling reminder options updates settings repository and schedules workers`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.ToggleDailyReminder(true))
        coVerify { settingsRepository.setReminderEnabled(true) }
        coVerify { workScheduler.scheduleAll(match { it.reminderEnabled }) }

        viewModel.onEvent(SettingsEvent.ToggleWeeklySummary(false))
        coVerify { settingsRepository.setWeeklySummaryEnabled(false) }
        coVerify { workScheduler.scheduleAll(match { !it.weeklySummaryEnabled }) }

        viewModel.onEvent(SettingsEvent.ToggleMonthlySummary(false))
        coVerify { settingsRepository.setMonthlySummaryEnabled(false) }
        coVerify { workScheduler.scheduleAll(match { !it.monthlySummaryEnabled }) }
    }

    @Test
    fun `request, cancel, and confirm delete all data flow`() = runTest {
        val viewModel = createViewModel()

        // Request dialog
        viewModel.onEvent(SettingsEvent.RequestDeleteAllData)
        assertTrue(viewModel.uiState.value.showDeleteAllDialog)

        // Cancel dialog
        viewModel.onEvent(SettingsEvent.CancelDeleteAllData)
        assertFalse(viewModel.uiState.value.showDeleteAllDialog)

        // Request and Confirm
        viewModel.onEvent(SettingsEvent.RequestDeleteAllData)
        viewModel.onEvent(SettingsEvent.ConfirmDeleteAllData)

        coVerify { deleteAllDataUseCase() }
        assertFalse(viewModel.uiState.value.showDeleteAllDialog)
        assertFalse(viewModel.uiState.value.isDeletingData)
        assertEquals("All data has been reset", viewModel.uiState.value.userMessage)
    }

    @Test
    fun `getCsvContent calls repository and returns CSV string`() = runTest {
        val viewModel = createViewModel()
        val csv = viewModel.getCsvContent()
        assertTrue(csv.startsWith("kind,date,time,amount,type,category_or_source,note,payment_method"))
    }

    @Test
    fun `getJsonBackupContent calls jsonBackupManager`() = runTest {
        coEvery { jsonBackupManager.exportJson() } returns "{\"schemaVersion\": 1}"
        val viewModel = createViewModel()
        val json = viewModel.getJsonBackupContent()
        assertEquals("{\"schemaVersion\": 1}", json)
    }

    @Test
    fun `import file selection and confirmation flow`() = runTest {
        val jsonContent = "{\"schemaVersion\": 1}"
        coEvery { jsonBackupManager.importJson(jsonContent, ImportMode.MERGE) } returns ImportResult(
            expensesRestoredCount = 5,
            categoriesRestoredCount = 2,
            budgetsRestoredCount = 1,
        )

        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.ImportFileSelected(jsonContent))
        assertTrue(viewModel.uiState.value.showImportDialog)
        assertEquals(jsonContent, viewModel.uiState.value.pendingImportJson)

        viewModel.onEvent(SettingsEvent.ConfirmImport(ImportMode.MERGE))
        assertFalse(viewModel.uiState.value.showImportDialog)
        assertNull(viewModel.uiState.value.pendingImportJson)
        assertTrue(viewModel.uiState.value.userMessage?.contains("5 expenses restored") == true)
    }

    @Test
    fun `import cancel flow clears pending JSON and closes dialog`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.ImportFileSelected("{}"))
        assertTrue(viewModel.uiState.value.showImportDialog)

        viewModel.onEvent(SettingsEvent.CancelImport)
        assertFalse(viewModel.uiState.value.showImportDialog)
        assertNull(viewModel.uiState.value.pendingImportJson)
    }

    @Test
    fun `export success and failed events update messages`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.ExportCsvSuccess)
        assertEquals("Expenses exported to CSV successfully", viewModel.uiState.value.userMessage)

        viewModel.onEvent(SettingsEvent.DismissMessage)
        assertNull(viewModel.uiState.value.userMessage)

        viewModel.onEvent(SettingsEvent.ExportFailed("Disk write error"))
        assertEquals("Disk write error", viewModel.uiState.value.errorMessage)
    }
}
