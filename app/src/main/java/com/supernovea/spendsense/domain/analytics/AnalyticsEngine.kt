/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.analytics

import com.supernovea.spendsense.domain.model.CategoryShare
import com.supernovea.spendsense.domain.model.DayIncome
import com.supernovea.spendsense.domain.model.DayTotal
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.IncomeSourceShare
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.PreviousComparison
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.model.WeekTotal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * Pure Kotlin analytics computation engine.
 * Implements PRD §8 formulas with zero Android dependencies.
 */
object AnalyticsEngine {

    fun summarize(
        expenses: List<ExpenseWithCategory>,
        previousExpenses: List<ExpenseWithCategory>,
        period: Period,
    ): PeriodSummary = summarize(
        expenses = expenses,
        incomes = emptyList(),
        previousExpenses = previousExpenses,
        previousIncomes = emptyList(),
        period = period,
        today = LocalDate.now(),
        weekStartDay = WeekStartDay.MONDAY,
    )

    /**
     * Summarizes expenses and incomes for the given [period] following PRD §8 and Update.md §4.
     *
     * @param expenses Expenses in the current period.
     * @param incomes Incomes in the current period.
     * @param previousExpenses Expenses in the preceding period.
     * @param previousIncomes Incomes in the preceding period.
     * @param period The current evaluation period (Week, Month, or Custom).
     * @param today Reference date for calculating elapsed days and clean days (defaults to now).
     * @param weekStartDay Preferred start day of the week (Monday or Sunday).
     */
    fun summarize(
        expenses: List<ExpenseWithCategory>,
        incomes: List<IncomeWithSource> = emptyList(),
        previousExpenses: List<ExpenseWithCategory> = emptyList(),
        previousIncomes: List<IncomeWithSource> = emptyList(),
        period: Period,
        today: LocalDate = LocalDate.now(),
        weekStartDay: WeekStartDay = WeekStartDay.MONDAY,
    ): PeriodSummary {
        val range = PeriodCalculator.getDateRange(period)
        val currentExpenses = expenses.filter { it.expense.occurredDate in range }
        val currentIncomes = incomes.filter { it.income.receivedDate in range }

        val totalMinor = currentExpenses.sumOf { it.expense.amountMinor }
        val needMinor = currentExpenses
            .filter { it.expense.type == ExpenseType.NEED }
            .sumOf { it.expense.amountMinor }
        val wasteMinor = currentExpenses
            .filter { it.expense.type == ExpenseType.WASTE }
            .sumOf { it.expense.amountMinor }

        val wasteRatioPct = if (totalMinor == 0L) {
            0.0
        } else {
            (wasteMinor.toDouble() / totalMinor.toDouble()) * 100.0
        }

        val needRatioPct = if (totalMinor == 0L) {
            0.0
        } else {
            100.0 - wasteRatioPct
        }

        val incomeMinor = currentIncomes.sumOf { it.income.amountMinor }
        val netMinor = incomeMinor - totalMinor
        val savingsRatePct = if (incomeMinor > 0L) {
            (netMinor.toDouble() / incomeMinor.toDouble()) * 100.0
        } else {
            null
        }
        val spendRatePct = if (incomeMinor > 0L) {
            (totalMinor.toDouble() / incomeMinor.toDouble()) * 100.0
        } else {
            null
        }

        val daysElapsed = calculateDaysElapsed(range, today)
        val avgDailyMinor = if (daysElapsed <= 0) {
            0L
        } else {
            totalMinor / daysElapsed
        }

        val dailyTotals = buildDailyTotals(range, currentExpenses)
        val dailyIncome = buildDailyIncomes(range, currentIncomes)

        val peakDay = if (totalMinor == 0L) {
            null
        } else {
            dailyTotals.filter { it.totalMinor > 0L }.maxByOrNull { it.totalMinor }
        }

        val weeklyTotals = if (period is Period.Month || (period is Period.Custom && range.daysCount > 31)) {
            buildWeeklyTotals(range, currentExpenses, weekStartDay)
        } else {
            emptyList()
        }

        val topCategories = buildCategoryShares(currentExpenses, totalMinor)
        val incomeBySource = buildIncomeSourceShares(currentIncomes, incomeMinor)

        val previous = buildPreviousComparison(
            currentPeriod = period,
            previousExpenses = previousExpenses,
            previousIncomes = previousIncomes,
            currentTotalMinor = totalMinor,
            currentWasteRatioPct = wasteRatioPct,
            currentIncomeMinor = incomeMinor,
            currentNetMinor = netMinor,
            currentSavingsRatePct = savingsRatePct,
        )

        val cleanDays = calculateCleanDays(dailyTotals, range, today)

        return PeriodSummary(
            period = period,
            totalMinor = totalMinor,
            needMinor = needMinor,
            wasteMinor = wasteMinor,
            wasteRatioPct = wasteRatioPct,
            needRatioPct = needRatioPct,
            avgDailyMinor = avgDailyMinor,
            peakDay = peakDay,
            dailyTotals = dailyTotals,
            weeklyTotals = weeklyTotals,
            topCategories = topCategories,
            previous = previous,
            cleanDays = cleanDays,
            incomeMinor = incomeMinor,
            netMinor = netMinor,
            savingsRatePct = savingsRatePct,
            spendRatePct = spendRatePct,
            incomeBySource = incomeBySource,
            dailyIncome = dailyIncome,
        )
    }

