/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.supernovea.spendsense.domain.model.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val workManager: WorkManager by lazy { WorkManager.getInstance(context) }

    companion object {
        const val UNIQUE_DAILY_REMINDER = "work_daily_reminder"
        const val UNIQUE_WEEKLY_SUMMARY = "work_weekly_summary"
        const val UNIQUE_MONTHLY_SUMMARY = "work_monthly_summary"
        const val UNIQUE_PURGE_DELETED = "work_purge_deleted"
    }

    fun scheduleAll(settings: UserSettings) {
        schedulePurgeDeleted()

        if (settings.reminderEnabled) {
            scheduleDailyReminder(settings.reminderTimeMinutes)
        } else {
            cancelDailyReminder()
        }

        if (settings.weeklySummaryEnabled) {
            scheduleWeeklySummary()
        } else {
            workManager.cancelUniqueWork(UNIQUE_WEEKLY_SUMMARY)
        }

        if (settings.monthlySummaryEnabled) {
            scheduleMonthlySummary()
        } else {
            workManager.cancelUniqueWork(UNIQUE_MONTHLY_SUMMARY)
        }
    }

    fun schedulePurgeDeleted() {
        val request = PeriodicWorkRequestBuilder<PurgeDeletedWorker>(24, TimeUnit.HOURS)
            .setConstraints(Constraints.NONE)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_PURGE_DELETED,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleDailyReminder(reminderMinutes: Int) {
        val targetHour = reminderMinutes / 60
        val targetMinute = reminderMinutes % 60
        val targetTime = LocalTime.of(targetHour, targetMinute)

        val now = LocalDateTime.now()
        var nextRun = now.with(targetTime)
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1)
        }

        val initialDelayMillis = Duration.between(now, nextRun).toMillis()

        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_DAILY_REMINDER,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelDailyReminder() {
        workManager.cancelUniqueWork(UNIQUE_DAILY_REMINDER)
    }

    fun scheduleWeeklySummary() {
        val request = PeriodicWorkRequestBuilder<WeeklySummaryWorker>(7, TimeUnit.DAYS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WEEKLY_SUMMARY,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleMonthlySummary() {
        // Run daily check to see if it is the 1st of the month
        val request = PeriodicWorkRequestBuilder<MonthlySummaryWorker>(24, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_MONTHLY_SUMMARY,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
