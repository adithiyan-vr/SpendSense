/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.mapper

import com.supernovea.spendsense.data.local.entity.CategoryEntity
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType

fun CategoryEntity.toDomain(): Category {
    val parsedType = try {
        ExpenseType.valueOf(defaultType)
    } catch (_: IllegalArgumentException) {
        ExpenseType.WASTE
    }
    return Category(
        id = id,
        defaultKey = defaultKey,
        groupKey = groupKey,
        name = name,
        iconKey = iconKey,
        colorHex = colorHex,
        defaultType = parsedType,
        isArchived = isArchived,
        isDefault = isDefault,
        sortOrder = sortOrder,
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        defaultKey = defaultKey,
        groupKey = groupKey,
        name = name,
        iconKey = iconKey,
        colorHex = colorHex,
        defaultType = defaultType.name,
        isArchived = isArchived,
        isDefault = isDefault,
        sortOrder = sortOrder,
    )
}
