/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.model.BudgetStatus
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * Use case to observe and update monthly budget status.
 */
class GetBudgetStatusUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
) {

    operator fun invoke(
        month: YearMonth,
        today: LocalDate = LocalDate.now(),
    ): Flow<BudgetStatus?> {
        val monthKey = month.toString()
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()

        val budgetFlow = budgetRepository.observeByMonth(monthKey)
        val expensesFlow = expenseRepository.observeBetween(startDate, endDate)

        return combine(budgetFlow, expensesFlow) { budget, expenses ->
            if (budget == null) {
                null
            } else {
                val totalSpent = expenses.sumOf { it.expense.amountMinor }
                val wasteSpent = expenses
                    .filter { it.expense.type == ExpenseType.WASTE }
                    .sumOf { it.expense.amountMinor }

                val totalProgress = (totalSpent.toDouble() / budget.totalLimitMinor.toDouble()) * 100.0
                val wasteProgress = budget.wasteLimitMinor?.let { limit ->
                    if (limit > 0L) (wasteSpent.toDouble() / limit.toDouble()) * 100.0 else 0.0
                }

                val isOverTotal = totalSpent >= budget.totalLimitMinor
                val isOverWaste = budget.wasteLimitMinor != null && wasteSpent >= budget.wasteLimitMinor

                val daysRemaining = if (today.year == month.year && today.month == month.month) {
                    maxOf(0, month.lengthOfMonth() - today.dayOfMonth)
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
            }
        }
    }

    suspend fun setBudget(budget: Budget) {
        require(budget.totalLimitMinor > 0L) { "Total budget limit must be greater than zero." }
        budget.wasteLimitMinor?.let { wasteLimit ->
            require(wasteLimit >= 0L) { "Waste budget limit cannot be negative." }
            require(wasteLimit <= budget.totalLimitMinor) { "Waste limit cannot exceed total budget limit." }
        }
        budgetRepository.setBudget(budget)
    }
}
