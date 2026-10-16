/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis

import com.supernovea.spendsense.domain.model.DayIncome
import com.supernovea.spendsense.domain.model.DayTotal
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.WeekTotal
import com.supernovea.spendsense.feature.analysis.components.TrendSeriesBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TrendSeriesBuilderTest {

    @Test
    fun `null summary returns empty series data`() {
        val result = TrendSeriesBuilder.build(null, AnalysisPeriodMode.WEEK)
        assertTrue(result.points.isEmpty())
        assertFalse(result.hasIncomeData)
        assertFalse(result.hasExpenseData)
        assertFalse(result.isSingleDataPoint)
    }

    @Test
    fun `week mode maps 7 days of income and expense correctly`() {
        val start = LocalDate.of(2026, 3, 2) // Monday
        val dailyTotals = (0..6).map { i ->
            val date = start.plusDays(i.toLong())
            DayTotal(
                date = date,
                totalMinor = (i + 1) * 10_000L,
                needMinor = (i + 1) * 7_000L,
                wasteMinor = (i + 1) * 3_000L,
            )
        }

        val dailyIncome = listOf(
            DayIncome(date = start, totalMinor = 100_000L),
            DayIncome(date = start.plusDays(4), totalMinor = 50_000L),
        )

        val summary = PeriodSummary(
            period = Period.Week(start),
            totalMinor = dailyTotals.sumOf { it.totalMinor },
            needMinor = dailyTotals.sumOf { it.needMinor },
            wasteMinor = dailyTotals.sumOf { it.wasteMinor },
            wasteRatioPct = 30.0,
            needRatioPct = 70.0,
            avgDailyMinor = 40_000L,
            peakDay = dailyTotals.last(),
            dailyTotals = dailyTotals,
            topCategories = emptyList(),
            previous = null,
            cleanDays = 0,
            incomeMinor = 150_000L,
            dailyIncome = dailyIncome,
        )

        val result = TrendSeriesBuilder.build(summary, AnalysisPeriodMode.WEEK)
        assertEquals(7, result.points.size)
        assertTrue(result.hasExpenseData)
        assertTrue(result.hasIncomeData)
        assertFalse(result.isSingleDataPoint)

        // Point 0 (Monday): expense = 10,000, income = 100,000
        assertEquals(10_000L, result.points[0].expenseMinor)
        assertEquals(100_000L, result.points[0].incomeMinor)

        // Point 1 (Tuesday): expense = 20,000, income = 0
        assertEquals(20_000L, result.points[1].expenseMinor)
        assertEquals(0L, result.points[1].incomeMinor)

        // Point 4 (Friday): expense = 50,000, income = 50,000
        assertEquals(50_000L, result.points[4].expenseMinor)
        assertEquals(50_000L, result.points[4].incomeMinor)
    }

    @Test
    fun `single active entry flags isSingleDataPoint true`() {
        val start = LocalDate.of(2026, 3, 2)
        val dailyTotals = (0..6).map { i ->
            val date = start.plusDays(i.toLong())
            DayTotal(
                date = date,
                totalMinor = if (i == 2) 50_000L else 0L,
                needMinor = if (i == 2) 50_000L else 0L,
                wasteMinor = 0L,
            )
        }

        val summary = PeriodSummary(
            period = Period.Week(start),
            totalMinor = 50_000L,
            needMinor = 50_000L,
            wasteMinor = 0L,
            wasteRatioPct = 0.0,
            needRatioPct = 100.0,
            avgDailyMinor = 7_142L,
            peakDay = dailyTotals[2],
            dailyTotals = dailyTotals,
            topCategories = emptyList(),
            previous = null,
            cleanDays = 6,
            incomeMinor = 0L,
            dailyIncome = emptyList(),
        )

        val result = TrendSeriesBuilder.build(summary, AnalysisPeriodMode.WEEK)
        assertTrue(result.isSingleDataPoint)
    }

    @Test
    fun `month mode aggregates weekly totals and date-bounded income`() {
        val start = LocalDate.of(2026, 3, 1)
        val weeklyTotals = listOf(
            WeekTotal(weekNumber = 1, startDate = start, endDate = start.plusDays(6), totalMinor = 150_000L, needMinor = 100_000L, wasteMinor = 50_000L),
            WeekTotal(weekNumber = 2, startDate = start.plusDays(7), endDate = start.plusDays(13), totalMinor = 200_000L, needMinor = 150_000L, wasteMinor = 50_000L),
        )

        val dailyIncome = listOf(
            DayIncome(date = start.plusDays(2), totalMinor = 300_000L),
            DayIncome(date = start.plusDays(8), totalMinor = 100_000L),
        )

        val summary = PeriodSummary(
            period = Period.Month(java.time.YearMonth.of(2026, 3)),
            totalMinor = 350_000L,
            needMinor = 250_000L,
            wasteMinor = 100_000L,
            wasteRatioPct = 28.5,
            needRatioPct = 71.5,
            avgDailyMinor = 25_000L,
            peakDay = null,
            dailyTotals = emptyList(),
            weeklyTotals = weeklyTotals,
            topCategories = emptyList(),
            previous = null,
            cleanDays = 0,
            incomeMinor = 400_000L,
            dailyIncome = dailyIncome,
        )

        val result = TrendSeriesBuilder.build(summary, AnalysisPeriodMode.MONTH)
        assertEquals(2, result.points.size)
        assertEquals(150_000L, result.points[0].expenseMinor)
        assertEquals(300_000L, result.points[0].incomeMinor)
        assertEquals(200_000L, result.points[1].expenseMinor)
        assertEquals(100_000L, result.points[1].incomeMinor)
    }

    @Test
    fun `calculateNiceTicks generates valid ascending ticks starting at zero`() {
        val (niceMax, ticks) = TrendSeriesBuilder.calculateNiceTicks(345_000L) // ₹3,450
        assertEquals(0L, ticks.first())
        assertTrue(niceMax >= 345_000L)
        assertTrue(ticks.size in 3..6)

        // Verify ticks are strictly increasing
        for (i in 0 until ticks.size - 1) {
            assertTrue(ticks[i + 1] > ticks[i])
        }
    }
}
