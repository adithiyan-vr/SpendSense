/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.supernovea.spendsense.domain.model.ThemeMode
import com.supernovea.spendsense.domain.model.WeekStartDay
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsRepositoryTest {

    private val dataStore = mockk<DataStore<Preferences>>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setup() {
        repository = SettingsRepositoryImpl(dataStore, testDispatcher)
    }

    @Test
    fun `default settings are emitted when preferences are empty`() = runTest(testDispatcher) {
        val emptyPrefs = preferencesOf()
        every { dataStore.data } returns flowOf(emptyPrefs)

        val settings = repository.observeSettings().first()

        assertEquals("₹", settings.currencySymbol)
        assertEquals(WeekStartDay.MONDAY, settings.weekStartDay)
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertEquals(false, settings.pureBlack)
        assertEquals(false, settings.dynamicColor)
        assertEquals(false, settings.reminderEnabled)
        assertEquals(false, settings.onboardingDone)
    }

    @Test
    fun `settings are correctly mapped from preferences`() = runTest(testDispatcher) {
        val currencyKey = stringPreferencesKey("currency_symbol")
        val weekKey = stringPreferencesKey("week_start_day")
        val themeKey = stringPreferencesKey("theme_mode")
        val pureBlackKey = booleanPreferencesKey("pure_black")
        val dynamicColorKey = booleanPreferencesKey("dynamic_color")
        val onboardingKey = booleanPreferencesKey("onboarding_done")

        val prefs = preferencesOf(
            currencyKey to "$",
            weekKey to "SUNDAY",
            themeKey to "DARK",
            pureBlackKey to true,
            dynamicColorKey to true,
            onboardingKey to true,
        )
        every { dataStore.data } returns flowOf(prefs)

        val settings = repository.observeSettings().first()

        assertEquals("$", settings.currencySymbol)
        assertEquals(WeekStartDay.SUNDAY, settings.weekStartDay)
        assertEquals(ThemeMode.DARK, settings.themeMode)
        assertEquals(true, settings.pureBlack)
        assertEquals(true, settings.dynamicColor)
        assertEquals(true, settings.onboardingDone)
    }

    @Test
    fun `theme mode falls back to SYSTEM when invalid value is in datastore`() = runTest(testDispatcher) {
        val themeKey = stringPreferencesKey("theme_mode")
        val prefs = preferencesOf(themeKey to "UNKNOWN_VALUE_OR_CORRUPTED")
        every { dataStore.data } returns flowOf(prefs)

        val settings = repository.observeSettings().first()

        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
    }

    @Test
    fun `setCurrencySymbol calls edit on datastore`() = runTest(testDispatcher) {
        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = firstArg<suspend (Preferences) -> Preferences>()
            val mutablePrefs = mockk<MutablePreferences>(relaxed = true)
            transform(mutablePrefs)
            mutablePrefs
        }

        repository.setCurrencySymbol("$")

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setPureBlack calls edit on datastore`() = runTest(testDispatcher) {
        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = firstArg<suspend (Preferences) -> Preferences>()
            val mutablePrefs = mockk<MutablePreferences>(relaxed = true)
            transform(mutablePrefs)
            mutablePrefs
        }

        repository.setPureBlack(true)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setDynamicColor calls edit on datastore`() = runTest(testDispatcher) {
        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = firstArg<suspend (Preferences) -> Preferences>()
            val mutablePrefs = mockk<MutablePreferences>(relaxed = true)
            transform(mutablePrefs)
            mutablePrefs
        }

        repository.setDynamicColor(true)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `userProfile and opening balance are mapped and updated`() = runTest(testDispatcher) {
        val profileKey = stringPreferencesKey("user_profile_type")
        val balanceKey = androidx.datastore.preferences.core.longPreferencesKey("opening_balance_minor")
        val prefs = preferencesOf(
            profileKey to "WORKING_PROFESSIONAL",
            balanceKey to 2500000L,
        )
        every { dataStore.data } returns flowOf(prefs)

        val settings = repository.observeSettings().first()
        assertEquals(com.supernovea.spendsense.domain.model.UserProfileType.WORKING_PROFESSIONAL, settings.userProfile)
        assertEquals(2500000L, settings.openingBalanceMinor)

        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = firstArg<suspend (Preferences) -> Preferences>()
            val mutablePrefs = mockk<MutablePreferences>(relaxed = true)
            transform(mutablePrefs)
            mutablePrefs
        }

        repository.setUserProfile(com.supernovea.spendsense.domain.model.UserProfileType.STUDENT)
        repository.setOpeningBalanceMinor(100000L)
        repository.setPocketMoneyDay(5)

        coVerify(exactly = 3) { dataStore.updateData(any()) }
    }
}
