/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.security

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockoutManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs = context.getSharedPreferences("spendsense_lockout_prefs", Context.MODE_PRIVATE)

    companion object {
        const val MAX_ATTEMPTS_BEFORE_LOCKOUT = 5
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until_epoch_ms"

        private val LOCKOUT_DURATIONS_SECONDS = listOf(
            30L,   // 5th failed attempt: 30s
            60L,   // 6th failed attempt: 1 min
            300L,  // 7th failed attempt: 5 min
            900L,  // 8th failed attempt: 15 min
            3600L, // 9th+ failed attempt: 1 hour
        )
    }

    @Synchronized
    fun getFailedAttempts(): Int = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)

    @Synchronized
    fun getRemainingLockoutSeconds(): Long {
        val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        val now = System.currentTimeMillis()
        val remainingMs = lockoutUntil - now
        return if (remainingMs > 0) (remainingMs + 999) / 1000 else 0L
    }

    @Synchronized
    fun isLockedOut(): Boolean = getRemainingLockoutSeconds() > 0L

    @Synchronized
    fun recordFailedAttempt(): Long {
        val attempts = getFailedAttempts() + 1
        val lockoutSeconds = if (attempts >= MAX_ATTEMPTS_BEFORE_LOCKOUT) {
            val tierIndex = (attempts - MAX_ATTEMPTS_BEFORE_LOCKOUT).coerceAtMost(LOCKOUT_DURATIONS_SECONDS.lastIndex)
            LOCKOUT_DURATIONS_SECONDS[tierIndex]
        } else {
            0L
        }

        val lockoutUntil = if (lockoutSeconds > 0) {
            System.currentTimeMillis() + (lockoutSeconds * 1000L)
        } else {
            0L
        }

        prefs.edit {
            putInt(KEY_FAILED_ATTEMPTS, attempts)
            putLong(KEY_LOCKOUT_UNTIL, lockoutUntil)
        }

        return lockoutSeconds
    }

    @Synchronized
    fun resetFailedAttempts() {
        prefs.edit {
            putInt(KEY_FAILED_ATTEMPTS, 0)
            putLong(KEY_LOCKOUT_UNTIL, 0L)
        }
    }

    @Synchronized
    fun resetLockout() {
        resetFailedAttempts()
    }

    @Synchronized
    fun clear() {
        prefs.edit { clear() }
    }
}
