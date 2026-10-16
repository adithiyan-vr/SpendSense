/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.security

import android.os.SystemClock
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AppLockManagerTest {

    private val pinManager = mockk<PinManager>(relaxed = true)
    private lateinit var appLockManager: AppLockManager

    @Before
    fun setUp() {
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 100000L
        appLockManager = AppLockManager(pinManager)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `initialize with appLockEnabled true sets isLocked to true on cold start`() {
        appLockManager.initialize(appLockEnabled = true)
        assertTrue(appLockManager.isLocked.value)
    }

    @Test
    fun `initialize with appLockEnabled false leaves isLocked false`() {
        appLockManager.initialize(appLockEnabled = false)
        assertFalse(appLockManager.isLocked.value)
    }

    @Test
    fun `subsequent initialize calls do not override unlocked state`() {
        appLockManager.initialize(appLockEnabled = true)
        assertTrue(appLockManager.isLocked.value)

        appLockManager.unlock()
        assertFalse(appLockManager.isLocked.value)

        // Subsequent call
        appLockManager.initialize(appLockEnabled = true)
        assertFalse(appLockManager.isLocked.value)
    }

    @Test
    fun `unlock sets isLocked to false`() {
        appLockManager.initialize(appLockEnabled = true)
        appLockManager.unlock()
        assertFalse(appLockManager.isLocked.value)
    }

    @Test
    fun `lock sets isLocked to true`() {
        appLockManager.initialize(appLockEnabled = false)
        assertFalse(appLockManager.isLocked.value)

        appLockManager.lock()
        assertTrue(appLockManager.isLocked.value)
    }

    @Test
    fun `foreground before timeout keeps app unlocked`() {
        appLockManager.initialize(appLockEnabled = true)
        appLockManager.unlock()
        assertFalse(appLockManager.isLocked.value)

        // Backgrounded at t = 100,000ms
        every { SystemClock.elapsedRealtime() } returns 100000L
        appLockManager.onAppBackgrounded()

        // Foregrounded at t = 115,000ms (15s elapsed, timeout is 30s)
        every { SystemClock.elapsedRealtime() } returns 115000L
        appLockManager.onAppForegrounded(appLockEnabled = true, timeoutSeconds = 30)

        assertFalse(appLockManager.isLocked.value)
    }

    @Test
    fun `foreground after timeout locks the app`() {
        appLockManager.initialize(appLockEnabled = true)
        appLockManager.unlock()
        assertFalse(appLockManager.isLocked.value)

        // Backgrounded at t = 100,000ms
        every { SystemClock.elapsedRealtime() } returns 100000L
        appLockManager.onAppBackgrounded()

        // Foregrounded at t = 135,000ms (35s elapsed, timeout is 30s)
        every { SystemClock.elapsedRealtime() } returns 135000L
        appLockManager.onAppForegrounded(appLockEnabled = true, timeoutSeconds = 30)

        assertTrue(appLockManager.isLocked.value)
    }

    @Test
    fun `foreground with immediate timeout locks the app immediately`() {
        appLockManager.initialize(appLockEnabled = true)
        appLockManager.unlock()
        assertFalse(appLockManager.isLocked.value)

        every { SystemClock.elapsedRealtime() } returns 100000L
        appLockManager.onAppBackgrounded()

        every { SystemClock.elapsedRealtime() } returns 100100L // 100ms
        appLockManager.onAppForegrounded(appLockEnabled = true, timeoutSeconds = 0)

        assertTrue(appLockManager.isLocked.value)
    }

    @Test
    fun `foreground when appLockEnabled is false never locks`() {
        appLockManager.initialize(appLockEnabled = true)
        assertTrue(appLockManager.isLocked.value)

        every { SystemClock.elapsedRealtime() } returns 100000L
        appLockManager.onAppBackgrounded()

        every { SystemClock.elapsedRealtime() } returns 200000L
        appLockManager.onAppForegrounded(appLockEnabled = false, timeoutSeconds = 0)

        assertFalse(appLockManager.isLocked.value)
    }
}
