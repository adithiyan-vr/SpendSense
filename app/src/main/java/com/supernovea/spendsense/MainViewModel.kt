/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.core.security.AppLockManager
import com.supernovea.spendsense.domain.model.UserSettings
import com.supernovea.spendsense.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val appLockManager: AppLockManager,
) : ViewModel() {

    val appSettings: StateFlow<UserSettings?> = settingsRepository.observeSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    val settingsLoaded: StateFlow<Boolean> = appSettings
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    val isLocked: StateFlow<Boolean> = appLockManager.isLocked

    init {
        viewModelScope.launch {
            appSettings.filterNotNull().distinctUntilChanged().collect { settings ->
                appLockManager.initialize(settings.appLockEnabled)
            }
        }
    }

    fun onAppBackgrounded() {
        appLockManager.onAppBackgrounded()
    }

    fun onAppForegrounded() {
        val settings = appSettings.value
        val enabled = settings?.appLockEnabled ?: false
        val timeout = settings?.appLockTimeoutSeconds ?: 0
        appLockManager.onAppForegrounded(enabled, timeout)
    }
}
