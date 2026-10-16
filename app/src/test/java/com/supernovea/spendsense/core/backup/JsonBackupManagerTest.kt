/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.backup

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.Income
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.CategoryRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class JsonBackupManagerTest {

    private val expenseRepository = mockk<ExpenseRepository>(relaxed = true)
    private val categoryRepository = mockk<CategoryRepository>(relaxed = true)
    private val budgetRepository = mockk<BudgetRepository>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true)
    private val incomeRepository = mockk<IncomeRepository>(relaxed = true)
    private val incomeSourceRepository = mockk<IncomeSourceRepository>(relaxed = true)

    private lateinit var manager: JsonBackupManager

    private val sampleCategory = Category(
        id = 10L,
        name = "Groceries",
        iconKey = "local_grocery_store",
        colorHex = "#4CAF50",
        defaultType = ExpenseType.NEED,
        sortOrder = 1,
        defaultKey = "groceries",
        groupKey = "food_drink",
    )

    private val sampleIncomeSource = IncomeSource(
        id = 20L,
        name = "Salary",
        iconKey = "payments",
        colorHex = "#10B981",
        defaultKey = "salary",
        sortOrder = 1,
    )

    private val sampleExpense = Expense(
        id = 100L,
        amountMinor = 5000L,
        type = ExpenseType.NEED,
        categoryId = 10L,
        note = "Weekly groceries",
        paymentMethod = PaymentMethod.UPI,
        occurredAt = Instant.parse("2026-10-12T12:00:00Z"),
        occurredDate = LocalDate.parse("2026-10-12"),
        createdAt = Instant.parse("2026-10-12T12:00:00Z"),
        updatedAt = Instant.parse("2026-10-12T12:00:00Z"),
    )

    private val sampleIncome = Income(
        id = 200L,
        amountMinor = 5000000L,
        sourceId = 20L,
        note = "October Salary",
        receivedAt = Instant.parse("2026-10-01T09:00:00Z"),
        receivedDate = LocalDate.parse("2026-10-01"),
        createdAt = Instant.parse("2026-10-01T09:00:00Z"),
        updatedAt = Instant.parse("2026-10-01T09:00:00Z"),
    )

    private val sampleBudget = Budget(
        monthKey = "2026-10",
        totalLimitMinor = 3000000L,
        wasteLimitMinor = 500000L,
    )

    private val sampleSettings = UserSettings(
        currencySymbol = "$",
        weekStartDay = WeekStartDay.SUNDAY,
        themeMode = ThemeMode.DARK,
        reminderEnabled = true,
        reminderTimeMinutes = 1260,
        weeklySummaryEnabled = true,
        monthlySummaryEnabled = false,
        lastBackupEpochMillis = null,
    )

    @Before
    fun setup() {
        manager = JsonBackupManager(
            expenseRepository = expenseRepository,
            categoryRepository = categoryRepository,
            budgetRepository = budgetRepository,
            settingsRepository = settingsRepository,
            incomeRepository = incomeRepository,
            incomeSourceRepository = incomeSourceRepository,
        )

        coEvery { expenseRepository.observeAll() } returns flowOf(listOf(ExpenseWithCategory(sampleExpense, sampleCategory)))
        coEvery { categoryRepository.observeAll() } returns flowOf(listOf(sampleCategory))
        coEvery { budgetRepository.observeAll() } returns flowOf(listOf(sampleBudget))
        coEvery { settingsRepository.getSettings() } returns sampleSettings
        coEvery { incomeRepository.observeAll() } returns flowOf(listOf(IncomeWithSource(sampleIncome, sampleIncomeSource)))
        coEvery { incomeSourceRepository.observeAll() } returns flowOf(listOf(sampleIncomeSource))
    }

    @Test
    fun `exportJson generates valid JSON payload and records backup timestamp`() = runTest {
        val json = manager.exportJson()

        assertTrue(json.contains("\"schemaVersion\": 2"))
        assertTrue(json.contains("\"Groceries\""))
        assertTrue(json.contains("\"Weekly groceries\""))
        assertTrue(json.contains("\"2026-10\""))
        assertTrue(json.contains("\"currencySymbol\": \"$\""))
        assertTrue(json.contains("\"Salary\""))
        assertTrue(json.contains("\"October Salary\""))

        coVerify { settingsRepository.setLastBackupEpochMillis(any()) }
    }

    @Test
    fun `importJson with REPLACE mode wipes current data and restores all entries and settings`() = runTest {
        val json = manager.exportJson()

        val result = manager.importJson(json, ImportMode.REPLACE)

        assertEquals(1, result.expensesRestoredCount)
        assertEquals(1, result.categoriesRestoredCount)
        assertEquals(1, result.budgetsRestoredCount)
        assertEquals(1, result.incomesRestoredCount)
        assertEquals(1, result.incomeSourcesRestoredCount)

        coVerify { expenseRepository.deleteAllExpenses() }
        coVerify { incomeRepository.deleteAllIncomes() }
        coVerify { budgetRepository.deleteBudget("2026-10") }
        coVerify { categoryRepository.upsertCategory(match { it.name == "Groceries" && it.groupKey == "food_drink" }) }
        coVerify { incomeSourceRepository.upsertIncomeSource(match { it.name == "Salary" }) }
        coVerify { budgetRepository.setBudget(match { it.monthKey == "2026-10" }) }
        coVerify { expenseRepository.insertExpense(match { it.note == "Weekly groceries" }) }
        coVerify { incomeRepository.insertIncome(match { it.note == "October Salary" }) }
        coVerify { settingsRepository.setCurrencySymbol("$") }
        coVerify { settingsRepository.setWeekStartDay(WeekStartDay.SUNDAY) }
        coVerify { settingsRepository.setThemeMode(ThemeMode.DARK) }
    }

    @Test
    fun `importJson with MERGE mode skips exact duplicates and preserves existing data`() = runTest {
        val json = manager.exportJson()

        // Existing data contains the same expense and income
        coEvery { expenseRepository.observeAll() } returns flowOf(listOf(ExpenseWithCategory(sampleExpense, sampleCategory)))
        coEvery { incomeRepository.observeAll() } returns flowOf(listOf(IncomeWithSource(sampleIncome, sampleIncomeSource)))

        val result = manager.importJson(json, ImportMode.MERGE)

        // Expense and income were duplicates, so 0 should be inserted
        assertEquals(0, result.expensesRestoredCount)
        assertEquals(0, result.incomesRestoredCount)
        assertEquals(1, result.categoriesRestoredCount)
        assertEquals(1, result.budgetsRestoredCount)
        assertEquals(1, result.incomeSourcesRestoredCount)

        coVerify(exactly = 0) { expenseRepository.deleteAllExpenses() }
        coVerify(exactly = 0) { incomeRepository.deleteAllIncomes() }
        coVerify(exactly = 0) { expenseRepository.insertExpense(any()) }
        coVerify(exactly = 0) { incomeRepository.insertIncome(any()) }
    }

    @Test
    fun `importJson rejects future unsupported schema versions`() = runTest {
        val futureJson = """
            {
                "schemaVersion": 999,
                "exportedAtEpochMillis": 1776000000000,
                "expenses": [],
                "categories": [],
                "budgets": []
            }
        """.trimIndent()

        try {
            manager.importJson(futureJson, ImportMode.MERGE)
            fail("Expected IllegalArgumentException for unsupported version")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Unsupported backup schema version: 999") == true)
        }
    }
}
