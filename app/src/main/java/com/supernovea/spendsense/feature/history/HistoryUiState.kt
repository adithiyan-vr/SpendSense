/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.history

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.model.PaymentMethod
import java.time.Instant
import java.time.LocalDate

enum class HistoryTab {
    ALL,
    EXPENSES,
    INCOME,
}

sealed interface HistoryTransaction {
    val id: Long
    val amountMinor: Long
    val occurredDate: LocalDate
    val occurredInstant: Instant
    val note: String?

    data class ExpenseItem(val expenseWithCategory: ExpenseWithCategory) : HistoryTransaction {
        override val id: Long get() = expenseWithCategory.expense.id
        override val amountMinor: Long get() = expenseWithCategory.expense.amountMinor
        override val occurredDate: LocalDate get() = expenseWithCategory.expense.occurredDate
        override val occurredInstant: Instant get() = expenseWithCategory.expense.occurredAt
        override val note: String? get() = expenseWithCategory.expense.note
    }

    data class IncomeItem(val incomeWithSource: IncomeWithSource) : HistoryTransaction {
        override val id: Long get() = incomeWithSource.income.id
        override val amountMinor: Long get() = incomeWithSource.income.amountMinor
        override val occurredDate: LocalDate get() = incomeWithSource.income.receivedDate
        override val occurredInstant: Instant get() = incomeWithSource.income.receivedAt
        override val note: String? get() = incomeWithSource.income.note
    }
}

data class DayGroup(
    val date: LocalDate,
    val items: List<HistoryTransaction>,
    val spentMinor: Long,
    val receivedMinor: Long,
    val needMinor: Long,
    val wasteMinor: Long,
) {
    // Legacy compatibility for pure expense consumers
    val totalMinor: Long get() = spentMinor
}

data class HistoryUiState(
    val selectedTab: HistoryTab = HistoryTab.ALL,
    val searchQuery: String = "",
    val selectedTypeFilter: ExpenseType? = null,
    val selectedCategoryId: Long? = null,
    val selectedPaymentMethod: PaymentMethod? = null,
    val dayGroups: List<DayGroup> = emptyList(),
    val totalItemCount: Int = 0,
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val snackbarMessage: String? = null,
    val canUndoDelete: Boolean = false,
) {
    val totalExpenseCount: Int get() = totalItemCount
}

sealed interface HistoryEvent {
    data class OnTabSelected(val tab: HistoryTab) : HistoryEvent
    data class OnSearchQueryChanged(val query: String) : HistoryEvent
    data class OnTypeFilterSelected(val type: ExpenseType?) : HistoryEvent
    data class OnCategoryFilterSelected(val categoryId: Long?) : HistoryEvent
    data class OnPaymentFilterSelected(val method: PaymentMethod?) : HistoryEvent
    data class OnDeleteTransaction(val item: HistoryTransaction) : HistoryEvent
    data class OnDeleteExpense(val expense: Expense) : HistoryEvent
    data object OnUndoDelete : HistoryEvent
    data object OnDismissSnackbar : HistoryEvent
}
