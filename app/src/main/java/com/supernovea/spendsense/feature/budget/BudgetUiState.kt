/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.budget

import com.supernovea.spendsense.domain.model.BudgetStatus
import java.time.YearMonth

data class BudgetUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val totalLimitInput: String = "",
    val wasteLimitInput: String = "",
    val currencySymbol: String = "₹",
    val status: BudgetStatus? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val errorMessage: String? = null,
)
