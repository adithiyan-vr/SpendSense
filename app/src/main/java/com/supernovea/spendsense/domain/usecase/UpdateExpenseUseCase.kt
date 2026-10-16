/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

/**
 * Use case to update an existing expense entry.
 * Validates amount > 0 and refreshes [occurredDate] and [updatedAt].
 */
class UpdateExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
) {

    suspend operator fun invoke(
        expense: Expense,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ) {
        require(expense.amountMinor > 0L) { "Expense amount must be greater than zero." }

        val updatedDate = expense.occurredAt.atZone(zoneId).toLocalDate()
        val updatedExpense = expense.copy(
            occurredDate = updatedDate,
            note = expense.note?.trim()?.ifEmpty { null },
            updatedAt = Instant.now(),
        )

        expenseRepository.updateExpense(updatedExpense)
    }
}
