/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.mapper

import com.supernovea.spendsense.data.local.entity.CategoryEntity
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryMapperTest {

    @Test
    fun `toDomain maps entity fields correctly`() {
        val entity = CategoryEntity(
            id = 10L,
            name = "Groceries",
            iconKey = "shopping_basket",
            colorHex = "#10B981",
            defaultType = "NEED",
            isArchived = false,
            isDefault = true,
            sortOrder = 2,
        )

        val domain = entity.toDomain()

        assertEquals(10L, domain.id)
        assertEquals("Groceries", domain.name)
        assertEquals("shopping_basket", domain.iconKey)
        assertEquals("#10B981", domain.colorHex)
        assertEquals(ExpenseType.NEED, domain.defaultType)
        assertEquals(false, domain.isArchived)
        assertEquals(true, domain.isDefault)
        assertEquals(2, domain.sortOrder)
    }

    @Test
    fun `toEntity maps domain fields correctly`() {
        val domain = Category(
            id = 5L,
            name = "Eating Out",
            iconKey = "local_dining",
            colorHex = "#EF4444",
            defaultType = ExpenseType.WASTE,
            isArchived = true,
            isDefault = false,
            sortOrder = 9,
        )

        val entity = domain.toEntity()

        assertEquals(5L, entity.id)
        assertEquals("Eating Out", entity.name)
        assertEquals("local_dining", entity.iconKey)
        assertEquals("#EF4444", entity.colorHex)
        assertEquals("WASTE", entity.defaultType)
        assertEquals(true, entity.isArchived)
        assertEquals(false, entity.isDefault)
        assertEquals(9, entity.sortOrder)
    }
}