    /**
     * Determines the number of elapsed days in [range] relative to [today].
     * Past periods use the full period duration. Ongoing periods count days up to [today].
     * Future periods return 0.
     */
    fun calculateDaysElapsed(range: PeriodCalculator.DateRange, today: LocalDate): Int {
        return when {
            today.isBefore(range.start) -> 0
            today.isAfter(range.end) -> range.daysCount
            else -> (ChronoUnit.DAYS.between(range.start, today) + 1).toInt()
        }
    }

    /**
     * Constructs a continuous list of [DayTotal] for each day in [range].
     */
    fun buildDailyTotals(
        range: PeriodCalculator.DateRange,
        expenses: List<ExpenseWithCategory>,
    ): List<DayTotal> {
        val expensesByDate = expenses.groupBy { it.expense.occurredDate }
        val result = mutableListOf<DayTotal>()

        var current = range.start
        while (!current.isAfter(range.end)) {
            val dayList = expensesByDate[current].orEmpty()
            val dayTotal = dayList.sumOf { it.expense.amountMinor }
            val dayNeed = dayList.filter { it.expense.type == ExpenseType.NEED }.sumOf { it.expense.amountMinor }
            val dayWaste = dayList.filter { it.expense.type == ExpenseType.WASTE }.sumOf { it.expense.amountMinor }

            result.add(
                DayTotal(
                    date = current,
                    totalMinor = dayTotal,
                    needMinor = dayNeed,
                    wasteMinor = dayWaste,
                )
            )
            current = current.plusDays(1)
        }
        return result
    }

    /**
     * Segments days in a month into weekly chunks aligned to [weekStartDay].
     */
    fun buildWeeklyTotals(
        range: PeriodCalculator.DateRange,
        expenses: List<ExpenseWithCategory>,
        weekStartDay: WeekStartDay,
    ): List<WeekTotal> {
        val targetDayOfWeek = when (weekStartDay) {
            WeekStartDay.MONDAY -> DayOfWeek.MONDAY
            WeekStartDay.SUNDAY -> DayOfWeek.SUNDAY
        }

        val result = mutableListOf<WeekTotal>()
        var weekNumber = 1
        var chunkStart = range.start

        while (!chunkStart.isAfter(range.end)) {
            // Find end of this weekly chunk (either before next targetDayOfWeek or range.end)
            var chunkEnd = chunkStart
            while (chunkEnd.plusDays(1) <= range.end && chunkEnd.plusDays(1).dayOfWeek != targetDayOfWeek) {
                chunkEnd = chunkEnd.plusDays(1)
            }

            val weekExpenses = expenses.filter {
                !it.expense.occurredDate.isBefore(chunkStart) && !it.expense.occurredDate.isAfter(chunkEnd)
            }

            val weekTotal = weekExpenses.sumOf { it.expense.amountMinor }
            val weekNeed = weekExpenses.filter { it.expense.type == ExpenseType.NEED }.sumOf { it.expense.amountMinor }
            val weekWaste = weekExpenses.filter { it.expense.type == ExpenseType.WASTE }.sumOf { it.expense.amountMinor }

            result.add(
                WeekTotal(
                    weekNumber = weekNumber,
                    startDate = chunkStart,
                    endDate = chunkEnd,
                    totalMinor = weekTotal,
                    needMinor = weekNeed,
                    wasteMinor = weekWaste,
                )
            )

            weekNumber++
            chunkStart = chunkEnd.plusDays(1)
        }
        return result
    }

