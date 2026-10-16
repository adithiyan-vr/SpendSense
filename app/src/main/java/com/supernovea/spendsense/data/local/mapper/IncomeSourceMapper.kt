/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.mapper

import com.supernovea.spendsense.data.local.entity.IncomeSourceEntity
import com.supernovea.spendsense.domain.model.IncomeSource

fun IncomeSourceEntity.toDomain(): IncomeSource {
    return IncomeSource(
        id = id,
        defaultKey = defaultKey,
        name = name,
        iconKey = iconKey,
        colorHex = colorHex,
        isArchived = isArchived,
        isDefault = isDefault,
        sortOrder = sortOrder,
    )
}

fun IncomeSource.toEntity(): IncomeSourceEntity {
    return IncomeSourceEntity(
        id = id,
        defaultKey = defaultKey,
        name = name,
        iconKey = iconKey,
        colorHex = colorHex,
        isArchived = isArchived,
        isDefault = isDefault,
        sortOrder = sortOrder,
    )
}
