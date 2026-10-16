/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.usecase.DeleteExpenseUseCase
import com.supernovea.spendsense.domain.usecase.DeleteIncomeUseCase
import com.supernovea.spendsense.domain.usecase.GetExpensesUseCase
import com.supernovea.spendsense.domain.usecase.GetIncomesUseCase
import com.supernovea.spendsense.domain.usecase.ManageCategoriesUseCase
import com.supernovea.spendsense.domain.usecase.RestoreExpenseUseCase
import com.supernovea.spendsense.domain.usecase.RestoreIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val restoreExpenseUseCase: RestoreExpenseUseCase,
    private val getIncomesUseCase: GetIncomesUseCase,
    private val deleteIncomeUseCase: DeleteIncomeUseCase,
    private val restoreIncomeUseCase: RestoreIncomeUseCase,
    private val manageCategoriesUseCase: ManageCategoriesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val selectedTab = MutableStateFlow(HistoryTab.ALL)
    private val searchQuery = MutableStateFlow("")
    private val typeFilter = MutableStateFlow<ExpenseType?>(null)
    private val categoryFilter = MutableStateFlow<Long?>(null)
    private val paymentFilter = MutableStateFlow<PaymentMethod?>(null)

    private var recentlyDeletedTransaction: HistoryTransaction? = null

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            combine(
                listOf(
                    getExpensesUseCase.getAll(),
                    getIncomesUseCase.observeAll(),
                    manageCategoriesUseCase.getCategories(includeArchived = false),
                    selectedTab,
                    searchQuery,
                    typeFilter,
                    categoryFilter,
                    paymentFilter,
                )
            ) { flows ->
                @Suppress("UNCHECKED_CAST")
                val allExpenses = flows[0] as List<ExpenseWithCategory>
                @Suppress("UNCHECKED_CAST")
                val allIncomes = flows[1] as List<IncomeWithSource>
                @Suppress("UNCHECKED_CAST")
                val categories = flows[2] as List<Category>
                val tab = flows[3] as HistoryTab
                val query = flows[4] as String
                val type = flows[5] as ExpenseType?
                val catId = flows[6] as Long?
                val payment = flows[7] as PaymentMethod?

                val expenseItems = if (tab == HistoryTab.INCOME) {
                    emptyList()
                } else {
                    allExpenses.filter { item ->
                        val matchesQuery = query.isBlank() ||
                            (item.expense.note?.contains(query, ignoreCase = true) == true) ||
                            item.category.name.contains(query, ignoreCase = true)

                        val matchesType = type == null || item.expense.type == type
                        val matchesCategory = catId == null || item.expense.categoryId == catId
                        val matchesPayment = payment == null || item.expense.paymentMethod == payment

                        matchesQuery && matchesType && matchesCategory && matchesPayment
                    }.map { HistoryTransaction.ExpenseItem(it) }
                }

                val incomeItems = if (tab == HistoryTab.EXPENSES) {
                    emptyList()
                } else {
                    allIncomes.filter { item ->
                        // Expense-only filters do not apply to income, so if an expense-only filter is set, income is excluded
                        if (type != null || catId != null || payment != null) {
                            return@filter false
                        }
                        val matchesQuery = query.isBlank() ||
                            (item.income.note?.contains(query, ignoreCase = true) == true) ||
                            item.source.name.contains(query, ignoreCase = true)

                        matchesQuery
                    }.map { HistoryTransaction.IncomeItem(it) }
                }

                val combined: List<HistoryTransaction> = expenseItems + incomeItems

                val dayGroups = combined
                    .groupBy { it.occurredDate }
                    .toSortedMap(compareByDescending { it })
                    .map { (date, items) ->
                        val sortedItems = items.sortedWith(
                            compareByDescending<HistoryTransaction> { it.occurredInstant }
                                .thenByDescending { it.id }
                        )
                        val spent = sortedItems.filterIsInstance<HistoryTransaction.ExpenseItem>()
                            .sumOf { it.amountMinor }
                        val received = sortedItems.filterIsInstance<HistoryTransaction.IncomeItem>()
                            .sumOf { it.amountMinor }
                        val need = sortedItems.filterIsInstance<HistoryTransaction.ExpenseItem>()
                            .filter { it.expenseWithCategory.expense.type == ExpenseType.NEED }
                            .sumOf { it.amountMinor }
                        val waste = sortedItems.filterIsInstance<HistoryTransaction.ExpenseItem>()
                            .filter { it.expenseWithCategory.expense.type == ExpenseType.WASTE }
                            .sumOf { it.amountMinor }

                        DayGroup(
                            date = date,
                            items = sortedItems,
                            spentMinor = spent,
                            receivedMinor = received,
                            needMinor = need,
                            wasteMinor = waste,
                        )
                    }

                HistoryUiState(
                    selectedTab = tab,
                    searchQuery = query,
                    selectedTypeFilter = type,
                    selectedCategoryId = catId,
                    selectedPaymentMethod = payment,
                    dayGroups = dayGroups,
                    totalItemCount = combined.size,
                    categories = categories,
                    isLoading = false,
                    canUndoDelete = recentlyDeletedTransaction != null,
                )
            }.collect { newState ->
                _uiState.update { current ->
                    newState.copy(
                        snackbarMessage = current.snackbarMessage,
                    )
                }
            }
        }
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.OnTabSelected -> {
                selectedTab.value = event.tab
            }
            is HistoryEvent.OnSearchQueryChanged -> {
                searchQuery.value = event.query
            }
            is HistoryEvent.OnTypeFilterSelected -> {
                typeFilter.value = event.type
            }
            is HistoryEvent.OnCategoryFilterSelected -> {
                categoryFilter.value = event.categoryId
            }
            is HistoryEvent.OnPaymentFilterSelected -> {
                paymentFilter.value = event.method
            }
            is HistoryEvent.OnDeleteTransaction -> {
                deleteTransaction(event.item)
            }
            is HistoryEvent.OnDeleteExpense -> {
                deleteExpense(event.expense)
            }
            is HistoryEvent.OnUndoDelete -> {
                undoDelete()
            }
            is HistoryEvent.OnDismissSnackbar -> {
                _uiState.update { it.copy(snackbarMessage = null) }
            }
        }
    }

    private fun deleteTransaction(item: HistoryTransaction) {
        recentlyDeletedTransaction = item
        viewModelScope.launch {
            when (item) {
                is HistoryTransaction.ExpenseItem -> {
                    deleteExpenseUseCase(item.id)
                    _uiState.update {
                        it.copy(
                            snackbarMessage = "Expense deleted",
                            canUndoDelete = true,
                        )
                    }
                }
                is HistoryTransaction.IncomeItem -> {
                    deleteIncomeUseCase(item.id)
                    _uiState.update {
                        it.copy(
                            snackbarMessage = "Income deleted",
                            canUndoDelete = true,
                        )
                    }
                }
            }
        }
    }

    private fun deleteExpense(expense: Expense) {
        val category = _uiState.value.categories.firstOrNull { it.id == expense.categoryId }
            ?: Category(id = expense.categoryId, name = "", iconKey = "category", colorHex = "#9CA3AF", defaultType = expense.type)
        val item = HistoryTransaction.ExpenseItem(ExpenseWithCategory(expense = expense, category = category))
        deleteTransaction(item)
    }

    private fun undoDelete() {
        val toRestore = recentlyDeletedTransaction ?: return
        viewModelScope.launch {
            when (toRestore) {
                is HistoryTransaction.ExpenseItem -> restoreExpenseUseCase(toRestore.id)
                is HistoryTransaction.IncomeItem -> restoreIncomeUseCase(toRestore.id)
            }
            recentlyDeletedTransaction = null
            _uiState.update {
                it.copy(
                    snackbarMessage = "Restored successfully",
                    canUndoDelete = false,
                )
            }
        }
    }
}
