/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.addexpense

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.model.UserProfileType
import java.time.Instant
import java.time.LocalDate

enum class EntryKind {
    EXPENSE,
    INCOME,
}

data class AddExpenseUiState(
    val entryKind: EntryKind = EntryKind.EXPENSE,
    val amountInput: String = "0",
    val amountMinor: Long = 0L,
    val selectedType: ExpenseType = ExpenseType.NEED,
    val userManuallySetType: Boolean = false,
    val selectedCategoryId: Long? = null,
    val categories: List<Category> = emptyList(),
    // Income fields
    val selectedSourceId: Long? = null,
    val incomeSources: List<IncomeSource> = emptyList(),
    val userProfile: UserProfileType = UserProfileType.STUDENT,
    val currencySymbol: String = "₹",
    // Common fields
    val note: String = "",
    val occurredDate: LocalDate = LocalDate.now(),
    val occurredInstant: Instant = Instant.now(),
    val paymentMethod: PaymentMethod = PaymentMethod.UPI,
    val isEditing: Boolean = false,
    val editingExpenseId: Long? = null,
    val editingIncomeId: Long? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val showNewCategoryDialog: Boolean = false,
) {
    val isValid: Boolean
        get() = if (entryKind == EntryKind.EXPENSE) {
            amountMinor > 0L && selectedCategoryId != null
        } else {
            amountMinor > 0L && selectedSourceId != null
        }
}

sealed interface AddExpenseEvent {
    data class OnEntryKindSelected(val kind: EntryKind) : AddExpenseEvent
    data class OnDigitClick(val digit: Char) : AddExpenseEvent
    data object OnDecimalClick : AddExpenseEvent
    data object OnBackspaceClick : AddExpenseEvent
    data object OnClearClick : AddExpenseEvent
    data class OnTypeSelected(val type: ExpenseType) : AddExpenseEvent
    data class OnCategorySelected(val category: Category) : AddExpenseEvent
    data class OnIncomeSourceSelected(val source: IncomeSource) : AddExpenseEvent
    data class OnNoteChanged(val note: String) : AddExpenseEvent
    data class OnPaymentMethodSelected(val method: PaymentMethod) : AddExpenseEvent
    data class OnDateSelected(val date: LocalDate) : AddExpenseEvent
    data object OnSaveClick : AddExpenseEvent
    data object OnOpenNewCategoryDialog : AddExpenseEvent
    data object OnDismissNewCategoryDialog : AddExpenseEvent
    data class OnCreateCategory(
        val name: String,
        val defaultType: ExpenseType,
        val iconKey: String = "category",
        val colorHex: String = "#6366F1",
    ) : AddExpenseEvent
}
