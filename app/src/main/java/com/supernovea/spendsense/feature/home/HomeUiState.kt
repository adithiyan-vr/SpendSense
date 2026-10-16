/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.home

import com.supernovea.spendsense.domain.model.BudgetStatus
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.Insight
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.UserProfileType
import com.supernovea.spendsense.feature.history.HistoryTransaction

enum class HomePeriod {
    TODAY,
    WEEK,
    MONTH,
}

data class MiniPeriodSpend(
    val totalMinor: Long = 0L,
    val wasteMinor: Long = 0L,
    val wasteRatioPct: Double = 0.0,
)

data class HomeUiState(
    val userName: String = "Friend",
    val userProfile: UserProfileType = UserProfileType.STUDENT,
    val currencySymbol: String = "₹",
    // Hero Available Balance (UPD-02 / FR-071)
    val availableBalanceMinor: Long = 0L,
    val openingBalanceMinor: Long = 0L,
    val daysLeftEstimate: Int? = null,
    val isAmountsHidden: Boolean = false,
    // Period metrics (Today, Week, Month)
    val selectedPeriod: HomePeriod = HomePeriod.MONTH,
    val receivedMinor: Long = 0L,
    val spentMinor: Long = 0L,
    val netMinor: Long = 0L,
    val savingsRatePct: Double? = null,
    val savingsRateDeltaPts: Double? = null,
    val needMinor: Long = 0L,
    val wasteMinor: Long = 0L,
    val wasteRatioPct: Double = 0.0,
    val wasteRatioDeltaPts: Double? = null,
    // Overview summaries
    val monthSummary: PeriodSummary? = null,
    val budgetStatus: BudgetStatus? = null,
    val topInsight: Insight? = null,
    val todaySpend: MiniPeriodSpend = MiniPeriodSpend(),
    val weekSpend: MiniPeriodSpend = MiniPeriodSpend(),
    val recentExpenses: List<ExpenseWithCategory> = emptyList(),
    val recentTransactions: List<HistoryTransaction> = emptyList(),
    val hasAnyExpenses: Boolean = false,
    val hasAnyTransactions: Boolean = false,
    val isLoading: Boolean = true,
)

sealed interface HomeEvent {
    data class OnSelectPeriod(val period: HomePeriod) : HomeEvent
    data object OnToggleHideAmounts : HomeEvent
    data object OnRefresh : HomeEvent
}
