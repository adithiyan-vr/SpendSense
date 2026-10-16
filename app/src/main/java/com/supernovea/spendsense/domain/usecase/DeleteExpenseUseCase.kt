/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.repository.ExpenseRepository
import java.time.Instant
import javax.inject.Inject

/**
 * Use case to soft-delete an expense by setting its deletedAt timestamp.
 * This supports the "Swipe-to-delete with Undo" requirement in PRD §7.2.
 */
class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
) {

    suspend operator fun invoke(id: Long, deletedAt: Instant = Instant.now()) {
        expenseRepository.softDeleteExpense(id, deletedAt)
    }
}
