/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.mapper

import com.supernovea.spendsense.data.local.entity.BudgetEntity
import com.supernovea.spendsense.domain.model.Budget
import org.junit.Assert.assertEquals
import org.junit.Test

class BudgetMapperTest {

    @Test
    fun `toDomain converts BudgetEntity correctly`() {
        val entity = BudgetEntity(
            monthKey = "2026-10",
            totalLimitMinor = 1500000L,
            wasteLimitMinor = 300000L,
        )

        val domain = entity.toDomain()

        assertEquals("2026-10", domain.monthKey)
        assertEquals(1500000L, domain.totalLimitMinor)
        assertEquals(300000L, domain.wasteLimitMinor)
    }

    @Test
    fun `toEntity converts Budget domain model correctly`() {
        val domain = Budget(
            monthKey = "2026-11",
            totalLimitMinor = 2000000L,
            wasteLimitMinor = null,
        )

        val entity = domain.toEntity()

        assertEquals("2026-11", entity.monthKey)
        assertEquals(2000000L, entity.totalLimitMinor)
        assertEquals(null, entity.wasteLimitMinor)
    }
}
