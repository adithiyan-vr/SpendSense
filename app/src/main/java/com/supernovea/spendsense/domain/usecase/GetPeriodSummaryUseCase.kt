/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.analytics.AnalyticsEngine
import com.supernovea.spendsense.domain.analytics.InsightGenerator
import com.supernovea.spendsense.domain.analytics.PeriodCalculator
import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.BudgetStatus
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.model.Insight
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class PeriodSummaryResult(
    val summary: PeriodSummary,
    val insights: List<Insight>,
    val budgetStatus: BudgetStatus?,
)

/**
 * Use case to compute reactive analytical summaries and insights for a [Period].
 * Reactively recalculates whenever underlying expenses, incomes, budgets, or user settings change.
 */
class GetPeriodSummaryUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val budgetRepository: BudgetRepository,
    private val settingsRepository: SettingsRepository,
) {

    operator fun invoke(
        period: Period,
        today: LocalDate = LocalDate.now(),
    ): Flow<PeriodSummaryResult> {
        val currentRange = PeriodCalculator.getDateRange(period)
        val prevPeriod = PeriodCalculator.getPreviousPeriod(period)
        val prevRange = PeriodCalculator.getDateRange(prevPeriod)

        val monthKey = when (period) {
            is Period.Month -> period.ym.toString()
            is Period.Week -> YearMonth.from(currentRange.start).toString()
            is Period.Custom -> YearMonth.from(currentRange.start).toString()
        }

        val currentExpensesFlow = expenseRepository.observeBetween(
            currentRange.start,
            currentRange.end,
        )
        val prevExpensesFlow = expenseRepository.observeBetween(
            prevRange.start,
            prevRange.end,
        )
        val currentIncomesFlow = incomeRepository.observeBetween(
            currentRange.start,
            currentRange.end,
        )
        val prevIncomesFlow = incomeRepository.observeBetween(
            prevRange.start,
            prevRange.end,
        )
        val settingsFlow = settingsRepository.observeSettings()
        val budgetFlow = budgetRepository.observeByMonth(monthKey)

        val expensesPairFlow = combine(currentExpensesFlow, prevExpensesFlow) { current, prev -> current to prev }
        val incomesPairFlow = combine(currentIncomesFlow, prevIncomesFlow) { current, prev -> current to prev }

        return combine(
            expensesPairFlow,
            incomesPairFlow,
            settingsFlow,
            budgetFlow,
        ) { (currentExpenses, prevExpenses), (currentIncomes, prevIncomes), settings: UserSettings, budget: Budget? ->
            val summary = AnalyticsEngine.summarize(
                expenses = currentExpenses,
                incomes = currentIncomes,
                previousExpenses = prevExpenses,
                previousIncomes = prevIncomes,
                period = period,
                today = today,
                weekStartDay = settings.weekStartDay,
            )

            val insights = InsightGenerator.generate(
                summary = summary,
                budget = budget,
                today = today,
                userProfile = settings.userProfile,
            )

            val budgetStatus = if (budget != null && period is Period.Month) {
                val totalSpent = currentExpenses.sumOf { it.expense.amountMinor }
                val wasteSpent = currentExpenses
                    .filter { it.expense.type == ExpenseType.WASTE }
                    .sumOf { it.expense.amountMinor }
                val totalProgress = (totalSpent.toDouble() / budget.totalLimitMinor.toDouble()) * 100.0
                val wasteProgress = budget.wasteLimitMinor?.let { limit ->
                    if (limit > 0L) (wasteSpent.toDouble() / limit.toDouble()) * 100.0 else 0.0
                }
                val isOverTotal = totalSpent >= budget.totalLimitMinor
                val isOverWaste = budget.wasteLimitMinor != null && wasteSpent >= budget.wasteLimitMinor
                val daysRemaining = if (today.year == period.ym.year && today.month == period.ym.month) {
                    maxOf(0, period.ym.lengthOfMonth() - today.dayOfMonth)
                } else {
                    0
                }

                BudgetStatus(
                    budget = budget,
                    totalSpentMinor = totalSpent,
                    wasteSpentMinor = wasteSpent,
                    totalProgressPct = totalProgress,
                    wasteProgressPct = wasteProgress,
                    isOverTotal = isOverTotal,
                    isOverWaste = isOverWaste,
                    daysRemainingInMonth = daysRemaining,
                )
            } else {
                null
            }

            PeriodSummaryResult(
                summary = summary,
                insights = insights,
                budgetStatus = budgetStatus,
            )
        }
    }
}
