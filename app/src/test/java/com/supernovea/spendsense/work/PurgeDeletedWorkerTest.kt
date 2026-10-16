/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.work

import android.content.Context
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.IncomeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PurgeDeletedWorkerTest {

    private val context = mockk<Context>(relaxed = true)
    private val workerParams = mockk<WorkerParameters>(relaxed = true)
    private val expenseRepository = mockk<ExpenseRepository>()
    private val incomeRepository = mockk<IncomeRepository>()

    @Test
    fun `doWork purges both expenses and incomes older than 30 days and returns success`() = runTest {
        coEvery { expenseRepository.purgeDeletedBefore(any()) } returns 3
        coEvery { incomeRepository.purgeDeletedBefore(any()) } returns 2

        val worker = PurgeDeletedWorker(
            appContext = context,
            workerParams = workerParams,
            expenseRepository = expenseRepository,
            incomeRepository = incomeRepository,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        coVerify { expenseRepository.purgeDeletedBefore(any()) }
        coVerify { incomeRepository.purgeDeletedBefore(any()) }
    }

    @Test
    fun `doWork returns retry when repository throws exception`() = runTest {
        coEvery { expenseRepository.purgeDeletedBefore(any()) } throws RuntimeException("DB error")

        val worker = PurgeDeletedWorker(
            appContext = context,
            workerParams = workerParams,
            expenseRepository = expenseRepository,
            incomeRepository = incomeRepository,
        )

        val result = worker.doWork()

        assertEquals(Result.retry(), result)
    }
}
