/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.supernovea.spendsense.core.notification.SpendSenseNotifier
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.analytics.PeriodCalculator
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.GetPeriodSummaryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@HiltWorker
class WeeklySummaryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val getPeriodSummaryUseCase: GetPeriodSummaryUseCase,
    private val notifier: SpendSenseNotifier,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val settings = settingsRepository.getSettings()
            if (settings.weeklySummaryEnabled) {
                val today = LocalDate.now()
                val weekPeriod = PeriodCalculator.getWeekPeriod(today, settings.weekStartDay)
                val result = getPeriodSummaryUseCase(weekPeriod, today).first()
                val summary = result.summary
                val formattedTotal = Money.format(summary.totalMinor, settings.currencySymbol)
                notifier.sendWeeklySummary(
                    totalSpent = formattedTotal,
                    wasteRatioPct = summary.wasteRatioPct.toInt(),
                    isPrivate = settings.notificationPrivacyEnabled,
                )
            }
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }
}
