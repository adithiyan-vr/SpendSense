/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense

import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.WindowManager
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supernovea.spendsense.core.designsystem.LOADER_MIN_DISPLAY_MS
import com.supernovea.spendsense.core.designsystem.SpendSenseLoaderScreen
import com.supernovea.spendsense.core.designsystem.SpendSenseTheme
import com.supernovea.spendsense.core.designsystem.isReduceMotionEnabled
import com.supernovea.spendsense.core.designsystem.resolveDarkTheme
import com.supernovea.spendsense.core.navigation.SpendSenseNavHost
import com.supernovea.spendsense.core.security.BiometricAuthHelper
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.feature.lock.LockScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private var shortcutAction by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Core SplashScreen installed before super.onCreate
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        shortcutAction = intent?.getStringExtra("shortcut_action")

        // Wait until settings are loaded from DataStore before dismissing splash to eliminate theme flash (FR-112)
        splash.setKeepOnScreenCondition { !mainViewModel.settingsLoaded.value }

        val isColdStart = (savedInstanceState == null)

        setContent {
            val settingsNullable by mainViewModel.appSettings.collectAsStateWithLifecycle()
            val settings = settingsNullable ?: UserSettings()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = resolveDarkTheme(settings.themeMode, systemDark)
            val isLocked by mainViewModel.isLocked.collectAsStateWithLifecycle()

            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkTheme },
                )
                onDispose {}
            }

            // Screen Protection (FLAG_SECURE)
            DisposableEffect(settings.secureScreenEnabled) {
                if (settings.secureScreenEnabled) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
                onDispose {}
            }

            SpendSenseTheme(
                darkTheme = darkTheme,
                pureBlack = settings.pureBlack && darkTheme,
                dynamicColor = settings.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            ) {
                // Root UI Composition Gating (FR-136)
                if (isLocked) {
                    LockScreen()
                } else {
                    MainAppContent(
                        isColdStart = isColdStart,
                        onboardingDone = settings.onboardingDone,
                        shortcutAction = shortcutAction,
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.onAppForegrounded()
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.onAppBackgrounded()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        shortcutAction = intent.getStringExtra("shortcut_action")
    }
}

@Composable
fun MainAppContent(
    isColdStart: Boolean,
    onboardingDone: Boolean,
    shortcutAction: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showStartupLoader by rememberSaveable { mutableStateOf(isColdStart) }

    if (showStartupLoader) {
        val startTime = remember { SystemClock.uptimeMillis() }
        val isReducedMotion = remember(context) { isReduceMotionEnabled(context) }

        LaunchedEffect(Unit) {
            if (!isReducedMotion) {
                val elapsed = SystemClock.uptimeMillis() - startTime
                val remaining = LOADER_MIN_DISPLAY_MS - elapsed
                if (remaining > 0) {
                    delay(remaining)
                }
            }
            showStartupLoader = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        SpendSenseNavHost(
            onboardingDone = onboardingDone,
            initialShortcutAction = shortcutAction,
        )

        AnimatedVisibility(
            visible = showStartupLoader,
            exit = fadeOut(animationSpec = tween(durationMillis = 150))
        ) {
            SpendSenseLoaderScreen()
        }
    }
}
