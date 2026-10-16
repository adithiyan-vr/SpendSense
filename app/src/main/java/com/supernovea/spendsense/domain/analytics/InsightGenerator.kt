/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.analytics

import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.Insight
import com.supernovea.spendsense.domain.model.InsightSeverity
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.UserProfileType
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

/**
 * Pure Kotlin rule engine that generates actionable behavioral insights
 * based on [PeriodSummary] and optional [Budget].
 * Adheres to PRD §8 rules and Update.md §4.2 with encouraging, neutral language.
 */
object InsightGenerator {

    fun generate(
        summary: PeriodSummary,
        budget: Budget? = null,
        today: LocalDate = LocalDate.now(),
        userProfile: UserProfileType = UserProfileType.OTHER,
        lastIncomeDate: LocalDate? = null,
        daysSinceLastIncome: Int? = null,
    ): List<Insight> {
        val insights = mutableListOf<Insight>()

        // 1. Zero Waste achievement
        if (summary.totalMinor > 0L && summary.wasteMinor == 0L) {
            insights.add(
                Insight(
                    id = "zero_waste",
                    title = "100% Needs",
                    message = "Every expense this period went directly toward essential needs. Incredible discipline!",
                    severity = InsightSeverity.POSITIVE,
                )
            )
        }

        // 2. Clean Day streak
        val cleanStreak = AnalyticsEngine.calculateCleanDayStreak(summary.dailyTotals, today)
        if (cleanStreak >= 3) {
            insights.add(
                Insight(
                    id = "clean_streak_$cleanStreak",
                    title = "$cleanStreak Clean Days in a row",
                    message = "You've gone $cleanStreak consecutive days without any waste spending. Keep this streak alive!",
                    severity = InsightSeverity.POSITIVE,
                )
            )
        }

        // 3. Period-over-period Waste Ratio delta
        summary.previous?.let { prev ->
            if (prev.previousTotalMinor > 0L) {
                when {
                    prev.wasteRatioDeltaPts >= 5.0 -> {
                        insights.add(
                            Insight(
                                id = "waste_ratio_rose",
                                title = "Waste share increased",
                                message = String.format(
                                    Locale.US,
                                    "Waste ratio increased by %.1f percentage points vs the previous period.",
                                    prev.wasteRatioDeltaPts,
                                ),
                                severity = InsightSeverity.WARNING,
                            )
                        )
                    }
                    prev.wasteRatioDeltaPts <= -4.0 -> {
                        insights.add(
                            Insight(
                                id = "waste_ratio_fell",
                                title = "Waste share decreased",
                                message = String.format(
                                    Locale.US,
                                    "Waste ratio fell by %.1f percentage points vs the previous period. Great progress!",
                                    abs(prev.wasteRatioDeltaPts),
                                ),
                                severity = InsightSeverity.POSITIVE,
                            )
                        )
                    }
                }
            }
        }

        // 4. Waste Category Dominance (if single category accounts for >= 40% of waste spend)
        if (summary.wasteMinor > 0L) {
            val topWasteCategory = summary.topCategories
                .filter { it.wasteMinor > 0L }
                .maxByOrNull { it.wasteMinor }

            if (topWasteCategory != null) {
                val wasteShare = (topWasteCategory.wasteMinor.toDouble() / summary.wasteMinor.toDouble()) * 100.0
                if (wasteShare >= 40.0) {
                    insights.add(
                        Insight(
                            id = "dominant_waste_${topWasteCategory.category.id}",
                            title = "High waste concentration",
                            message = String.format(
                                Locale.US,
                                "%s accounts for %.0f%% of your total waste spending.",
                                topWasteCategory.category.name,
                                wasteShare,
                            ),
                            severity = InsightSeverity.INFO,
                        )
                    )
                }
            }
        }

        // 5. Income insight rules (Update.md §4.2)
        // 5a. Spend rate >= 80% before day 20 of the month
        if (summary.period is Period.Month && today.dayOfMonth < 20) {
            summary.spendRatePct?.let { spendRate ->
                if (spendRate >= 80.0) {
                    val label = if (userProfile == UserProfileType.STUDENT) "pocket money" else "income"
                    insights.add(
                        Insight(
                            id = "spend_rate_early_80",
                            title = "High spend rate early",
                            message = String.format(
                                Locale.US,
                                "You've spent %.0f%% of this month's %s before day 20.",
                                spendRate,
                                label,
                            ),
                            severity = InsightSeverity.WARNING,
                        )
                    )
                }
            }
        }

        // 5b. Savings Rate rose by >= 5 points vs previous period
        summary.previous?.savingsRateDeltaPts?.let { deltaPts ->
            if (deltaPts >= 5.0) {
                insights.add(
                    Insight(
                        id = "savings_rate_rose",
                        title = "Savings rate improved",
                        message = String.format(
                            Locale.US,
                            "Savings rate rose by %.1f percentage points vs the previous period. Great progress!",
                            deltaPts,
                        ),
                        severity = InsightSeverity.POSITIVE,
                    )
                )
            }
        }

        // 5c. Net negative (when income was recorded and spending exceeded income)
        if (summary.incomeMinor > 0L && summary.netMinor < 0L) {
            val periodLabel = if (summary.period is Period.Month) "this month" else "this period"
            val overspent = Money.format(abs(summary.netMinor))
            insights.add(
                Insight(
                    id = "net_negative",
                    title = "Net negative $periodLabel",
                    message = "You spent $overspent more than you received $periodLabel.",
                    severity = InsightSeverity.INFO,
                )
            )
        }

        // 5d. No income recorded in 35 days (for Student profile)
        val daysWithoutIncome = daysSinceLastIncome
            ?: lastIncomeDate?.let { ChronoUnit.DAYS.between(it, today).toInt() }
        if (userProfile == UserProfileType.STUDENT && daysWithoutIncome != null && daysWithoutIncome >= 35) {
            insights.add(
                Insight(
                    id = "reminder_pocket_money_35",
                    title = "Log your pocket money",
                    message = "No pocket money recorded in 35+ days. Remember to log it when you receive it.",
                    severity = InsightSeverity.INFO,
                )
            )
        }

        // 6. Budget checks (when evaluating a Month period)
        if (budget != null && summary.period is Period.Month) {
            // Overall budget checks
            val totalProgressPct = (summary.totalMinor.toDouble() / budget.totalLimitMinor.toDouble()) * 100.0
            if (totalProgressPct >= 100.0) {
                insights.add(
                    Insight(
                        id = "budget_total_exceeded",
                        title = "Monthly budget exceeded",
                        message = "Total spending has reached 100% of your monthly budget limit.",
                        severity = InsightSeverity.WARNING,
                    )
                )
            } else if (totalProgressPct >= 80.0) {
                insights.add(
                    Insight(
                        id = "budget_total_80",
                        title = "Approaching monthly budget",
                        message = String.format(
                            Locale.US,
                            "You have used %.0f%% of your total monthly budget.",
                            totalProgressPct,
                        ),
                        severity = InsightSeverity.WARNING,
                    )
                )
            }

            // Waste budget checks
            budget.wasteLimitMinor?.let { wasteLimit ->
                if (wasteLimit > 0L) {
                    val wasteProgressPct = (summary.wasteMinor.toDouble() / wasteLimit.toDouble()) * 100.0
                    if (wasteProgressPct >= 100.0) {
                        insights.add(
                            Insight(
                                id = "budget_waste_exceeded",
                                title = "Waste limit exceeded",
                                message = "You have exceeded your monthly waste limit.",
                                severity = InsightSeverity.WARNING,
                            )
                        )
                    } else if (wasteProgressPct >= 80.0) {
                        if (today.dayOfMonth < 20) {
                            insights.add(
                                Insight(
                                    id = "budget_waste_early_80",
                                    title = "Approaching waste limit early",
                                    message = "You've reached 80% of your waste limit before day 20 of the month.",
                                    severity = InsightSeverity.WARNING,
                                )
                            )
                        } else {
                            insights.add(
                                Insight(
                                    id = "budget_waste_80",
                                    title = "Approaching waste limit",
                                    message = String.format(
                                        Locale.US,
                                        "You have reached %.0f%% of your monthly waste limit.",
                                        wasteProgressPct,
                                    ),
                                    severity = InsightSeverity.WARNING,
                                )
                            )
                        }
                    }
                }
            }
        }

        return insights
    }
}
