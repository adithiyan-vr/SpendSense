/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.repository.CategoryRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

/**
 * Use case to validate and persist a new expense entry.
 * Derives local [occurredDate] from [occurredAt] and enforces amount > 0.
 */
class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
) {

    suspend operator fun invoke(
        amountMinor: Long,
        type: ExpenseType,
        categoryId: Long,
        note: String? = null,
        occurredAt: Instant = Instant.now(),
        paymentMethod: PaymentMethod = PaymentMethod.OTHER,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Long {
        require(amountMinor > 0L) { "Expense amount must be greater than zero." }

        val category = categoryRepository.getById(categoryId)
        requireNotNull(category) { "Category with ID $categoryId does not exist." }

        val now = Instant.now()
        val occurredDate = occurredAt.atZone(zoneId).toLocalDate()

        val expense = Expense(
            id = 0L,
            amountMinor = amountMinor,
            type = type,
            categoryId = categoryId,
            note = note?.trim()?.ifEmpty { null },
            occurredAt = occurredAt,
            occurredDate = occurredDate,
            paymentMethod = paymentMethod,
            createdAt = now,
            updatedAt = now,
            deletedAt = null,
        )

        return expenseRepository.insertExpense(expense)
    }
}
