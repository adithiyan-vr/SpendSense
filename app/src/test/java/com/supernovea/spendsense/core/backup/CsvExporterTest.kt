/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.backup

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class CsvExporterTest {

    private val now = Instant.parse("2026-10-12T12:00:00Z")

    private val sampleCategory = Category(
        id = 1L,
        name = "Food & Dining",
        iconKey = "fastfood",
        colorHex = "#FF5722",
        defaultType = ExpenseType.NEED,
    )

    @Test
    fun `export with empty list returns only CSV header`() {
        val result = CsvExporter.export(emptyList())
        assertEquals("id,date,time,amount,type,category,note,payment_method\n", result)
    }

    @Test
    fun `export formats single expense correctly`() {
        val expense = Expense(
            id = 42L,
            amountMinor = 2550L,
            type = ExpenseType.NEED,
            categoryId = 1L,
            note = "Lunch at cafe",
            paymentMethod = PaymentMethod.UPI,
            occurredAt = Instant.parse("2026-10-12T07:30:00Z"),
            occurredDate = LocalDate.parse("2026-10-12"),
            createdAt = now,
            updatedAt = now,
        )
        val item = ExpenseWithCategory(expense, sampleCategory)

        val csv = CsvExporter.export(listOf(item))
        val lines = csv.trim().lines()

        assertEquals(2, lines.size)
        assertEquals("id,date,time,amount,type,category,note,payment_method", lines[0])
        assertTrue(lines[1].startsWith("42,2026-10-12,"))
        assertTrue(lines[1].contains(",25.50,NEED,Food & Dining,Lunch at cafe,UPI"))
    }

    @Test
    fun `export escapes commas, quotes, and newlines in notes and categories`() {
        val specialCategory = Category(
            id = 2L,
            name = "Bills, Taxes & \"Fees\"",
            iconKey = "receipt",
            colorHex = "#2196F3",
            defaultType = ExpenseType.NEED,
        )
        val expense = Expense(
            id = 99L,
            amountMinor = 10000L,
            type = ExpenseType.WASTE,
            categoryId = 2L,
            note = "Bought coffee,\ntips and \"snacks\"",
            paymentMethod = PaymentMethod.CARD,
            occurredAt = Instant.parse("2026-10-12T10:00:00Z"),
            occurredDate = LocalDate.parse("2026-10-12"),
            createdAt = now,
            updatedAt = now,
        )
        val item = ExpenseWithCategory(expense, specialCategory)

        val csv = CsvExporter.export(listOf(item))
        // Verify category escaped
        assertTrue(csv.contains("\"Bills, Taxes & \"\"Fees\"\"\""))
        // Verify note escaped
        assertTrue(csv.contains("\"Bought coffee,\ntips and \"\"snacks\"\"\""))
        assertTrue(csv.contains(",CARD"))
    }

    @Test
    fun `export sorts expenses descending by occurredAt`() {
        val exp1 = Expense(
            id = 1L,
            amountMinor = 500L,
            type = ExpenseType.NEED,
            categoryId = 1L,
            occurredAt = Instant.parse("2026-10-10T10:00:00Z"),
            occurredDate = LocalDate.parse("2026-10-10"),
            createdAt = now,
            updatedAt = now,
        )
        val exp2 = Expense(
            id = 2L,
            amountMinor = 700L,
            type = ExpenseType.NEED,
            categoryId = 1L,
            occurredAt = Instant.parse("2026-10-12T12:00:00Z"),
            occurredDate = LocalDate.parse("2026-10-12"),
            createdAt = now,
            updatedAt = now,
        )

        val items = listOf(
            ExpenseWithCategory(exp1, sampleCategory),
            ExpenseWithCategory(exp2, sampleCategory),
        )

        val csv = CsvExporter.export(items)
        val lines = csv.trim().lines()
        assertEquals(3, lines.size)
        // First row after header should be exp2 (more recent)
        assertTrue(lines[1].startsWith("2,"))
        assertTrue(lines[2].startsWith("1,"))
    }
}
