/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.security

import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor(
    private val pinManager: PinManager,
) {
    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private var backgroundTimestamp: Long = 0L
    private var isInitialized = false

    fun initialize(appLockEnabled: Boolean) {
        if (!isInitialized) {
            isInitialized = true
            // Cold start is always locked if app lock is enabled
            if (appLockEnabled) {
                _isLocked.value = true
            }
        }
    }

    fun unlock() {
        _isLocked.value = false
        backgroundTimestamp = 0L
    }

    fun lock() {
        _isLocked.value = true
    }

    fun onAppBackgrounded() {
        backgroundTimestamp = SystemClock.elapsedRealtime()
    }

    fun onAppForegrounded(appLockEnabled: Boolean, timeoutSeconds: Int) {
        if (!appLockEnabled) {
            _isLocked.value = false
            return
        }

        if (backgroundTimestamp == 0L) {
            // Already locked or foregrounded from startup
            return
        }

        val elapsedMs = SystemClock.elapsedRealtime() - backgroundTimestamp
        val timeoutMs = timeoutSeconds * 1000L

        if (elapsedMs >= timeoutMs) {
            _isLocked.value = true
        }
        backgroundTimestamp = 0L
    }

    fun isPinConfigured(): Boolean = pinManager.isPinSet()
}
