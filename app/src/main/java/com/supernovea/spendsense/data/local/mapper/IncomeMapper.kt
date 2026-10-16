/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.mapper

import com.supernovea.spendsense.data.local.entity.IncomeEntity
import com.supernovea.spendsense.data.local.entity.IncomeWithSourceEntity
import com.supernovea.spendsense.domain.model.Income
import com.supernovea.spendsense.domain.model.IncomeWithSource
import java.time.Instant
import java.time.LocalDate

fun IncomeEntity.toDomain(): Income {
    return Income(
        id = id,
        amountMinor = amountMinor,
        sourceId = sourceId,
        note = note,
        receivedAt = Instant.ofEpochMilli(receivedAt),
        receivedDate = LocalDate.ofEpochDay(receivedDate),
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        deletedAt = deletedAt?.let { Instant.ofEpochMilli(it) },
        recurringRuleId = recurringRuleId,
    )
}

fun Income.toEntity(): IncomeEntity {
    return IncomeEntity(
        id = id,
        amountMinor = amountMinor,
        sourceId = sourceId,
        note = note,
        receivedAt = receivedAt.toEpochMilli(),
        receivedDate = receivedDate.toEpochDay(),
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        deletedAt = deletedAt?.toEpochMilli(),
        recurringRuleId = recurringRuleId,
    )
}

fun IncomeWithSourceEntity.toDomain(): IncomeWithSource {
    return IncomeWithSource(
        income = income.toDomain(),
        source = source.toDomain(),
    )
}
