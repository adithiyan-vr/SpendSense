/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.mapper

import com.supernovea.spendsense.data.local.entity.CategoryEntity
import com.supernovea.spendsense.data.local.entity.ExpenseEntity
import com.supernovea.spendsense.data.local.entity.ExpenseWithCategoryEntity
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class ExpenseMapperTest {

    @Test
    fun `toDomain converts entity timestamps and enums correctly`() {
        val entity = ExpenseEntity(
            id = 101L,
            amountMinor = 25000L,
            type = "NEED",
            categoryId = 2L,
            note = "Campus mess fees",
            occurredAt = 1770000000000L,
            occurredDate = 20500L,
            paymentMethod = "UPI",
            createdAt = 1770000000000L,
            updatedAt = 1770000000000L,
            deletedAt = null,
        )

        val domain = entity.toDomain()

        assertEquals(101L, domain.id)
        assertEquals(25000L, domain.amountMinor)
        assertEquals(ExpenseType.NEED, domain.type)
        assertEquals(2L, domain.categoryId)
        assertEquals("Campus mess fees", domain.note)
        assertEquals(Instant.ofEpochMilli(1770000000000L), domain.occurredAt)
        assertEquals(LocalDate.ofEpochDay(20500L), domain.occurredDate)
        assertEquals(PaymentMethod.UPI, domain.paymentMethod)
        assertNull(domain.deletedAt)
    }

    @Test
    fun `toEntity converts domain model fields correctly`() {
        val now = Instant.ofEpochMilli(1770000000000L)
        val today = LocalDate.ofEpochDay(20500L)

        val domain = Expense(
            id = 202L,
            amountMinor = 1500L,
            type = ExpenseType.WASTE,
            categoryId = 8L,
            note = "Evening snacks",
            occurredAt = now,
            occurredDate = today,
            paymentMethod = PaymentMethod.CASH,
            createdAt = now,
            updatedAt = now,
            deletedAt = null,
        )

        val entity = domain.toEntity()

        assertEquals(202L, entity.id)
        assertEquals(1500L, entity.amountMinor)
        assertEquals("WASTE", entity.type)
        assertEquals(8L, entity.categoryId)
        assertEquals("Evening snacks", entity.note)
        assertEquals(1770000000000L, entity.occurredAt)
        assertEquals(20500L, entity.occurredDate)
        assertEquals("CASH", entity.paymentMethod)
    }

    @Test
    fun `ExpenseWithCategoryEntity toDomain maps both expense and category`() {
        val categoryEntity = CategoryEntity(
            id = 3L,
            name = "Transport",
            iconKey = "directions_bus",
            colorHex = "#3B82F6",
            defaultType = "NEED",
            isDefault = true,
        )
        val expenseEntity = ExpenseEntity(
            id = 1L,
            amountMinor = 5000L,
            type = "NEED",
            categoryId = 3L,
            note = "Bus pass",
            occurredAt = 1770000000000L,
            occurredDate = 20500L,
            paymentMethod = "CARD",
            createdAt = 1770000000000L,
            updatedAt = 1770000000000L,
        )

        val relation = ExpenseWithCategoryEntity(
            expense = expenseEntity,
            category = categoryEntity,
        )

        val domain = relation.toDomain()

        assertEquals(1L, domain.expense.id)
        assertEquals(3L, domain.category.id)
        assertEquals("Transport", domain.category.name)
    }
}
