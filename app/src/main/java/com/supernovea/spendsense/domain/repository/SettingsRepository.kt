/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.repository

import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.model.WeekStartDay
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun getSettings(): UserSettings
    suspend fun setCurrencySymbol(symbol: String)
    suspend fun setWeekStartDay(day: WeekStartDay)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setPureBlack(enabled: Boolean)
    suspend fun setDynamicColor(enabled: Boolean)
    suspend fun setReminderEnabled(enabled: Boolean)
    suspend fun setReminderTimeMinutes(minutes: Int)
    suspend fun setWeeklySummaryEnabled(enabled: Boolean)
    suspend fun setMonthlySummaryEnabled(enabled: Boolean)
    suspend fun setOnboardingDone(done: Boolean)
    suspend fun setLastBackupEpochMillis(epochMillis: Long)
    suspend fun setUserProfile(profile: com.supernovea.spendsense.domain.model.UserProfileType)
    suspend fun setOpeningBalanceMinor(amountMinor: Long)
    suspend fun setPocketMoneyDay(day: Int?)
    suspend fun setHideAmountsDefault(enabled: Boolean)
    suspend fun setSecureScreenEnabled(enabled: Boolean)
    suspend fun setAppLockEnabled(enabled: Boolean)
    suspend fun setBiometricAuthEnabled(enabled: Boolean)
    suspend fun setAppLockTimeoutSeconds(seconds: Int)
    suspend fun setNotificationPrivacyEnabled(enabled: Boolean)
}
