/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.analytics

import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import kotlin.system.measureTimeMillis

class PerformanceBenchmarkTest {

    private val categories = listOf(
        Category(id = 1, name = "Food", iconKey = "restaurant", colorHex = "#FF5722", defaultType = ExpenseType.NEED),
        Category(id = 2, name = "Groceries", iconKey = "shopping_cart", colorHex = "#4CAF50", defaultType = ExpenseType.NEED),
        Category(id = 3, name = "Transit", iconKey = "directions_bus", colorHex = "#2196F3", defaultType = ExpenseType.NEED),
        Category(id = 4, name = "Entertainment", iconKey = "movie", colorHex = "#9C27B0", defaultType = ExpenseType.WASTE),
        Category(id = 5, name = "Gadgets", iconKey = "smartphone", colorHex = "#FF9800", defaultType = ExpenseType.WASTE),
    )

    private val categoryMap = categories.associateBy { it.id }

    @Test
    fun `analytics engine processes 10,000 seeded expenses within high-performance threshold`() {
        val now = Instant.now()
        val yearMonth = YearMonth.of(2026, 10)
        val seededCount = 10_000

        // Generate 10,000 expenses across October 2026 (31 days)
        val expensesWithCategory = ArrayList<ExpenseWithCategory>(seededCount)
        var expectedTotalNeedMinor = 0L
        var expectedTotalWasteMinor = 0L

        for (i in 0 until seededCount) {
            val dayOfMonth = (i % 31) + 1
            val date = LocalDate.of(2026, 10, dayOfMonth)
            val categoryId = ((i % 5) + 1).toLong()
            val category = categoryMap[categoryId]!!
            val isWaste = (i % 3 == 0) // Every 3rd expense is Waste
            val type = if (isWaste) ExpenseType.WASTE else ExpenseType.NEED
            val amountMinor = (100L + (i % 1000) * 10L) // 1.00 to 100.90

            if (type == ExpenseType.NEED) {
                expectedTotalNeedMinor += amountMinor
            } else {
                expectedTotalWasteMinor += amountMinor
            }

            val expense = Expense(
                id = i.toLong() + 1,
                amountMinor = amountMinor,
                type = type,
                categoryId = categoryId,
                note = "Benchmark expense #$i",
                paymentMethod = PaymentMethod.UPI,
                occurredAt = date.atStartOfDay(java.time.ZoneOffset.UTC).toInstant(),
                occurredDate = date,
                createdAt = now,
                updatedAt = now,
            )
            expensesWithCategory.add(ExpenseWithCategory(expense, category))
        }

        val monthPeriod = Period.Month(yearMonth)
        val today = LocalDate.of(2026, 10, 31)

        // Measure summarize time
        lateinit var summary: PeriodSummary
        val elapsedMillis = measureTimeMillis {
            summary = AnalyticsEngine.summarize(
                expenses = expensesWithCategory,
                previousExpenses = emptyList(),
                period = monthPeriod,
                today = today,
            )
        }

        // Performance Assertion: 10,000 expenses analyzed in under 200ms
        assertTrue("Analytics engine took ${elapsedMillis}ms, expected under 200ms", elapsedMillis < 200)

        // Correctness Assertions
        val expectedTotal = expectedTotalNeedMinor + expectedTotalWasteMinor
        assertEquals(expectedTotal, summary.totalMinor)
        assertEquals(expectedTotalNeedMinor, summary.needMinor)
        assertEquals(expectedTotalWasteMinor, summary.wasteMinor)
        assertEquals(31, summary.dailyTotals.size)

        // Money conversion performance: 10,000 iterations in under 100ms
        val moneyTime = measureTimeMillis {
            for (item in expensesWithCategory) {
                Money.format(item.expense.amountMinor, "₹")
            }
        }
        assertTrue("Money formatting took ${moneyTime}ms, expected under 100ms", moneyTime < 100)
    }
}
