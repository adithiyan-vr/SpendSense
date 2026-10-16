/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.onboarding

import com.supernovea.spendsense.domain.model.UserProfileType

enum class OnboardingStep {
    WELCOME,
    PROFILE,
    OPENING_BALANCE,
    READY,
}

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val selectedProfile: UserProfileType = UserProfileType.STUDENT,
    val openingBalanceInput: String = "0",
    val openingBalanceMinor: Long = 0L,
    val isCompleted: Boolean = false,
)

sealed interface OnboardingEvent {
    data object OnNext : OnboardingEvent
    data object OnBack : OnboardingEvent
    data class OnProfileSelect(val profile: UserProfileType) : OnboardingEvent
    data class OnBalanceChange(val input: String) : OnboardingEvent
    data object OnSkip : OnboardingEvent
    data object OnFinish : OnboardingEvent
}
