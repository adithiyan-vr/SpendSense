/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.startup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.supernovea.spendsense.MainAppContent
import com.supernovea.spendsense.core.designsystem.SpendSenseLoader
import com.supernovea.spendsense.core.designsystem.SpendSenseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpendSenseLoaderUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loader_displaysWithAccessibilityContentDescription() {
        composeTestRule.setContent {
            SpendSenseTheme {
                SpendSenseLoader()
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Loading SpendSense")
            .assertIsDisplayed()
    }

    @Test
    fun coldStart_showsLoaderThenDisappearsWhenReady() {
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.setContent {
            SpendSenseTheme {
                MainAppContent(isColdStart = true)
            }
        }

        // On cold start, loader node is immediately present
        composeTestRule
            .onNodeWithContentDescription("Loading SpendSense")
            .assertIsDisplayed()

        // Advance past LOADER_MIN_DISPLAY_MS (1500ms) + fadeout (150ms)
        composeTestRule.mainClock.advanceTimeBy(1700L)
        composeTestRule.waitForIdle()

        // Loader should have disappeared
        composeTestRule
            .onNodeWithContentDescription("Loading SpendSense")
            .assertDoesNotExist()
    }

    @Test
    fun warmStart_doesNotShowStartupLoader() {
        composeTestRule.setContent {
            SpendSenseTheme {
                MainAppContent(isColdStart = false)
            }
        }

        // On warm start, loader should not exist
        composeTestRule
            .onNodeWithContentDescription("Loading SpendSense")
            .assertDoesNotExist()
    }
}
