/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.repository

import com.supernovea.spendsense.data.local.dao.IncomeDao
import com.supernovea.spendsense.data.local.entity.IncomeEntity
import com.supernovea.spendsense.data.local.entity.IncomeSourceEntity
import com.supernovea.spendsense.data.local.entity.IncomeWithSourceEntity
import com.supernovea.spendsense.domain.model.Income
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class IncomeRepositoryTest {

    private val incomeDao = mockk<IncomeDao>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: IncomeRepositoryImpl

    @Before
    fun setup() {
        repository = IncomeRepositoryImpl(incomeDao, testDispatcher)
    }

    @Test
    fun `observeBetween queries dao with correct epoch days and maps entities`() = runTest(testDispatcher) {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 7)

        val sampleRelation = IncomeWithSourceEntity(
            income = IncomeEntity(
                id = 1L,
                amountMinor = 500000L,
                sourceId = 1L,
                note = "Allowance",
                receivedAt = 1770000000000L,
                receivedDate = start.toEpochDay(),
                createdAt = 1770000000000L,
                updatedAt = 1770000000000L,
            ),
            source = IncomeSourceEntity(
                id = 1L,
                defaultKey = "pocket_money",
                name = "Pocket Money",
                iconKey = "savings",
                colorHex = "#3B82F6",
            ),
        )

        every {
            incomeDao.observeBetween(start.toEpochDay(), end.toEpochDay())
        } returns flowOf(listOf(sampleRelation))

        val result = repository.observeBetween(start, end).first()

        assertEquals(1, result.size)
        assertEquals(500000L, result[0].income.amountMinor)
        assertEquals("Allowance", result[0].income.note)
        assertEquals("Pocket Money", result[0].source.name)
    }

    @Test
    fun `observeTotalBetween delegates to dao`() = runTest(testDispatcher) {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)

        every {
            incomeDao.observeTotalBetween(start.toEpochDay(), end.toEpochDay())
        } returns flowOf(750000L)

        val result = repository.observeTotalBetween(start, end).first()
        assertEquals(750000L, result)
    }

    @Test
    fun `insertIncome and updateIncome map to entity and call dao`() = runTest(testDispatcher) {
        coEvery { incomeDao.insert(any()) } returns 10L
        coEvery { incomeDao.update(any()) } returns Unit

        val income = Income(
            id = 0L,
            amountMinor = 300000L,
            sourceId = 2L,
            note = "Stipend",
            receivedAt = Instant.now(),
            receivedDate = LocalDate.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        )

        val id = repository.insertIncome(income)
        assertEquals(10L, id)

        repository.updateIncome(income.copy(id = 10L, amountMinor = 350000L))
        coVerify { incomeDao.update(any()) }
    }

    @Test
    fun `softDelete and restore call dao with timestamp`() = runTest(testDispatcher) {
        coEvery { incomeDao.softDelete(any(), any()) } returns Unit
        coEvery { incomeDao.restore(any(), any()) } returns Unit

        val now = Instant.now()
        repository.softDeleteIncome(7L, now)
        coVerify { incomeDao.softDelete(7L, now.toEpochMilli()) }

        repository.restoreIncome(7L, now)
        coVerify { incomeDao.restore(7L, now.toEpochMilli()) }
    }
}
