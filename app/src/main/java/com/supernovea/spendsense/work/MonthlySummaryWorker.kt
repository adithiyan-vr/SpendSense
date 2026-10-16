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
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.GetPeriodSummaryUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.YearMonth

@HiltWorker
class MonthlySummaryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val getPeriodSummaryUseCase: GetPeriodSummaryUseCase,
    private val notifier: SpendSenseNotifier,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val settings = settingsRepository.getSettings()

            if (settings.monthlySummaryEnabled) {
                val previousMonth = YearMonth.now().minusMonths(1)
                val referenceDate = previousMonth.atEndOfMonth()
                val result = getPeriodSummaryUseCase(Period.Month(previousMonth), referenceDate).first()
                val summary = result.summary
                val formattedTotal = Money.format(summary.totalMinor, settings.currencySymbol)

                // Clean days: days in month where wasteMinor == 0L
                val cleanDays = summary.dailyTotals.count { it.wasteMinor == 0L && it.totalMinor > 0L }

                notifier.sendMonthlySummary(
                    totalSpent = formattedTotal,
                    cleanDays = cleanDays,
                    isPrivate = settings.notificationPrivacyEnabled,
                )
            }

            // Check if 30+ days have passed since last backup
            val lastBackup = settings.lastBackupEpochMillis
            val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000L
            if (lastBackup == null || (System.currentTimeMillis() - lastBackup) > thirtyDaysMillis) {
                notifier.sendBackupReminder()
            }

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }
}
