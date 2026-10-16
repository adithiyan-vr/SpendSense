/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.security

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LockoutManagerTest {

    private val prefsMap = mutableMapOf<String, Any>()
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private val prefs = mockk<SharedPreferences>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private lateinit var lockoutManager: LockoutManager

    @Before
    fun setUp() {
        prefsMap.clear()
        every { editor.putInt(any(), any()) } answers {
            prefsMap[firstArg()] = secondArg<Int>()
            editor
        }
        every { editor.putLong(any(), any()) } answers {
            prefsMap[firstArg()] = secondArg<Long>()
            editor
        }
        every { editor.clear() } answers {
            prefsMap.clear()
            editor
        }
        every { editor.apply() } returns Unit

        every { prefs.getInt(any(), any()) } answers {
            (prefsMap[firstArg()] as? Int) ?: secondArg()
        }
        every { prefs.getLong(any(), any()) } answers {
            (prefsMap[firstArg()] as? Long) ?: secondArg()
        }
        every { prefs.edit() } returns editor
        every { context.getSharedPreferences(any(), any()) } returns prefs

        lockoutManager = LockoutManager(context)
    }

    @Test
    fun `initial state has zero failed attempts and not locked out`() {
        assertEquals(0, lockoutManager.getFailedAttempts())
        assertFalse(lockoutManager.isLockedOut())
        assertEquals(0L, lockoutManager.getRemainingLockoutSeconds())
    }

    @Test
    fun `failed attempts 1 to 4 do not trigger lockout`() {
        for (i in 1..4) {
            val lockoutSeconds = lockoutManager.recordFailedAttempt()
            assertEquals(0L, lockoutSeconds)
            assertEquals(i, lockoutManager.getFailedAttempts())
            assertFalse(lockoutManager.isLockedOut())
        }
    }

    @Test
    fun `5th failed attempt triggers 30s lockout tier`() {
        repeat(4) { lockoutManager.recordFailedAttempt() }
        val lockoutSeconds = lockoutManager.recordFailedAttempt()

        assertEquals(30L, lockoutSeconds)
        assertEquals(5, lockoutManager.getFailedAttempts())
        assertTrue(lockoutManager.isLockedOut())
        assertTrue(lockoutManager.getRemainingLockoutSeconds() in 28..30)
    }

    @Test
    fun `6th failed attempt triggers 60s lockout tier`() {
        repeat(5) { lockoutManager.recordFailedAttempt() }
        val lockoutSeconds = lockoutManager.recordFailedAttempt()

        assertEquals(60L, lockoutSeconds)
        assertEquals(6, lockoutManager.getFailedAttempts())
        assertTrue(lockoutManager.isLockedOut())
    }

    @Test
    fun `7th failed attempt triggers 300s lockout tier`() {
        repeat(6) { lockoutManager.recordFailedAttempt() }
        val lockoutSeconds = lockoutManager.recordFailedAttempt()

        assertEquals(300L, lockoutSeconds)
        assertEquals(7, lockoutManager.getFailedAttempts())
    }

    @Test
    fun `8th failed attempt triggers 900s lockout tier`() {
        repeat(7) { lockoutManager.recordFailedAttempt() }
        val lockoutSeconds = lockoutManager.recordFailedAttempt()

        assertEquals(900L, lockoutSeconds)
        assertEquals(8, lockoutManager.getFailedAttempts())
    }

    @Test
    fun `9th and subsequent attempts trigger maximum 3600s lockout tier`() {
        repeat(8) { lockoutManager.recordFailedAttempt() }
        val lockout9 = lockoutManager.recordFailedAttempt()
        assertEquals(3600L, lockout9)

        val lockout10 = lockoutManager.recordFailedAttempt()
        assertEquals(3600L, lockout10)
    }

    @Test
    fun `resetFailedAttempts resets counters and unlocks`() {
        repeat(5) { lockoutManager.recordFailedAttempt() }
        assertTrue(lockoutManager.isLockedOut())

        lockoutManager.resetFailedAttempts()

        assertEquals(0, lockoutManager.getFailedAttempts())
        assertFalse(lockoutManager.isLockedOut())
        assertEquals(0L, lockoutManager.getRemainingLockoutSeconds())
    }

    @Test
    fun `resetLockout clears lockout state`() {
        repeat(5) { lockoutManager.recordFailedAttempt() }
        assertTrue(lockoutManager.isLockedOut())

        lockoutManager.resetLockout()

        assertEquals(0, lockoutManager.getFailedAttempts())
        assertFalse(lockoutManager.isLockedOut())
    }
}
