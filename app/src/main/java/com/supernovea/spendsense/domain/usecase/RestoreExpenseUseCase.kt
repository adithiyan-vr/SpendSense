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
 * Use case to restore a soft-deleted expense (Undo deletion).
 */
class RestoreExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
) {

    suspend operator fun invoke(id: Long, timestamp: Instant = Instant.now()) {
        expenseRepository.restoreExpense(id, timestamp)
    }
}
