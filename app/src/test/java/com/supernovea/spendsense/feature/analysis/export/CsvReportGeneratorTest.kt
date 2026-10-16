/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.export

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.Income
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class CsvReportGeneratorTest {

    private val fixedZone = ZoneId.of("UTC")

    private val sampleCategory = Category(
        id = 1L,
        name = "Food & Dining",
        defaultType = ExpenseType.NEED,
        iconKey = "restaurant",
        colorHex = "#2AC1AE",
        isDefault = true,
        sortOrder = 0,
    )

    private val sampleSource = IncomeSource(
        id = 2L,
        name = "Pocket Money",
        iconKey = "wallet",
        colorHex = "#4E73DF",
        isDefault = true,
        sortOrder = 0,
    )

    @Test
    fun `generate with empty transactions outputs only header`() {
        val result = CsvReportGenerator.generate(
            expenses = emptyList(),
            incomes = emptyList(),
            includeNotes = false,
            zoneId = fixedZone,
        )

        assertEquals("kind,date,time,amount,type,category_or_source,note,payment_method\n", result)
    }

    @Test
    fun `generate with notes disabled excludes note content`() {
        val expense = ExpenseWithCategory(
            expense = Expense(
                id = 10L,
                amountMinor = 15000L, // 150.00
                categoryId = 1L,
                type = ExpenseType.NEED,
                paymentMethod = PaymentMethod.UPI,
                note = "Secret personal purchase",
                occurredAt = Instant.parse("2026-10-10T14:30:00Z"),
                occurredDate = LocalDate.of(2026, 10, 10),
                createdAt = Instant.parse("2026-10-10T14:30:00Z"),
                updatedAt = Instant.parse("2026-10-10T14:30:00Z"),
            ),
            category = sampleCategory,
        )

        val result = CsvReportGenerator.generate(
            expenses = listOf(expense),
            incomes = emptyList(),
            includeNotes = false,
            zoneId = fixedZone,
        )

        assertTrue(result.contains("EXPENSE,2026-10-10,14:30:00,150,NEED,Food & Dining,,UPI"))
        assertTrue(!result.contains("Secret personal purchase"))
    }

    @Test
    fun `generate with notes enabled includes note content`() {
        val expense = ExpenseWithCategory(
            expense = Expense(
                id = 10L,
                amountMinor = 15000L,
                categoryId = 1L,
                type = ExpenseType.WASTE,
                paymentMethod = PaymentMethod.CASH,
                note = "Lunch with friends",
                occurredAt = Instant.parse("2026-10-10T12:00:00Z"),
                occurredDate = LocalDate.of(2026, 10, 10),
                createdAt = Instant.parse("2026-10-10T12:00:00Z"),
                updatedAt = Instant.parse("2026-10-10T12:00:00Z"),
            ),
            category = sampleCategory,
        )

        val result = CsvReportGenerator.generate(
            expenses = listOf(expense),
            incomes = emptyList(),
            includeNotes = true,
            zoneId = fixedZone,
        )

        assertTrue(result.contains("EXPENSE,2026-10-10,12:00:00,150,WASTE,Food & Dining,Lunch with friends,CASH"))
    }

    @Test
    fun `generate escapes special characters correctly`() {
        val trickyCategory = sampleCategory.copy(name = "Snacks, \"Treats\" & More")
        val expense = ExpenseWithCategory(
            expense = Expense(
                id = 11L,
                amountMinor = 5500L,
                categoryId = 1L,
                type = ExpenseType.WASTE,
                paymentMethod = PaymentMethod.CARD,
                note = "Note with\nnewline and, comma",
                occurredAt = Instant.parse("2026-10-11T10:00:00Z"),
                occurredDate = LocalDate.of(2026, 10, 11),
                createdAt = Instant.parse("2026-10-11T10:00:00Z"),
                updatedAt = Instant.parse("2026-10-11T10:00:00Z"),
            ),
            category = trickyCategory,
        )

        val result = CsvReportGenerator.generate(
            expenses = listOf(expense),
            incomes = emptyList(),
            includeNotes = true,
            zoneId = fixedZone,
        )

        assertTrue(result.contains("\"Snacks, \"\"Treats\"\" & More\""))
        assertTrue(result.contains("\"Note with\nnewline and, comma\""))
    }

    @Test
    fun `generate sorts combined expenses and incomes descending by time`() {
        val olderIncome = IncomeWithSource(
            income = Income(
                id = 20L,
                amountMinor = 500000L, // 5000.00
                sourceId = 2L,
                note = "Monthly pocket money",
                receivedAt = Instant.parse("2026-10-01T09:00:00Z"),
                receivedDate = LocalDate.of(2026, 10, 1),
                createdAt = Instant.parse("2026-10-01T09:00:00Z"),
                updatedAt = Instant.parse("2026-10-01T09:00:00Z"),
            ),
            source = sampleSource,
        )

        val newerExpense = ExpenseWithCategory(
            expense = Expense(
                id = 21L,
                amountMinor = 25000L,
                categoryId = 1L,
                type = ExpenseType.NEED,
                paymentMethod = PaymentMethod.UPI,
                note = "Groceries",
                occurredAt = Instant.parse("2026-10-05T15:30:00Z"),
                occurredDate = LocalDate.of(2026, 10, 5),
                createdAt = Instant.parse("2026-10-05T15:30:00Z"),
                updatedAt = Instant.parse("2026-10-05T15:30:00Z"),
            ),
            category = sampleCategory,
        )

        val result = CsvReportGenerator.generate(
            expenses = listOf(newerExpense),
            incomes = listOf(olderIncome),
            includeNotes = true,
            zoneId = fixedZone,
        )

        val lines = result.trim().split("\n")
        assertEquals(3, lines.size) // header + 2 records
        assertEquals(CsvReportGenerator.HEADER, lines[0])
        assertTrue("Newer expense must come first", lines[1].startsWith("EXPENSE,2026-10-05"))
        assertTrue("Older income must come second", lines[2].startsWith("INCOME,2026-10-01"))
    }
}
