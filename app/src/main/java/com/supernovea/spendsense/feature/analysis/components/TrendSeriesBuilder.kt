/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.components

import com.supernovea.spendsense.domain.analytics.PeriodCalculator
import com.supernovea.spendsense.domain.model.DayTotal
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.WeekTotal
import com.supernovea.spendsense.feature.analysis.AnalysisPeriodMode
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow

data class TrendPoint(
    val index: Int,
    val xLabel: String,
    val fullDateLabel: String,
    val expenseMinor: Long,
    val incomeMinor: Long,
)

data class TrendSeriesData(
    val points: List<TrendPoint>,
    val maxYMinor: Long,
    val niceYTicks: List<Long>,
    val hasIncomeData: Boolean,
    val hasExpenseData: Boolean,
    val isSingleDataPoint: Boolean,
)

/**
 * Pure function builder for line charts:
 * 1. Straight line segments (no cubic bezier smoothing that invents false data).
 * 2. Two series: Income and Expense over time.
 * 3. Nice Y-axis ticks calculation starting at 0.
 * 4. Single data point detection.
 */
object TrendSeriesBuilder {

    fun build(
        summary: PeriodSummary?,
        mode: AnalysisPeriodMode,
    ): TrendSeriesData {
        if (summary == null) {
            return TrendSeriesData(
                points = emptyList(),
                maxYMinor = 0L,
                niceYTicks = listOf(0L),
                hasIncomeData = false,
                hasExpenseData = false,
                isSingleDataPoint = false,
            )
        }

        val incomeMap = summary.dailyIncome.associateBy { it.date }

        val points = when (mode) {
            AnalysisPeriodMode.WEEK -> {
                summary.dailyTotals.mapIndexed { index, day: DayTotal ->
                    val shortDay = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val fullDate = "${day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${day.date.dayOfMonth} ${day.date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}"
                    val incomeForDay = incomeMap[day.date]?.totalMinor ?: 0L

                    TrendPoint(
                        index = index,
                        xLabel = shortDay,
                        fullDateLabel = fullDate,
                        expenseMinor = day.totalMinor,
                        incomeMinor = incomeForDay,
                    )
                }
            }
            AnalysisPeriodMode.MONTH -> {
                summary.weeklyTotals.mapIndexed { index, week: WeekTotal ->
                    val incomeForWeek = summary.dailyIncome.filter {
                        !it.date.isBefore(week.startDate) && !it.date.isAfter(week.endDate)
                    }.sumOf { it.totalMinor }

                    val startMonth = week.startDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val endMonth = week.endDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val fullDate = "Week ${week.weekNumber} (${week.startDate.dayOfMonth} $startMonth - ${week.endDate.dayOfMonth} $endMonth)"

                    TrendPoint(
                        index = index,
                        xLabel = "W${week.weekNumber}",
                        fullDateLabel = fullDate,
                        expenseMinor = week.totalMinor,
                        incomeMinor = incomeForWeek,
                    )
                }
            }
            AnalysisPeriodMode.CUSTOM -> {
                val range = PeriodCalculator.getDateRange(summary.period)
                val daysCount = range.daysCount
                when {
                    daysCount <= 31 -> {
                        summary.dailyTotals.mapIndexed { index, day: DayTotal ->
                            val shortDay = if (daysCount <= 7) {
                                day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            } else {
                                "${day.date.dayOfMonth}"
                            }
                            val fullDate = "${day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, ${day.date.dayOfMonth} ${day.date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}"
                            val incomeForDay = incomeMap[day.date]?.totalMinor ?: 0L

                            TrendPoint(
                                index = index,
                                xLabel = shortDay,
                                fullDateLabel = fullDate,
                                expenseMinor = day.totalMinor,
                                incomeMinor = incomeForDay,
                            )
                        }
                    }
                    daysCount <= 120 -> {
                        summary.weeklyTotals.mapIndexed { index, week: WeekTotal ->
                            val incomeForWeek = summary.dailyIncome.filter {
                                !it.date.isBefore(week.startDate) && !it.date.isAfter(week.endDate)
                            }.sumOf { it.totalMinor }

                            val startMonth = week.startDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            val endMonth = week.endDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            val fullDate = "Week ${week.weekNumber} (${week.startDate.dayOfMonth} $startMonth - ${week.endDate.dayOfMonth} $endMonth)"

                            TrendPoint(
                                index = index,
                                xLabel = "W${week.weekNumber}",
                                fullDateLabel = fullDate,
                                expenseMinor = week.totalMinor,
                                incomeMinor = incomeForWeek,
                            )
                        }
                    }
                    else -> {
                        val months = mutableListOf<YearMonth>()
                        var m = YearMonth.from(range.start)
                        val endM = YearMonth.from(range.end)
                        while (!m.isAfter(endM)) {
                            months.add(m)
                            m = m.plusMonths(1)
                        }
                        val expenseByMonth = summary.dailyTotals.groupBy { YearMonth.from(it.date) }
                        val incomeByMonth = summary.dailyIncome.groupBy { YearMonth.from(it.date) }

                        months.mapIndexed { index, ym ->
                            val monthExpenses = expenseByMonth[ym].orEmpty().sumOf { it.totalMinor }
                            val monthIncomes = incomeByMonth[ym].orEmpty().sumOf { it.totalMinor }
                            val shortLabel = ym.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            val fullLabel = ym.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))

                            TrendPoint(
                                index = index,
                                xLabel = shortLabel,
                                fullDateLabel = fullLabel,
                                expenseMinor = monthExpenses,
                                incomeMinor = monthIncomes,
                            )
                        }
                    }
                }
            }
        }

        val maxExpense = points.maxOfOrNull { it.expenseMinor } ?: 0L
        val maxIncome = points.maxOfOrNull { it.incomeMinor } ?: 0L
        val rawMax = max(maxExpense, maxIncome)

        val activeCount = points.count { it.expenseMinor > 0L || it.incomeMinor > 0L }
        val isSingleDataPoint = activeCount == 1

        val (niceMax, ticks) = calculateNiceTicks(rawMax)

        return TrendSeriesData(
            points = points,
            maxYMinor = niceMax,
            niceYTicks = ticks,
            hasIncomeData = maxIncome > 0L,
            hasExpenseData = maxExpense > 0L,
            isSingleDataPoint = isSingleDataPoint,
        )
    }

    /**
     * Calculates nice round Y-axis tick intervals starting at 0 up to niceMax.
     */
    fun calculateNiceTicks(maxYMinor: Long): Pair<Long, List<Long>> {
        if (maxYMinor <= 0L) {
            val defaultStep = 100_000L // ₹1,000
            val defaultTicks = listOf(0L, defaultStep, defaultStep * 2, defaultStep * 3)
            return Pair(defaultStep * 3, defaultTicks)
        }

        val maxRupees = maxYMinor / 100.0
        val tickCount = 4
        val rawStep = maxRupees / tickCount
        val magnitude = 10.0.pow(floor(log10(rawStep)))
        val normalizedStep = rawStep / magnitude

        val roundedStep = when {
            normalizedStep <= 1.2 -> 1.0
            normalizedStep <= 2.5 -> 2.0
            normalizedStep <= 6.0 -> 5.0
            else -> 10.0
        }

        val niceStepRupees = roundedStep * magnitude
        val niceMaxRupees = ceil(maxRupees / niceStepRupees) * niceStepRupees

        val ticks = mutableListOf<Long>()
        var current = 0.0
        while (current <= niceMaxRupees + (niceStepRupees * 0.01)) {
            ticks.add((current * 100).toLong())
            current += niceStepRupees
        }

        return Pair((niceMaxRupees * 100).toLong(), ticks)
    }
}
