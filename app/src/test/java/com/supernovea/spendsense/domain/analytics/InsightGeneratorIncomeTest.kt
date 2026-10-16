/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.analytics

import com.supernovea.spendsense.domain.model.InsightSeverity
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.PreviousComparison
import com.supernovea.spendsense.domain.model.UserProfileType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class InsightGeneratorIncomeTest {

    private fun createBaseSummary(
        period: Period = Period.Month(YearMonth.of(2026, 10)),
        totalMinor: Long = 85000L,
        incomeMinor: Long = 100000L,
        spendRatePct: Double? = 85.0,
        savingsRatePct: Double? = 15.0,
        previous: PreviousComparison? = null,
    ): PeriodSummary {
        return PeriodSummary(
            period = period,
            totalMinor = totalMinor,
            needMinor = totalMinor,
            wasteMinor = 0L,
            wasteRatioPct = 0.0,
            needRatioPct = 100.0,
            avgDailyMinor = totalMinor / 10,
            peakDay = null,
            dailyTotals = emptyList(),
            topCategories = emptyList(),
            previous = previous,
            cleanDays = 0,
            incomeMinor = incomeMinor,
            netMinor = incomeMinor - totalMinor,
            savingsRatePct = savingsRatePct,
            spendRatePct = spendRatePct,
        )
    }

    @Test
    fun `spend rate 80 percent or higher before day 20 triggers early spend caution`() {
        val today = LocalDate.of(2026, 10, 15) // Day 15 < 20
        val summary = createBaseSummary(spendRatePct = 85.0)

        // For student profile: mentions pocket money
        val studentInsights = InsightGenerator.generate(
            summary = summary,
            today = today,
            userProfile = UserProfileType.STUDENT,
        )
        val studentCaution = studentInsights.find { it.id == "spend_rate_early_80" }
        assertNotNull(studentCaution)
        assertEquals(InsightSeverity.WARNING, studentCaution?.severity)
        assertTrue(studentCaution?.message?.contains("pocket money") == true)

        // For professional profile: mentions income
        val profInsights = InsightGenerator.generate(
            summary = summary,
            today = today,
            userProfile = UserProfileType.WORKING_PROFESSIONAL,
        )
        val profCaution = profInsights.find { it.id == "spend_rate_early_80" }
        assertNotNull(profCaution)
        assertTrue(profCaution?.message?.contains("income") == true)

        // On or after day 20: does not trigger early caution
        val lateDate = LocalDate.of(2026, 10, 21)
        val lateInsights = InsightGenerator.generate(
            summary = summary,
            today = lateDate,
            userProfile = UserProfileType.STUDENT,
        )
        assertNull(lateInsights.find { it.id == "spend_rate_early_80" })
    }

    @Test
    fun `savings rate rising by 5 points or more triggers positive reinforcement`() {
        val previous = PreviousComparison(
            previousTotalMinor = 50000L,
            previousNeedMinor = 50000L,
            previousWasteMinor = 0L,
            previousWasteRatioPct = 0.0,
            totalDeltaPct = 0.0,
            wasteRatioDeltaPts = 0.0,
            savingsRateDeltaPts = 7.5,
        )
        val summary = createBaseSummary(previous = previous)

        val insights = InsightGenerator.generate(summary = summary)
        val savingsInsight = insights.find { it.id == "savings_rate_rose" }
        assertNotNull(savingsInsight)
        assertEquals(InsightSeverity.POSITIVE, savingsInsight?.severity)
        assertTrue(savingsInsight?.message?.contains("7.5 percentage points") == true)

        // Below 5.0 points delta -> does not trigger
        val smallDeltaPrev = previous.copy(savingsRateDeltaPts = 4.9)
        val smallDeltaInsights = InsightGenerator.generate(
            summary = createBaseSummary(previous = smallDeltaPrev),
        )
        assertNull(smallDeltaInsights.find { it.id == "savings_rate_rose" })
    }

    @Test
    fun `net negative triggers neutral info insight when income is recorded`() {
        val summary = createBaseSummary(
            totalMinor = 120000L,
            incomeMinor = 100000L, // Net is -20,000 (spent 200 more than received)
            spendRatePct = 120.0,
            savingsRatePct = -20.0,
        )

        val insights = InsightGenerator.generate(summary = summary)
        val netNegInsight = insights.find { it.id == "net_negative" }
        assertNotNull(netNegInsight)
        assertEquals(InsightSeverity.INFO, netNegInsight?.severity) // Neutral INFO, never red or shaming
        assertTrue(netNegInsight?.message?.contains("You spent ₹200 more than you received this month.") == true)

        // When zero income was recorded, net_negative should NOT fire (avoids spurious alert on expense-only accounts)
        val noIncomeSummary = createBaseSummary(
            totalMinor = 50000L,
            incomeMinor = 0L,
            spendRatePct = null,
            savingsRatePct = null,
        )
        val noIncomeInsights = InsightGenerator.generate(summary = noIncomeSummary)
        assertNull(noIncomeInsights.find { it.id == "net_negative" })
    }

    @Test
    fun `student profile with no pocket money in 35 days triggers reminder`() {
        val today = LocalDate.of(2026, 10, 15)
        val summary = createBaseSummary()

        // 40 days since last income -> triggers reminder
        val studentInsights = InsightGenerator.generate(
            summary = summary,
            today = today,
            userProfile = UserProfileType.STUDENT,
            daysSinceLastIncome = 40,
        )
        val reminder = studentInsights.find { it.id == "reminder_pocket_money_35" }
        assertNotNull(reminder)
        assertEquals(InsightSeverity.INFO, reminder?.severity)

        // For Working Professional -> does NOT trigger
        val profInsights = InsightGenerator.generate(
            summary = summary,
            today = today,
            userProfile = UserProfileType.WORKING_PROFESSIONAL,
            daysSinceLastIncome = 40,
        )
        assertNull(profInsights.find { it.id == "reminder_pocket_money_35" })

        // Student with recent income (20 days) -> does NOT trigger
        val recentInsights = InsightGenerator.generate(
            summary = summary,
            today = today,
            userProfile = UserProfileType.STUDENT,
            daysSinceLastIncome = 20,
        )
        assertNull(recentInsights.find { it.id == "reminder_pocket_money_35" })
    }
}
