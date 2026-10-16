/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.supernovea.spendsense.core.di.IoDispatcher
import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.model.WeekStartDay
import com.supernovea.spendsense.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SettingsRepository {

    private object Keys {
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val WEEK_START_DAY = stringPreferencesKey("week_start_day")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PURE_BLACK = booleanPreferencesKey("pure_black")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_TIME = intPreferencesKey("reminder_time")
        val WEEKLY_SUMMARY_ENABLED = booleanPreferencesKey("weekly_summary_enabled")
        val MONTHLY_SUMMARY_ENABLED = booleanPreferencesKey("monthly_summary_enabled")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val LAST_BACKUP_AT = longPreferencesKey("last_backup_at")
        val USER_PROFILE_TYPE = stringPreferencesKey("user_profile_type")
        val OPENING_BALANCE_MINOR = longPreferencesKey("opening_balance_minor")
        val POCKET_MONEY_DAY = intPreferencesKey("pocket_money_day")
        val HIDE_AMOUNTS_DEFAULT = booleanPreferencesKey("hide_amounts_default")
        val SECURE_SCREEN_ENABLED = booleanPreferencesKey("secure_screen_enabled")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val BIOMETRIC_AUTH_ENABLED = booleanPreferencesKey("biometric_auth_enabled")
        val APP_LOCK_TIMEOUT_SECONDS = intPreferencesKey("app_lock_timeout_seconds")
        val NOTIFICATION_PRIVACY_ENABLED = booleanPreferencesKey("notification_privacy_enabled")
    }

    override fun observeSettings(): Flow<UserSettings> {
        return dataStore.data.map { prefs ->
            val symbol = prefs[Keys.CURRENCY_SYMBOL] ?: "₹"
            val weekStart = try {
                WeekStartDay.valueOf(prefs[Keys.WEEK_START_DAY] ?: WeekStartDay.MONDAY.name)
            } catch (_: IllegalArgumentException) {
                WeekStartDay.MONDAY
            }
            val themeMode = try {
                ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
            } catch (_: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
            val pureBlack = prefs[Keys.PURE_BLACK] ?: false
            val dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: false
            val reminderEnabled = prefs[Keys.REMINDER_ENABLED] ?: false
            val reminderTime = prefs[Keys.REMINDER_TIME] ?: (21 * 60)
            val weeklySummary = prefs[Keys.WEEKLY_SUMMARY_ENABLED] ?: true
            val monthlySummary = prefs[Keys.MONTHLY_SUMMARY_ENABLED] ?: true
            val onboardingDone = prefs[Keys.ONBOARDING_DONE] ?: false
            val lastBackupRaw = prefs[Keys.LAST_BACKUP_AT] ?: -1L
            val lastBackup = if (lastBackupRaw > 0) lastBackupRaw else null

            val profileType = try {
                com.supernovea.spendsense.domain.model.UserProfileType.valueOf(
                    prefs[Keys.USER_PROFILE_TYPE] ?: com.supernovea.spendsense.domain.model.UserProfileType.STUDENT.name
                )
            } catch (_: IllegalArgumentException) {
                com.supernovea.spendsense.domain.model.UserProfileType.STUDENT
            }
            val openingBalance = prefs[Keys.OPENING_BALANCE_MINOR] ?: 0L
            val pocketMoneyDay = prefs[Keys.POCKET_MONEY_DAY]
            val hideAmounts = prefs[Keys.HIDE_AMOUNTS_DEFAULT] ?: false
            val secureScreen = prefs[Keys.SECURE_SCREEN_ENABLED] ?: true
            val appLock = prefs[Keys.APP_LOCK_ENABLED] ?: false
            val biometricAuth = prefs[Keys.BIOMETRIC_AUTH_ENABLED] ?: true
            val appLockTimeout = prefs[Keys.APP_LOCK_TIMEOUT_SECONDS] ?: 0
            val notificationPrivacy = prefs[Keys.NOTIFICATION_PRIVACY_ENABLED] ?: true

            UserSettings(
                currencySymbol = symbol,
                weekStartDay = weekStart,
                themeMode = themeMode,
                pureBlack = pureBlack,
                dynamicColor = dynamicColor,
                reminderEnabled = reminderEnabled,
                reminderTimeMinutes = reminderTime,
                weeklySummaryEnabled = weeklySummary,
                monthlySummaryEnabled = monthlySummary,
                onboardingDone = onboardingDone,
                lastBackupEpochMillis = lastBackup,
                userProfile = profileType,
                openingBalanceMinor = openingBalance,
                pocketMoneyDay = pocketMoneyDay,
                hideAmountsDefault = hideAmounts,
                secureScreenEnabled = secureScreen,
                appLockEnabled = appLock,
                biometricAuthEnabled = biometricAuth,
                appLockTimeoutSeconds = appLockTimeout,
                notificationPrivacyEnabled = notificationPrivacy,
            )
        }.flowOn(ioDispatcher)
    }

    override suspend fun getSettings(): UserSettings = withContext(ioDispatcher) {
        observeSettings().first()
    }

    override suspend fun setCurrencySymbol(symbol: String) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.CURRENCY_SYMBOL] = symbol }
        Unit
    }

    override suspend fun setWeekStartDay(day: WeekStartDay) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.WEEK_START_DAY] = day.name }
        Unit
    }

    override suspend fun setThemeMode(mode: ThemeMode) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
        Unit
    }

    override suspend fun setPureBlack(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.PURE_BLACK] = enabled }
        Unit
    }

    override suspend fun setDynamicColor(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
        Unit
    }

    override suspend fun setReminderEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.REMINDER_ENABLED] = enabled }
        Unit
    }

    override suspend fun setReminderTimeMinutes(minutes: Int) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.REMINDER_TIME] = minutes }
        Unit
    }

    override suspend fun setWeeklySummaryEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.WEEKLY_SUMMARY_ENABLED] = enabled }
        Unit
    }

    override suspend fun setMonthlySummaryEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.MONTHLY_SUMMARY_ENABLED] = enabled }
        Unit
    }

    override suspend fun setOnboardingDone(done: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
        Unit
    }

    override suspend fun setLastBackupEpochMillis(epochMillis: Long) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.LAST_BACKUP_AT] = epochMillis }
        Unit
    }

    override suspend fun setUserProfile(profile: com.supernovea.spendsense.domain.model.UserProfileType) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.USER_PROFILE_TYPE] = profile.name }
        Unit
    }

    override suspend fun setOpeningBalanceMinor(amountMinor: Long) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.OPENING_BALANCE_MINOR] = amountMinor }
        Unit
    }

    override suspend fun setPocketMoneyDay(day: Int?) = withContext(ioDispatcher) {
        dataStore.edit {
            if (day != null) {
                it[Keys.POCKET_MONEY_DAY] = day
            } else {
                it.remove(Keys.POCKET_MONEY_DAY)
            }
        }
        Unit
    }

    override suspend fun setHideAmountsDefault(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.HIDE_AMOUNTS_DEFAULT] = enabled }
        Unit
    }

    override suspend fun setSecureScreenEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.SECURE_SCREEN_ENABLED] = enabled }
        Unit
    }

    override suspend fun setAppLockEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.APP_LOCK_ENABLED] = enabled }
        Unit
    }

    override suspend fun setBiometricAuthEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.BIOMETRIC_AUTH_ENABLED] = enabled }
        Unit
    }

    override suspend fun setAppLockTimeoutSeconds(seconds: Int) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.APP_LOCK_TIMEOUT_SECONDS] = seconds }
        Unit
    }

    override suspend fun setNotificationPrivacyEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        dataStore.edit { it[Keys.NOTIFICATION_PRIVACY_ENABLED] = enabled }
        Unit
    }
}
