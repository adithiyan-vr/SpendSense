/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.analytics

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.CategoryShare
import com.supernovea.spendsense.domain.model.DayTotal
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.InsightSeverity
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.PreviousComparison
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class InsightGeneratorTest {

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

    private fun createBaseSummary(
        totalMinor: Long = 100000L,
        needMinor: Long = 70000L,
        wasteMinor: Long = 30000L,
        previous: PreviousComparison? = null,
        dailyTotals: List<DayTotal> = emptyList(),
        topCategories: List<CategoryShare> = emptyList(),
        period: Period = Period.Month(YearMonth.of(2026, 10)),
    ): PeriodSummary {
        val wasteRatio = if (totalMinor == 0L) 0.0 else (wasteMinor.toDouble() / totalMinor.toDouble()) * 100.0
        return PeriodSummary(
            period = period,
            totalMinor = totalMinor,
            needMinor = needMinor,
            wasteMinor = wasteMinor,
            wasteRatioPct = wasteRatio,
            needRatioPct = 100.0 - wasteRatio,
            avgDailyMinor = totalMinor / 31,
            peakDay = null,
            dailyTotals = dailyTotals,
            topCategories = topCategories,
            previous = previous,
            cleanDays = 0,
        )
    }

    @Test
    fun `generate triggers zero_waste insight when spending is 100 percent needs`() {
        val summary = createBaseSummary(
            totalMinor = 50000L,
            needMinor = 50000L,
            wasteMinor = 0L,
        )

        val insights = InsightGenerator.generate(summary)
        val zeroWaste = insights.find { it.id == "zero_waste" }
        assertNotNull(zeroWaste)
        assertEquals(InsightSeverity.POSITIVE, zeroWaste?.severity)
    }

    @Test
    fun `generate triggers clean streak insight when 3 or more clean days in a row`() {
        val dailyTotals = listOf(
            DayTotal(LocalDate.of(2026, 10, 10), 1000L, 1000L, 0L),
            DayTotal(LocalDate.of(2026, 10, 11), 1000L, 1000L, 0L),
            DayTotal(LocalDate.of(2026, 10, 12), 1000L, 1000L, 0L),
        )
        val summary = createBaseSummary(dailyTotals = dailyTotals)

        val insights = InsightGenerator.generate(summary, today = LocalDate.of(2026, 10, 12))
        val streakInsight = insights.find { it.id.startsWith("clean_streak_") }
        assertNotNull(streakInsight)
        assertEquals(InsightSeverity.POSITIVE, streakInsight?.severity)
    }

    @Test
    fun `generate triggers waste increase warning when waste ratio rose by 5 or more points`() {
        val prev = PreviousComparison(
            previousTotalMinor = 100000L,
            previousNeedMinor = 80000L,
            previousWasteMinor = 20000L,
            previousWasteRatioPct = 20.0,
            totalDeltaPct = 0.0,
            wasteRatioDeltaPts = 10.0, // Rose by 10 points
        )
        val summary = createBaseSummary(previous = prev)

        val insights = InsightGenerator.generate(summary)
        val warning = insights.find { it.id == "waste_ratio_rose" }
        assertNotNull(warning)
        assertEquals(InsightSeverity.WARNING, warning?.severity)
    }

    @Test
    fun `generate triggers waste decrease positive insight when waste ratio fell by 4 or more points`() {
        val prev = PreviousComparison(
            previousTotalMinor = 100000L,
            previousNeedMinor = 70000L,
            previousWasteMinor = 30000L,
            previousWasteRatioPct = 30.0,
            totalDeltaPct = 0.0,
            wasteRatioDeltaPts = -5.0, // Fell by 5 points
        )
        val summary = createBaseSummary(previous = prev)

        val insights = InsightGenerator.generate(summary)
        val praise = insights.find { it.id == "waste_ratio_fell" }
        assertNotNull(praise)
        assertEquals(InsightSeverity.POSITIVE, praise?.severity)
    }

    @Test
    fun `generate triggers category dominance when single category accounts for 40 percent or more of waste`() {
        val shares = listOf(
            CategoryShare(
                category = gamingCategory,
                totalMinor = 25000L,
                needMinor = 0L,
                wasteMinor = 25000L, // 25000 / 30000 = 83.3% of waste
                sharePct = 25.0,
                defaultType = ExpenseType.WASTE,
            ),
            CategoryShare(
                category = foodCategory,
                totalMinor = 75000L,
                needMinor = 70000L,
                wasteMinor = 5000L,
                sharePct = 75.0,
                defaultType = ExpenseType.NEED,
            ),
        )
        val summary = createBaseSummary(wasteMinor = 30000L, topCategories = shares)

        val insights = InsightGenerator.generate(summary)
        val dominance = insights.find { it.id == "dominant_waste_2" }
        assertNotNull(dominance)
        assertEquals(InsightSeverity.INFO, dominance?.severity)
        assertTrue(dominance?.message?.contains("Gaming") == true)
    }

    @Test
    fun `generate triggers budget warnings at 80 percent and 100 percent thresholds`() {
        val budget = Budget(
            monthKey = "2026-10",
            totalLimitMinor = 100000L, // ₹1,000
            wasteLimitMinor = 30000L,  // ₹300
        )

        // Case 1: Total at 85% (₹850), Waste at 50% (₹150)
        val summary85 = createBaseSummary(totalMinor = 85000L, wasteMinor = 15000L)
        val insights85 = InsightGenerator.generate(summary85, budget)
        assertNotNull(insights85.find { it.id == "budget_total_80" })

        // Case 2: Total exceeded 100% (₹1,050)
        val summary105 = createBaseSummary(totalMinor = 105000L, wasteMinor = 15000L)
        val insights105 = InsightGenerator.generate(summary105, budget)
        assertNotNull(insights105.find { it.id == "budget_total_exceeded" })

        // Case 3: Waste at 85% (₹260) before day 20 (e.g. Day 10)
        val summaryWasteEarly = createBaseSummary(totalMinor = 50000L, wasteMinor = 26000L)
        val insightsEarly = InsightGenerator.generate(summaryWasteEarly, budget, today = LocalDate.of(2026, 10, 10))
        assertNotNull(insightsEarly.find { it.id == "budget_waste_early_80" })

        // Case 4: Waste exceeded 100% (₹350)
        val summaryWasteOver = createBaseSummary(totalMinor = 50000L, wasteMinor = 35000L)
        val insightsOver = InsightGenerator.generate(summaryWasteOver, budget)
        assertNotNull(insightsOver.find { it.id == "budget_waste_exceeded" })
    }
}