    /**
     * Calculates category spending shares sorted in descending order of spend.
     */
    fun buildCategoryShares(
        expenses: List<ExpenseWithCategory>,
        totalMinor: Long,
    ): List<CategoryShare> {
        if (expenses.isEmpty()) return emptyList()

        return expenses
            .groupBy { it.category }
            .map { (category, catExpenses) ->
                val catTotal = catExpenses.sumOf { it.expense.amountMinor }
                val catNeed = catExpenses.filter { it.expense.type == ExpenseType.NEED }.sumOf { it.expense.amountMinor }
                val catWaste = catExpenses.filter { it.expense.type == ExpenseType.WASTE }.sumOf { it.expense.amountMinor }
                val sharePct = if (totalMinor == 0L) 0.0 else (catTotal.toDouble() / totalMinor.toDouble()) * 100.0

                CategoryShare(
                    category = category,
                    totalMinor = catTotal,
                    needMinor = catNeed,
                    wasteMinor = catWaste,
                    sharePct = sharePct,
                    defaultType = category.defaultType,
                )
            }
            .sortedByDescending { it.totalMinor }
    }

    /**
     * Constructs a continuous list of [DayIncome] for each day in [range].
     */
    fun buildDailyIncomes(
        range: PeriodCalculator.DateRange,
        incomes: List<IncomeWithSource>,
    ): List<DayIncome> {
        val incomesByDate = incomes.groupBy { it.income.receivedDate }
        val result = mutableListOf<DayIncome>()

        var current = range.start
        while (!current.isAfter(range.end)) {
            val dayList = incomesByDate[current].orEmpty()
            val dayTotal = dayList.sumOf { it.income.amountMinor }

            result.add(
                DayIncome(
                    date = current,
                    totalMinor = dayTotal,
                )
            )
            current = current.plusDays(1)
        }
        return result
    }

    /**
     * Calculates income shares by source sorted in descending order of received amount.
     */
    fun buildIncomeSourceShares(
        incomes: List<IncomeWithSource>,
        totalIncomeMinor: Long,
    ): List<IncomeSourceShare> {
        if (incomes.isEmpty()) return emptyList()

        return incomes
            .groupBy { it.source }
            .map { (source, srcIncomes) ->
                val srcTotal = srcIncomes.sumOf { it.income.amountMinor }
                val sharePct = if (totalIncomeMinor == 0L) 0.0 else (srcTotal.toDouble() / totalIncomeMinor.toDouble()) * 100.0

                IncomeSourceShare(
                    source = source,
                    totalMinor = srcTotal,
                    sharePct = sharePct,
                )
            }
            .sortedByDescending { it.totalMinor }
    }

