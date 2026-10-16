/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.mapper

import com.supernovea.spendsense.data.local.entity.BudgetEntity
import com.supernovea.spendsense.domain.model.Budget

fun BudgetEntity.toDomain(): Budget {
    return Budget(
        monthKey = monthKey,
        totalLimitMinor = totalLimitMinor,
        wasteLimitMinor = wasteLimitMinor,
    )
}

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        monthKey = monthKey,
        totalLimitMinor = totalLimitMinor,
        wasteLimitMinor = wasteLimitMinor,
    )
}
