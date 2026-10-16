/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.supernovea.spendsense.MainActivity
import com.supernovea.spendsense.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpendSenseNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        const val CHANNEL_REMINDERS = "reminders"
        const val CHANNEL_SUMMARIES = "summaries"

        const val ID_DAILY_REMINDER = 1001
        const val ID_WEEKLY_SUMMARY = 1002
        const val ID_MONTHLY_SUMMARY = 1003
        const val ID_BACKUP_REMINDER = 1004
    }

    init {
        createNotificationChannels()
    }

    fun createNotificationChannels() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val reminderChannel = NotificationChannel(
            CHANNEL_REMINDERS,
            context.getString(R.string.notif_channel_reminders_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notif_channel_reminders_desc)
            enableVibration(true)
        }

        val summaryChannel = NotificationChannel(
            CHANNEL_SUMMARIES,
            context.getString(R.string.notif_channel_summaries_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notif_channel_summaries_desc)
            enableVibration(true)
        }

        notificationManager.createNotificationChannels(listOf(reminderChannel, summaryChannel))
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun createLaunchIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    @android.annotation.SuppressLint("MissingPermission")
    private fun notifySafely(id: Int, notification: android.app.Notification) {
        if (!hasNotificationPermission()) return
        NotificationManagerCompat.from(context).notify(id, notification)
    }

    fun sendDailyReminder() {
        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_stat_spendsense)
            .setContentTitle(context.getString(R.string.notif_daily_reminder_title))
            .setContentText(context.getString(R.string.notif_daily_reminder_body))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notif_daily_reminder_body)))
            .setContentIntent(createLaunchIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notifySafely(ID_DAILY_REMINDER, notification)
    }

    fun sendWeeklySummary(totalSpent: String, wasteRatioPct: Int, isPrivate: Boolean = true) {
        val publicNotification = NotificationCompat.Builder(context, CHANNEL_SUMMARIES)
            .setSmallIcon(R.drawable.ic_stat_spendsense)
            .setContentTitle(context.getString(R.string.notif_weekly_summary_title))
            .setContentText(context.getString(R.string.notif_weekly_summary_public_body))
            .setContentIntent(createLaunchIntent())
            .setAutoCancel(true)
            .build()

        val body = context.getString(R.string.notif_weekly_summary_body, totalSpent, wasteRatioPct)
        val builder = NotificationCompat.Builder(context, CHANNEL_SUMMARIES)
            .setSmallIcon(R.drawable.ic_stat_spendsense)
            .setContentTitle(context.getString(R.string.notif_weekly_summary_title))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(createLaunchIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (isPrivate) {
            builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            builder.setPublicVersion(publicNotification)
        }

        notifySafely(ID_WEEKLY_SUMMARY, builder.build())
    }

    fun sendMonthlySummary(totalSpent: String, cleanDays: Int, isPrivate: Boolean = true) {
        val publicNotification = NotificationCompat.Builder(context, CHANNEL_SUMMARIES)
            .setSmallIcon(R.drawable.ic_stat_spendsense)
            .setContentTitle(context.getString(R.string.notif_monthly_summary_title))
            .setContentText(context.getString(R.string.notif_monthly_summary_public_body))
            .setContentIntent(createLaunchIntent())
            .setAutoCancel(true)
            .build()

        val body = context.getString(R.string.notif_monthly_summary_body, totalSpent, cleanDays)
        val builder = NotificationCompat.Builder(context, CHANNEL_SUMMARIES)
            .setSmallIcon(R.drawable.ic_stat_spendsense)
            .setContentTitle(context.getString(R.string.notif_monthly_summary_title))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(createLaunchIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (isPrivate) {
            builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            builder.setPublicVersion(publicNotification)
        }

        notifySafely(ID_MONTHLY_SUMMARY, builder.build())
    }

    fun sendBackupReminder() {
        val notification = NotificationCompat.Builder(context, CHANNEL_SUMMARIES)
            .setSmallIcon(R.drawable.ic_stat_spendsense)
            .setContentTitle(context.getString(R.string.notif_backup_reminder_title))
            .setContentText(context.getString(R.string.notif_backup_reminder_body))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notif_backup_reminder_body)))
            .setContentIntent(createLaunchIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notifySafely(ID_BACKUP_REMINDER, notification)
    }
}
