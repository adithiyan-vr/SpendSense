/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.work

import android.content.Context
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.supernovea.spendsense.core.notification.SpendSenseNotifier
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.GetPeriodSummaryUseCase
import com.supernovea.spendsense.domain.usecase.PeriodSummaryResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class WeeklySummaryWorkerTest {

    private val context = mockk<Context>(relaxed = true)
    private val workerParams = mockk<WorkerParameters>(relaxed = true)
    private val settingsRepository = mockk<SettingsRepository>()
    private val getPeriodSummaryUseCase = mockk<GetPeriodSummaryUseCase>()
    private val notifier = mockk<SpendSenseNotifier>(relaxed = true)

    @Test
    fun `doWork sends weekly summary when enabled and aligns to weekStartDay`() = runTest {
        val settings = UserSettings(
            weeklySummaryEnabled = true,
            weekStartDay = WeekStartDay.SUNDAY,
            currencySymbol = "₹",
        )
        coEvery { settingsRepository.getSettings() } returns settings

        val periodSlot = slot<Period.Week>()
        val mockSummary = mockk<PeriodSummary> {
            every { totalMinor } returns 500000L
            every { wasteRatioPct } returns 40.0
        }
        val mockResult = PeriodSummaryResult(
            summary = mockSummary,
            budgetStatus = null,
            insights = emptyList(),
        )

        every { getPeriodSummaryUseCase(capture(periodSlot), any()) } returns flowOf(mockResult)

        val worker = WeeklySummaryWorker(
            appContext = context,
            workerParams = workerParams,
            settingsRepository = settingsRepository,
            getPeriodSummaryUseCase = getPeriodSummaryUseCase,
            notifier = notifier,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertEquals(DayOfWeek.SUNDAY, periodSlot.captured.start.dayOfWeek)
        verify {
            notifier.sendWeeklySummary(
                totalSpent = any(),
                wasteRatioPct = 40,
                isPrivate = true,
            )
        }
    }

    @Test
    fun `doWork skips notification when weeklySummaryEnabled is false`() = runTest {
        val settings = UserSettings(weeklySummaryEnabled = false)
        coEvery { settingsRepository.getSettings() } returns settings

        val worker = WeeklySummaryWorker(
            appContext = context,
            workerParams = workerParams,
            settingsRepository = settingsRepository,
            getPeriodSummaryUseCase = getPeriodSummaryUseCase,
            notifier = notifier,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        verify(exactly = 0) { notifier.sendWeeklySummary(any(), any(), any()) }
    }
}
