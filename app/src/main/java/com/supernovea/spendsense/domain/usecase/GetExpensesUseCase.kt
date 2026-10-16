/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to observe expenses for history, home, and filter screens.
 */
class GetExpensesUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
) {

    /**
     * Observes all active expenses sorted by occurredAt descending.
     */
    fun getAll(): Flow<List<ExpenseWithCategory>> {
        return expenseRepository.observeAll()
    }

    /**
     * Observes expenses occurring between [startDate] and [endDate] (inclusive).
     */
    fun getBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<ExpenseWithCategory>> {
        return expenseRepository.observeBetween(startDate, endDate)
    }

    /**
     * Observes expenses of a specific [type] between [startDate] and [endDate].
     */
    fun getByTypeBetween(
        type: ExpenseType,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<ExpenseWithCategory>> {
        return expenseRepository.observeBetween(startDate, endDate)
            .map { list -> list.filter { it.expense.type == type } }
    }

    /**
     * Observes expenses for a specific [categoryId] between [startDate] and [endDate].
     */
    fun getByCategoryBetween(
        categoryId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<ExpenseWithCategory>> {
        return expenseRepository.observeBetween(startDate, endDate)
            .map { list -> list.filter { it.expense.categoryId == categoryId } }
    }

    /**
     * Observes an expense with its category by [id].
     */
    fun getById(id: Long): Flow<ExpenseWithCategory?> {
        return expenseRepository.observeById(id)
    }
}