    /**
     * Calculates period-over-period comparison metrics for expenses and incomes.
     */
    fun buildPreviousComparison(
        currentPeriod: Period,
        previousExpenses: List<ExpenseWithCategory>,
        previousIncomes: List<IncomeWithSource> = emptyList(),
        currentTotalMinor: Long,
        currentWasteRatioPct: Double,
        currentIncomeMinor: Long = 0L,
        currentNetMinor: Long = 0L,
        currentSavingsRatePct: Double? = null,
    ): PreviousComparison {
        val prevPeriod = PeriodCalculator.getPreviousPeriod(currentPeriod)
        val prevRange = PeriodCalculator.getDateRange(prevPeriod)

        val filteredPrevExpenses = previousExpenses.filter { it.expense.occurredDate in prevRange }
        val prevTotal = filteredPrevExpenses.sumOf { it.expense.amountMinor }
        val prevNeed = filteredPrevExpenses.filter { it.expense.type == ExpenseType.NEED }.sumOf { it.expense.amountMinor }
        val prevWaste = filteredPrevExpenses.filter { it.expense.type == ExpenseType.WASTE }.sumOf { it.expense.amountMinor }

        val prevWasteRatio = if (prevTotal == 0L) {
            0.0
        } else {
            (prevWaste.toDouble() / prevTotal.toDouble()) * 100.0
        }

        val totalDeltaPct = if (prevTotal == 0L) {
            null
        } else {
            ((currentTotalMinor - prevTotal).toDouble() / prevTotal.toDouble()) * 100.0
        }

        val wasteRatioDeltaPts = currentWasteRatioPct - prevWasteRatio

        val filteredPrevIncomes = previousIncomes.filter { it.income.receivedDate in prevRange }
        val prevIncome = filteredPrevIncomes.sumOf { it.income.amountMinor }
        val prevNet = prevIncome - prevTotal
        val prevSavingsRatePct = if (prevIncome > 0L) {
            (prevNet.toDouble() / prevIncome.toDouble()) * 100.0
        } else {
            null
        }

        val incomeDeltaPct = if (prevIncome == 0L) {
            null
        } else {
            ((currentIncomeMinor - prevIncome).toDouble() / prevIncome.toDouble()) * 100.0
        }

        val netDeltaPct = if (prevNet == 0L) {
            null
        } else {
            ((currentNetMinor - prevNet).toDouble() / abs(prevNet).toDouble()) * 100.0
        }

        val savingsRateDeltaPts = if (currentSavingsRatePct != null && prevSavingsRatePct != null) {
            currentSavingsRatePct - prevSavingsRatePct
        } else {
            null
        }

        return PreviousComparison(
            previousTotalMinor = prevTotal,
            previousNeedMinor = prevNeed,
            previousWasteMinor = prevWaste,
            previousWasteRatioPct = prevWasteRatio,
            totalDeltaPct = totalDeltaPct,
            wasteRatioDeltaPts = wasteRatioDeltaPts,
            previousIncomeMinor = prevIncome,
            previousNetMinor = prevNet,
            previousSavingsRatePct = prevSavingsRatePct,
            incomeDeltaPct = incomeDeltaPct,
            netDeltaPct = netDeltaPct,
            savingsRateDeltaPts = savingsRateDeltaPts,
        )
    }

    /**
     * Calculates the Available Balance across all time (FR-071).
     * Formula: openingBalanceMinor + totalIncomeToDate - totalExpenseToDate
     */
    fun calculateAvailableBalance(
        openingBalanceMinor: Long,
        totalIncomeMinor: Long,
        totalExpenseMinor: Long,
    ): Long {
        return openingBalanceMinor + totalIncomeMinor - totalExpenseMinor
    }

    /**
     * Calculates the estimated days left of funds based on 14-day average daily spending (FR-072).
     * Formula: Available Balance ÷ average daily spend over the last 14 days.
     * Returns null when balance <= 0 or average daily spend <= 0.
     */
    fun calculateDaysLeftEstimate(
        availableBalanceMinor: Long,
        expensesLast14Days: List<ExpenseWithCategory>,
        today: LocalDate = LocalDate.now(),
    ): Int? {
        if (availableBalanceMinor <= 0L) return null
        val startDate = today.minusDays(13)
        val inRangeExpenses = expensesLast14Days.filter {
            !it.expense.occurredDate.isBefore(startDate) && !it.expense.occurredDate.isAfter(today)
        }
        val totalSpend = inRangeExpenses.sumOf { it.expense.amountMinor }
        val avgDailySpend = totalSpend / 14.0
        if (avgDailySpend <= 0.0) return null
        val days = (availableBalanceMinor.toDouble() / avgDailySpend).toInt()
        return if (days > 0) days else null
    }

    /**
     * Calculates the number of Clean Days (days with zero Waste spend)
     * among the elapsed days in [range].
     */
    fun calculateCleanDays(
        dailyTotals: List<DayTotal>,
        range: PeriodCalculator.DateRange,
        today: LocalDate,
    ): Int {
        if (today.isBefore(range.start)) return 0

        val effectiveEnd = if (today.isBefore(range.end)) today else range.end

        return dailyTotals
            .filter { !it.date.isAfter(effectiveEnd) && it.wasteMinor == 0L }
            .size
    }

    /**
     * Calculates the consecutive streak of Clean Days (days with 0 waste spend)
     * up to and including [upToDate].
     */
    fun calculateCleanDayStreak(
        dailyTotals: List<DayTotal>,
        upToDate: LocalDate = LocalDate.now(),
    ): Int {
        val sortedDays = dailyTotals
            .filter { !it.date.isAfter(upToDate) }
            .sortedByDescending { it.date }

        var streak = 0
        for (day in sortedDays) {
            if (day.wasteMinor == 0L) {
                streak++
            } else {
                break
            }
        }
        return streak
    }
}
