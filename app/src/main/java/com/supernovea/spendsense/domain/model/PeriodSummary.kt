/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

/**
 * Complete aggregated analytics summary for an evaluation period (Week or Month).
 * Computed exclusively by AnalyticsEngine following PRD §8 formulas.
 */
data class PeriodSummary(
    val period: Period,
    val totalMinor: Long,
    val needMinor: Long,
    val wasteMinor: Long,
    val wasteRatioPct: Double,
    val needRatioPct: Double,
    val avgDailyMinor: Long,
    val peakDay: DayTotal?,
    val dailyTotals: List<DayTotal>,
    val weeklyTotals: List<WeekTotal> = emptyList(),
    val topCategories: List<CategoryShare>,
    val previous: PreviousComparison?,
    val cleanDays: Int,
    val incomeMinor: Long = 0L,
    val netMinor: Long = 0L,
    val savingsRatePct: Double? = null,
    val spendRatePct: Double? = null,
    val incomeBySource: List<IncomeSourceShare> = emptyList(),
    val dailyIncome: List<DayIncome> = emptyList(),
)
