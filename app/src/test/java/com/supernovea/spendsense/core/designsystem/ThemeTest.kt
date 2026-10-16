/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.designsystem

import com.supernovea.spendsense.domain.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTest {

    @Test
    fun `resolveDarkTheme with SYSTEM mode returns systemDark state`() {
        assertEquals(false, resolveDarkTheme(ThemeMode.SYSTEM, systemDark = false))
        assertEquals(true, resolveDarkTheme(ThemeMode.SYSTEM, systemDark = true))
    }

    @Test
    fun `resolveDarkTheme with LIGHT mode always returns false`() {
        assertEquals(false, resolveDarkTheme(ThemeMode.LIGHT, systemDark = false))
        assertEquals(false, resolveDarkTheme(ThemeMode.LIGHT, systemDark = true))
    }

    @Test
    fun `resolveDarkTheme with DARK mode always returns true`() {
        assertEquals(true, resolveDarkTheme(ThemeMode.DARK, systemDark = false))
        assertEquals(true, resolveDarkTheme(ThemeMode.DARK, systemDark = true))
    }

    @Test
    fun `all six mode and system combinations resolve deterministically`() {
        val expectations = listOf(
            Triple(ThemeMode.SYSTEM, false, false),
            Triple(ThemeMode.SYSTEM, true, true),
            Triple(ThemeMode.LIGHT, false, false),
            Triple(ThemeMode.LIGHT, true, false),
            Triple(ThemeMode.DARK, false, true),
            Triple(ThemeMode.DARK, true, true),
        )

        for ((mode, systemDark, expected) in expectations) {
            val result = resolveDarkTheme(mode, systemDark)
            assertEquals("Failed for mode=$mode, systemDark=$systemDark", expected, result)
        }
    }
}
