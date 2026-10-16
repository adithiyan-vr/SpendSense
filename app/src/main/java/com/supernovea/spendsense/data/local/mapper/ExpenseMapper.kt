/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.mapper

import com.supernovea.spendsense.data.local.entity.ExpenseEntity
import com.supernovea.spendsense.data.local.entity.ExpenseWithCategoryEntity
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.PaymentMethod
import java.time.Instant
import java.time.LocalDate

fun ExpenseEntity.toDomain(): Expense {
    val parsedType = try {
        ExpenseType.valueOf(type)
    } catch (_: IllegalArgumentException) {
        ExpenseType.WASTE
    }
    val parsedPayment = try {
        PaymentMethod.valueOf(paymentMethod)
    } catch (_: IllegalArgumentException) {
        PaymentMethod.OTHER
    }

    return Expense(
        id = id,
        amountMinor = amountMinor,
        type = parsedType,
        categoryId = categoryId,
        note = note,
        occurredAt = Instant.ofEpochMilli(occurredAt),
        occurredDate = LocalDate.ofEpochDay(occurredDate),
        paymentMethod = parsedPayment,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        deletedAt = deletedAt?.let { Instant.ofEpochMilli(it) },
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amountMinor = amountMinor,
        type = type.name,
        categoryId = categoryId,
        note = note,
        occurredAt = occurredAt.toEpochMilli(),
        occurredDate = occurredDate.toEpochDay(),
        paymentMethod = paymentMethod.name,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        deletedAt = deletedAt?.toEpochMilli(),
    )
}

fun ExpenseWithCategoryEntity.toDomain(): ExpenseWithCategory {
    return ExpenseWithCategory(
        expense = expense.toDomain(),
        category = category.toDomain(),
    )
}
