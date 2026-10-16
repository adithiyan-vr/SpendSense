/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class WeekStartDay {
    MONDAY,
    SUNDAY,
}

data class UserSettings(
    val currencySymbol: String = "₹",
    val weekStartDay: WeekStartDay = WeekStartDay.MONDAY,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val pureBlack: Boolean = false,
    val dynamicColor: Boolean = false,
    val reminderEnabled: Boolean = false,
    val reminderTimeMinutes: Int = 21 * 60, // 21:00 default (9:00 PM)
    val weeklySummaryEnabled: Boolean = true,
    val monthlySummaryEnabled: Boolean = true,
    val onboardingDone: Boolean = false,
    val lastBackupEpochMillis: Long? = null,
    val userProfile: UserProfileType = UserProfileType.STUDENT,
    val openingBalanceMinor: Long = 0L,
    val pocketMoneyDay: Int? = null,
    val hideAmountsDefault: Boolean = false,
    val secureScreenEnabled: Boolean = true,
    val appLockEnabled: Boolean = false,
    val biometricAuthEnabled: Boolean = true,
    val appLockTimeoutSeconds: Int = 0, // 0 = Immediately, 30 = 30s, 60 = 1m, 300 = 5m
    val notificationPrivacyEnabled: Boolean = true,
)
