/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.OnNext -> {
                _uiState.update { current ->
                    val nextStep = when (current.step) {
                        OnboardingStep.WELCOME -> OnboardingStep.PROFILE
                        OnboardingStep.PROFILE -> OnboardingStep.OPENING_BALANCE
                        OnboardingStep.OPENING_BALANCE -> OnboardingStep.READY
                        OnboardingStep.READY -> OnboardingStep.READY
                    }
                    current.copy(step = nextStep)
                }
            }

            is OnboardingEvent.OnBack -> {
                _uiState.update { current ->
                    val prevStep = when (current.step) {
                        OnboardingStep.WELCOME -> OnboardingStep.WELCOME
                        OnboardingStep.PROFILE -> OnboardingStep.WELCOME
                        OnboardingStep.OPENING_BALANCE -> OnboardingStep.PROFILE
                        OnboardingStep.READY -> OnboardingStep.OPENING_BALANCE
                    }
                    current.copy(step = prevStep)
                }
            }

            is OnboardingEvent.OnProfileSelect -> {
                _uiState.update { it.copy(selectedProfile = event.profile) }
            }

            is OnboardingEvent.OnBalanceChange -> {
                val cleaned = event.input.filter { it.isDigit() || it == '.' }
                val parsedPaise = Money.rupeesToPaise(cleaned)
                _uiState.update {
                    it.copy(
                        openingBalanceInput = cleaned,
                        openingBalanceMinor = parsedPaise,
                    )
                }
            }

            is OnboardingEvent.OnSkip -> {
                completeOnboarding(saveDetails = false)
            }

            is OnboardingEvent.OnFinish -> {
                completeOnboarding(saveDetails = true)
            }
        }
    }

    private fun completeOnboarding(saveDetails: Boolean) {
        viewModelScope.launch {
            if (saveDetails) {
                settingsRepository.setUserProfile(_uiState.value.selectedProfile)
                settingsRepository.setOpeningBalanceMinor(_uiState.value.openingBalanceMinor)
            }
            settingsRepository.setOnboardingDone(true)
            _uiState.update { it.copy(isCompleted = true) }
        }
    }
}
