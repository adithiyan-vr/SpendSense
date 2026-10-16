/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Income
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class IncomeUseCasesTest {

    private val incomeRepository = mockk<IncomeRepository>()
    private val incomeSourceRepository = mockk<IncomeSourceRepository>()

    private val sampleSource = IncomeSource(
        id = 1L,
        name = "Pocket Money",
        iconKey = "savings",
        colorHex = "#3B82F6",
        isArchived = false,
        isDefault = true,
        sortOrder = 0,
    )

    private suspend inline fun <reified T : Throwable> assertThrowsSuspend(crossinline block: suspend () -> Unit) {
        var caught = false
        try {
            block()
        } catch (e: Throwable) {
            if (e is T) {
                caught = true
            } else {
                throw AssertionError("Expected ${T::class.simpleName} but got ${e::class.simpleName}", e)
            }
        }
        if (!caught) {
            throw AssertionError("Expected ${T::class.simpleName} was not thrown")
        }
    }

    @Test
    fun `AddIncomeUseCase validates positive amount`() = runTest {
        val useCase = AddIncomeUseCase(incomeRepository, incomeSourceRepository)

        assertThrowsSuspend<IllegalArgumentException> {
            useCase(amountMinor = 0L, sourceId = 1L)
        }

        assertThrowsSuspend<IllegalArgumentException> {
            useCase(amountMinor = -100L, sourceId = 1L)
        }
    }

    @Test
    fun `AddIncomeUseCase validates source exists`() = runTest {
        val useCase = AddIncomeUseCase(incomeRepository, incomeSourceRepository)
        coEvery { incomeSourceRepository.getById(99L) } returns null

        assertThrowsSuspend<IllegalArgumentException> {
            useCase(amountMinor = 500000L, sourceId = 99L)
        }
    }

    @Test
    fun `AddIncomeUseCase derives local date and inserts income`() = runTest {
        val useCase = AddIncomeUseCase(incomeRepository, incomeSourceRepository)
        coEvery { incomeSourceRepository.getById(1L) } returns sampleSource

        val slot = slot<Income>()
        coEvery { incomeRepository.insertIncome(capture(slot)) } returns 42L

        val instant = Instant.parse("2026-10-14T10:00:00Z")
        val zone = ZoneId.of("Asia/Kolkata")
        val result = useCase(
            amountMinor = 500000L,
            sourceId = 1L,
            note = "Monthly pocket money",
            receivedAt = instant,
            zoneId = zone,
        )

        assertEquals(42L, result)
        assertEquals(500000L, slot.captured.amountMinor)
        assertEquals(1L, slot.captured.sourceId)
        assertEquals("Monthly pocket money", slot.captured.note)
        assertEquals(LocalDate.of(2026, 10, 14), slot.captured.receivedDate)
    }

    @Test
    fun `UpdateIncomeUseCase updates existing income`() = runTest {
        val useCase = UpdateIncomeUseCase(incomeRepository, incomeSourceRepository)
        val now = Instant.now()
        val existing = IncomeWithSource(
            income = Income(
                id = 10L,
                amountMinor = 200000L,
                sourceId = 1L,
                note = "Old note",
                receivedAt = now,
                receivedDate = LocalDate.now(),
                createdAt = now,
                updatedAt = now,
            ),
            source = sampleSource,
        )

        coEvery { incomeRepository.getById(10L) } returns existing
        coEvery { incomeSourceRepository.getById(1L) } returns sampleSource

        val slot = slot<Income>()
        coEvery { incomeRepository.updateIncome(capture(slot)) } returns Unit

        useCase(
            id = 10L,
            amountMinor = 350000L,
            sourceId = 1L,
            note = "Updated note",
            receivedAt = now,
        )

        assertEquals(350000L, slot.captured.amountMinor)
        assertEquals("Updated note", slot.captured.note)
        coVerify { incomeRepository.updateIncome(any()) }
    }

    @Test
    fun `DeleteIncomeUseCase and RestoreIncomeUseCase call repository`() = runTest {
        val deleteUseCase = DeleteIncomeUseCase(incomeRepository)
        val restoreUseCase = RestoreIncomeUseCase(incomeRepository)

        coEvery { incomeRepository.softDeleteIncome(any(), any()) } returns Unit
        coEvery { incomeRepository.restoreIncome(any(), any()) } returns Unit

        val timestamp = Instant.now()
        deleteUseCase(5L, timestamp)
        coVerify { incomeRepository.softDeleteIncome(5L, timestamp) }

        restoreUseCase(5L, timestamp)
        coVerify { incomeRepository.restoreIncome(5L, timestamp) }
    }

    @Test
    fun `GetIncomesUseCase queries repository`() = runTest {
        val useCase = GetIncomesUseCase(incomeRepository)
        val list = listOf(
            IncomeWithSource(
                income = Income(
                    id = 1L,
                    amountMinor = 100000L,
                    sourceId = 1L,
                    note = null,
                    receivedAt = Instant.now(),
                    receivedDate = LocalDate.now(),
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                ),
                source = sampleSource,
            )
        )

        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)
        every { incomeRepository.observeBetween(start, end) } returns flowOf(list)
        every { incomeRepository.observeTotalBetween(start, end) } returns flowOf(100000L)
        every { incomeRepository.observeTotalAllTime() } returns flowOf(500000L)

        val retrieved = useCase.observeBetween(start, end).first()
        assertEquals(1, retrieved.size)
        assertEquals(100000L, useCase.observeTotalBetween(start, end).first())
        assertEquals(500000L, useCase.observeTotalAllTime().first())
    }

    @Test
    fun `ManageIncomeSourcesUseCase creates source and toggles archive`() = runTest {
        val useCase = ManageIncomeSourcesUseCase(incomeSourceRepository)

        coEvery { incomeSourceRepository.getByName("Freelance") } returns null
        coEvery { incomeSourceRepository.count() } returns 5
        coEvery { incomeSourceRepository.upsertIncomeSource(any()) } returns 6L
        coEvery { incomeSourceRepository.setArchived(6L, true) } returns Unit

        val id = useCase.createSource("Freelance")
        assertEquals(6L, id)

        useCase.setArchived(6L, true)
        coVerify { incomeSourceRepository.setArchived(6L, true) }
    }
}
