/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.budget

import java.time.YearMonth

sealed interface BudgetEvent {
    data object PreviousMonth : BudgetEvent
    data object NextMonth : BudgetEvent
    data class SelectMonth(val month: YearMonth) : BudgetEvent
    data class TotalLimitChanged(val input: String) : BudgetEvent
    data class WasteLimitChanged(val input: String) : BudgetEvent
    data object SaveBudget : BudgetEvent
    data object DeleteBudget : BudgetEvent
    data object DismissMessage : BudgetEvent
}
