/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.analytics

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.DayTotal
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.WeekStartDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class AnalyticsEngineTest {

    private val foodCategory = Category(
        id = 1L,
        name = "Food",
        iconKey = "ic_food",
        colorHex = "#FF5722",
        defaultType = ExpenseType.NEED,
        isArchived = false,
        isDefault = true,
    )

    private val gamingCategory = Category(
        id = 2L,
        name = "Gaming",
        iconKey = "ic_gamepad",
        colorHex = "#9C27B0",
        defaultType = ExpenseType.WASTE,
        isArchived = false,
        isDefault = true,
    )

    private val transportCategory = Category(
        id = 3L,
        name = "Transport",
        iconKey = "ic_bus",
        colorHex = "#2196F3",
        defaultType = ExpenseType.NEED,
        isArchived = false,
        isDefault = true,
    )

    private fun createExpense(
        id: Long,
        amountMinor: Long,
        type: ExpenseType,
        category: Category,
        date: LocalDate,
    ): ExpenseWithCategory {
        return ExpenseWithCategory(
            expense = Expense(
                id = id,
                amountMinor = amountMinor,
                type = type,
                categoryId = category.id,
                occurredAt = Instant.parse("${date}T12:00:00Z"),
                occurredDate = date,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            ),
            category = category,
        )
    }

    @Test
    fun `summarize handles completely empty period`() {
        val week = Period.Week(LocalDate.of(2026, 10, 12))
        val summary = AnalyticsEngine.summarize(
            expenses = emptyList(),
            previousExpenses = emptyList(),
            period = week,
            today = LocalDate.of(2026, 10, 15),
        )

        assertEquals(0L, summary.totalMinor)
        assertEquals(0L, summary.needMinor)
        assertEquals(0L, summary.wasteMinor)
        assertEquals(0.0, summary.wasteRatioPct, 0.001)
        assertEquals(0.0, summary.needRatioPct, 0.001)
        assertEquals(0L, summary.avgDailyMinor)
        assertNull(summary.peakDay)
        assertEquals(7, summary.dailyTotals.size)
        assertTrue(summary.topCategories.isEmpty())
        // Mon, Tue, Wed, Thu = 4 elapsed days, all zero waste -> 4 clean days
        assertEquals(4, summary.cleanDays)
    }

    @Test
    fun `summarize calculates Need and Waste totals and ratios correctly`() {
        // Week: Mon 2026-10-12 to Sun 2026-10-18
        // Need: ₹980 (98000L), Waste: ₹440 (44000L), Total: ₹1420 (142000L)
        val expenses = listOf(
            createExpense(1, 52000L, ExpenseType.NEED, foodCategory, LocalDate.of(2026, 10, 12)),
            createExpense(2, 46000L, ExpenseType.NEED, transportCategory, LocalDate.of(2026, 10, 14)),
            createExpense(3, 44000L, ExpenseType.WASTE, gamingCategory, LocalDate.of(2026, 10, 15)),
        )

        val week = Period.Week(LocalDate.of(2026, 10, 12))
        // Evaluated at end of week (Sunday 2026-10-18)
        val summary = AnalyticsEngine.summarize(
            expenses = expenses,
            period = week,
            today = LocalDate.of(2026, 10, 18),
        )

        assertEquals(142000L, summary.totalMinor)
        assertEquals(98000L, summary.needMinor)
        assertEquals(44000L, summary.wasteMinor)

        // Waste Ratio = (44000 / 142000) * 100 = 30.9859%
        val expectedWasteRatio = (44000.0 / 142000.0) * 100.0
        assertEquals(expectedWasteRatio, summary.wasteRatioPct, 0.001)
        assertEquals(100.0 - expectedWasteRatio, summary.needRatioPct, 0.001)

        // Avg daily spend: 142000 / 7 = 20285 paise (~₹202.85)
        assertEquals(142000L / 7, summary.avgDailyMinor)

        // Peak day: Thu Oct 15 had ₹440, Wed Oct 14 had ₹460, Mon Oct 12 had ₹520 -> Mon Oct 12 is peak
        assertNotNull(summary.peakDay)
        assertEquals(LocalDate.of(2026, 10, 12), summary.peakDay?.date)
        assertEquals(52000L, summary.peakDay?.totalMinor)

        // Clean days: Mon (0 waste), Tue (0 spend), Wed (0 waste), Fri (0 spend), Sat (0 spend), Sun (0 spend) = 6 clean days
        assertEquals(6, summary.cleanDays)
    }

    @Test
    fun `summarize builds ordered daily totals representing all 7 days of the week`() {
        val week = Period.Week(LocalDate.of(2026, 10, 12))
        val expenses = listOf(
            createExpense(1, 50000L, ExpenseType.NEED, foodCategory, LocalDate.of(2026, 10, 12)),
        )

        val summary = AnalyticsEngine.summarize(
            expenses = expenses,
            previousExpenses = emptyList(),
            period = week,
        )
        assertEquals(7, summary.dailyTotals.size)
        assertEquals(LocalDate.of(2026, 10, 12), summary.dailyTotals[0].date)
        assertEquals(50000L, summary.dailyTotals[0].totalMinor)
        assertEquals(LocalDate.of(2026, 10, 18), summary.dailyTotals[6].date)
        assertEquals(0L, summary.dailyTotals[6].totalMinor)
    }

    @Test
    fun `summarize ranks top categories by total spend descending`() {
        val expenses = listOf(
            createExpense(1, 20000L, ExpenseType.NEED, transportCategory, LocalDate.of(2026, 10, 12)),
            createExpense(2, 50000L, ExpenseType.NEED, foodCategory, LocalDate.of(2026, 10, 13)),
            createExpense(3, 30000L, ExpenseType.WASTE, gamingCategory, LocalDate.of(2026, 10, 14)),
        )

        val week = Period.Week(LocalDate.of(2026, 10, 12))
        val summary = AnalyticsEngine.summarize(
            expenses = expenses,
            previousExpenses = emptyList(),
            period = week,
        )

        assertEquals(3, summary.topCategories.size)
        // 1st: Food (₹500, 50%)
        assertEquals(foodCategory.id, summary.topCategories[0].category.id)
        assertEquals(50000L, summary.topCategories[0].totalMinor)
        assertEquals(50.0, summary.topCategories[0].sharePct, 0.001)

        // 2nd: Gaming (₹300, 30%)
        assertEquals(gamingCategory.id, summary.topCategories[1].category.id)
        assertEquals(30000L, summary.topCategories[1].totalMinor)
        assertEquals(30.0, summary.topCategories[1].sharePct, 0.001)

        // 3rd: Transport (₹200, 20%)
        assertEquals(transportCategory.id, summary.topCategories[2].category.id)
        assertEquals(20000L, summary.topCategories[2].totalMinor)
        assertEquals(20.0, summary.topCategories[2].sharePct, 0.001)
    }

    @Test
    fun `summarize calculates period-over-period comparison metrics`() {
        val currentExpenses = listOf(
            createExpense(1, 100000L, ExpenseType.NEED, foodCategory, LocalDate.of(2026, 10, 12)),
            createExpense(2, 50000L, ExpenseType.WASTE, gamingCategory, LocalDate.of(2026, 10, 13)),
        ) // Total = 150000L, Waste = 50000L -> Waste ratio = 33.333%

        val previousExpenses = listOf(
            createExpense(3, 80000L, ExpenseType.NEED, foodCategory, LocalDate.of(2026, 10, 5)),
            createExpense(4, 20000L, ExpenseType.WASTE, gamingCategory, LocalDate.of(2026, 10, 6)),
        ) // Prev Total = 100000L, Prev Waste = 20000L -> Prev Waste ratio = 20.0%

        val week = Period.Week(LocalDate.of(2026, 10, 12))
        val summary = AnalyticsEngine.summarize(
            expenses = currentExpenses,
            previousExpenses = previousExpenses,
            period = week,
        )

        val prev = summary.previous
        assertNotNull(prev)
        assertEquals(100000L, prev?.previousTotalMinor)
        assertEquals(80000L, prev?.previousNeedMinor)
        assertEquals(20000L, prev?.previousWasteMinor)
        assertEquals(20.0, prev?.previousWasteRatioPct ?: 0.0, 0.001)

        // Total delta: (150000 - 100000) / 100000 * 100 = +50.0%
        assertEquals(50.0, prev?.totalDeltaPct ?: 0.0, 0.001)

        // Waste ratio delta: 33.333% - 20.0% = +13.333 points
        val expectedRatioDelta = (50000.0 / 150000.0 * 100.0) - 20.0
        assertEquals(expectedRatioDelta, prev?.wasteRatioDeltaPts ?: 0.0, 0.001)
    }

    @Test
    fun `summarize hides totalDeltaPct when previous total is zero`() {
        val currentExpenses = listOf(
            createExpense(1, 100000L, ExpenseType.NEED, foodCategory, LocalDate.of(2026, 10, 12)),
        )

        val week = Period.Week(LocalDate.of(2026, 10, 12))
        val summary = AnalyticsEngine.summarize(
            expenses = currentExpenses,
            previousExpenses = emptyList(),
            period = week,
        )

        assertNotNull(summary.previous)
        assertNull(summary.previous?.totalDeltaPct)
        assertEquals(0L, summary.previous?.previousTotalMinor)
    }

    @Test
    fun `summarize breaks Month into weekly totals aligned to weekStartDay`() {
        val month = Period.Month(YearMonth.of(2026, 10))
        val expenses = listOf(
            createExpense(1, 10000L, ExpenseType.NEED, foodCategory, LocalDate.of(2026, 10, 1)),
            createExpense(2, 20000L, ExpenseType.WASTE, gamingCategory, LocalDate.of(2026, 10, 15)),
        )

        val summary = AnalyticsEngine.summarize(
            expenses = expenses,
            period = month,
            weekStartDay = WeekStartDay.MONDAY,
        )

        assertTrue(summary.weeklyTotals.isNotEmpty())
        assertEquals(summary.totalMinor, summary.weeklyTotals.sumOf { it.totalMinor })
    }

    @Test
    fun `calculateCleanDayStreak calculates consecutive clean days correctly`() {
        val dailyTotals = listOf(
            DayTotal(LocalDate.of(2026, 10, 10), 1000L, 1000L, 0L), // Clean
            DayTotal(LocalDate.of(2026, 10, 11), 2000L, 1000L, 1000L), // NOT Clean
            DayTotal(LocalDate.of(2026, 10, 12), 1000L, 1000L, 0L), // Clean
            DayTotal(LocalDate.of(2026, 10, 13), 0L, 0L, 0L), // Clean
            DayTotal(LocalDate.of(2026, 10, 14), 5000L, 5000L, 0L), // Clean
        )

        // Up to Oct 14: Oct 12, 13, 14 are clean -> streak of 3
        assertEquals(3, AnalyticsEngine.calculateCleanDayStreak(dailyTotals, LocalDate.of(2026, 10, 14)))

        // Up to Oct 11: Oct 11 had waste -> streak of 0
        assertEquals(0, AnalyticsEngine.calculateCleanDayStreak(dailyTotals, LocalDate.of(2026, 10, 11)))

        // Up to Oct 10: Oct 10 was clean -> streak of 1
        assertEquals(1, AnalyticsEngine.calculateCleanDayStreak(dailyTotals, LocalDate.of(2026, 10, 10)))
    }
}
