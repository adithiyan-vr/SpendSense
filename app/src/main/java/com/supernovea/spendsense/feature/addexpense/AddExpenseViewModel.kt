/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.addexpense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.core.navigation.Screen
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.Expense
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.model.UserProfileType
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.AddExpenseUseCase
import com.supernovea.spendsense.domain.usecase.AddIncomeUseCase
import com.supernovea.spendsense.domain.usecase.GetExpensesUseCase
import com.supernovea.spendsense.domain.usecase.GetIncomesUseCase
import com.supernovea.spendsense.domain.usecase.ManageCategoriesUseCase
import com.supernovea.spendsense.domain.usecase.ManageIncomeSourcesUseCase
import com.supernovea.spendsense.domain.usecase.UpdateExpenseUseCase
import com.supernovea.spendsense.domain.usecase.UpdateIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val manageCategoriesUseCase: ManageCategoriesUseCase,
    private val addIncomeUseCase: AddIncomeUseCase,
    private val updateIncomeUseCase: UpdateIncomeUseCase,
    private val getIncomesUseCase: GetIncomesUseCase,
    private val manageIncomeSourcesUseCase: ManageIncomeSourcesUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    init {
        val initialTabArg = savedStateHandle.get<String>(Screen.AddExpense.ARG_INITIAL_TAB)
        if (initialTabArg?.equals("income", ignoreCase = true) == true) {
            _uiState.update { it.copy(entryKind = EntryKind.INCOME) }
        }

        val expenseIdArg = savedStateHandle.get<String>(Screen.AddExpense.ARG_EXPENSE_ID)?.toLongOrNull()
            ?: savedStateHandle.get<Long>(Screen.AddExpense.ARG_EXPENSE_ID)

        val incomeIdArg = savedStateHandle.get<String>(Screen.AddExpense.ARG_INCOME_ID)?.toLongOrNull()
            ?: savedStateHandle.get<Long>(Screen.AddExpense.ARG_INCOME_ID)

        observeSettings()
        observeCategories(expenseIdArg)
        observeIncomeSources(incomeIdArg)

        if (expenseIdArg != null && expenseIdArg > 0) {
            loadExistingExpense(expenseIdArg)
        } else if (incomeIdArg != null && incomeIdArg > 0) {
            loadExistingIncome(incomeIdArg)
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { settings ->
                _uiState.update { current ->
                    current.copy(
                        userProfile = settings.userProfile,
                        currencySymbol = settings.currencySymbol,
                    )
                }
            }
        }
    }

    private fun observeCategories(editingExpenseId: Long?) {
        viewModelScope.launch {
            manageCategoriesUseCase.getCategories(includeArchived = false).collect { categories ->
                _uiState.update { current ->
                    val selectedCategory = if (current.selectedCategoryId == null && editingExpenseId == null) {
                        categories.firstOrNull()
                    } else {
                        categories.firstOrNull { it.id == current.selectedCategoryId }
                    }

                    current.copy(
                        categories = categories,
                        selectedCategoryId = selectedCategory?.id ?: current.selectedCategoryId,
                        selectedType = if (!current.userManuallySetType && selectedCategory != null && editingExpenseId == null) {
                            selectedCategory.defaultType
                        } else {
                            current.selectedType
                        }
                    )
                }
            }
        }
    }

    private fun observeIncomeSources(editingIncomeId: Long?) {
        viewModelScope.launch {
            combine(
                manageIncomeSourcesUseCase.observeActive(),
                settingsRepository.observeSettings()
            ) { sources, settings ->
                val profile = settings.userProfile
                val sorted = sortSourcesByProfile(sources, profile)
                Pair(sorted, profile)
            }.collect { (sources, profile) ->
                _uiState.update { current ->
                    val selected = if (current.selectedSourceId == null && editingIncomeId == null) {
                        sources.firstOrNull()
                    } else {
                        sources.firstOrNull { it.id == current.selectedSourceId }
                    }

                    current.copy(
                        incomeSources = sources,
                        selectedSourceId = selected?.id ?: current.selectedSourceId,
                        userProfile = profile,
                    )
                }
            }
        }
    }

    private fun sortSourcesByProfile(sources: List<IncomeSource>, profile: UserProfileType): List<IncomeSource> {
        val studentPriority = listOf(
            "pocket_money", "stipend", "part_time", "scholarship", "prizes",
            "freelance", "gifts_received", "refunds", "other_income"
        )
        val professionalPriority = listOf(
            "salary", "bonus", "business", "rental_income", "freelance",
            "interest_returns", "gifts_received", "refunds", "other_income"
        )
        val priorityList = when (profile) {
            UserProfileType.STUDENT -> studentPriority
            UserProfileType.WORKING_PROFESSIONAL, UserProfileType.OTHER -> professionalPriority
        }

        return sources.sortedBy { source ->
            val idx = priorityList.indexOf(source.defaultKey)
            if (idx >= 0) idx else 100 + source.sortOrder
        }
    }

    private fun loadExistingExpense(expenseId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, entryKind = EntryKind.EXPENSE) }
            val expenseWithCategory = getExpensesUseCase.getById(expenseId).firstOrNull()
            if (expenseWithCategory != null) {
                val expense = expenseWithCategory.expense
                val rupeesString = Money.paiseToBigDecimal(expense.amountMinor).stripTrailingZeros().toPlainString()

                _uiState.update { current ->
                    current.copy(
                        amountInput = rupeesString,
                        amountMinor = expense.amountMinor,
                        selectedType = expense.type,
                        userManuallySetType = true,
                        selectedCategoryId = expense.categoryId,
                        note = expense.note.orEmpty(),
                        occurredDate = expense.occurredDate,
                        occurredInstant = expense.occurredAt,
                        paymentMethod = expense.paymentMethod,
                        isEditing = true,
                        editingExpenseId = expense.id,
                        isLoading = false,
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadExistingIncome(incomeId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, entryKind = EntryKind.INCOME) }
            val incomeWithSource = getIncomesUseCase.observeById(incomeId).firstOrNull()
            if (incomeWithSource != null) {
                val income = incomeWithSource.income
                val rupeesString = Money.paiseToBigDecimal(income.amountMinor).stripTrailingZeros().toPlainString()

                _uiState.update { current ->
                    current.copy(
                        amountInput = rupeesString,
                        amountMinor = income.amountMinor,
                        selectedSourceId = income.sourceId,
                        note = income.note.orEmpty(),
                        occurredDate = income.receivedDate,
                        occurredInstant = income.receivedAt,
                        isEditing = true,
                        editingIncomeId = income.id,
                        isLoading = false,
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: AddExpenseEvent) {
        when (event) {
            is AddExpenseEvent.OnEntryKindSelected -> {
                _uiState.update { it.copy(entryKind = event.kind, errorMessage = null) }
            }
            is AddExpenseEvent.OnDigitClick -> handleDigit(event.digit)
            is AddExpenseEvent.OnDecimalClick -> handleDecimal()
            is AddExpenseEvent.OnBackspaceClick -> handleBackspace()
            is AddExpenseEvent.OnClearClick -> handleClear()
            is AddExpenseEvent.OnTypeSelected -> {
                _uiState.update { it.copy(selectedType = event.type, userManuallySetType = true) }
            }
            is AddExpenseEvent.OnCategorySelected -> {
                _uiState.update { current ->
                    current.copy(
                        selectedCategoryId = event.category.id,
                        selectedType = if (!current.userManuallySetType) event.category.defaultType else current.selectedType
                    )
                }
            }
            is AddExpenseEvent.OnIncomeSourceSelected -> {
                _uiState.update { it.copy(selectedSourceId = event.source.id) }
            }
            is AddExpenseEvent.OnNoteChanged -> {
                _uiState.update { it.copy(note = event.note) }
            }
            is AddExpenseEvent.OnPaymentMethodSelected -> {
                _uiState.update { it.copy(paymentMethod = event.method) }
            }
            is AddExpenseEvent.OnDateSelected -> {
                val existingLocalTime = _uiState.value.occurredInstant
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                val targetTime = if (event.date == LocalDate.now()) {
                    LocalTime.now()
                } else {
                    existingLocalTime
                }
                val updatedInstant = event.date.atTime(targetTime).atZone(ZoneId.systemDefault()).toInstant()
                _uiState.update { it.copy(occurredDate = event.date, occurredInstant = updatedInstant) }
            }
            is AddExpenseEvent.OnSaveClick -> {
                if (_uiState.value.entryKind == EntryKind.EXPENSE) {
                    saveExpense()
                } else {
                    saveIncome()
                }
            }
            is AddExpenseEvent.OnOpenNewCategoryDialog -> {
                _uiState.update { it.copy(showNewCategoryDialog = true) }
            }
            is AddExpenseEvent.OnDismissNewCategoryDialog -> {
                _uiState.update { it.copy(showNewCategoryDialog = false) }
            }
            is AddExpenseEvent.OnCreateCategory -> createCategory(event)
        }
    }

    private fun handleDigit(digit: Char) {
        _uiState.update { current ->
            val input = current.amountInput
            val newInput = when {
                input == "0" -> digit.toString()
                input.contains(".") -> {
                    val decimals = input.substringAfter(".")
                    if (decimals.length < 2) input + digit else input
                }
                else -> {
                    if (input.length < 9) input + digit else input
                }
            }
            val parsedMinor = parseInputToMinor(newInput)
            current.copy(amountInput = newInput, amountMinor = parsedMinor, errorMessage = null)
        }
    }

    private fun handleDecimal() {
        _uiState.update { current ->
            val input = current.amountInput
            val newInput = if (!input.contains(".")) {
                if (input.isEmpty()) "0." else "$input."
            } else {
                input
            }
            val parsedMinor = parseInputToMinor(newInput)
            current.copy(amountInput = newInput, amountMinor = parsedMinor, errorMessage = null)
        }
    }

    private fun handleBackspace() {
        _uiState.update { current ->
            val input = current.amountInput
            val newInput = if (input.length <= 1) {
                "0"
            } else {
                input.dropLast(1)
            }
            val parsedMinor = parseInputToMinor(newInput)
            current.copy(amountInput = newInput, amountMinor = parsedMinor, errorMessage = null)
        }
    }

    private fun handleClear() {
        _uiState.update { it.copy(amountInput = "0", amountMinor = 0L, errorMessage = null) }
    }

    private fun parseInputToMinor(input: String): Long {
        return try {
            val clean = if (input.endsWith(".")) input.dropLast(1) else input
            if (clean.isBlank()) 0L else Money.rupeesToPaise(clean)
        } catch (_: Exception) {
            0L
        }
    }

    private fun createCategory(event: AddExpenseEvent.OnCreateCategory) {
        viewModelScope.launch {
            val newId = manageCategoriesUseCase.addCategory(
                name = event.name.trim(),
                iconKey = event.iconKey,
                colorHex = event.colorHex,
                defaultType = event.defaultType,
            )
            _uiState.update { current ->
                current.copy(
                    showNewCategoryDialog = false,
                    selectedCategoryId = newId,
                    selectedType = if (!current.userManuallySetType) event.defaultType else current.selectedType,
                )
            }
        }
    }

    private fun saveExpense() {
        val state = _uiState.value
        if (state.amountMinor <= 0L) {
            _uiState.update { it.copy(errorMessage = "Enter an amount greater than zero") }
            return
        }
        val categoryId = state.selectedCategoryId ?: run {
            _uiState.update { it.copy(errorMessage = "Please select a category") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val now = Instant.now()
            if (state.isEditing && state.editingExpenseId != null) {
                val expense = Expense(
                    id = state.editingExpenseId,
                    amountMinor = state.amountMinor,
                    type = state.selectedType,
                    categoryId = categoryId,
                    note = state.note.takeIf { it.isNotBlank() },
                    occurredAt = state.occurredInstant,
                    occurredDate = state.occurredDate,
                    paymentMethod = state.paymentMethod,
                    createdAt = state.occurredInstant,
                    updatedAt = now,
                )
                updateExpenseUseCase(expense)
            } else {
                addExpenseUseCase(
                    amountMinor = state.amountMinor,
                    type = state.selectedType,
                    categoryId = categoryId,
                    note = state.note.takeIf { it.isNotBlank() },
                    occurredAt = state.occurredInstant,
                    paymentMethod = state.paymentMethod,
                )
            }
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }

    private fun saveIncome() {
        val state = _uiState.value
        if (state.amountMinor <= 0L) {
            _uiState.update { it.copy(errorMessage = "Enter an amount greater than zero") }
            return
        }
        val sourceId = state.selectedSourceId ?: run {
            _uiState.update { it.copy(errorMessage = "Please select an income source") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            if (state.isEditing && state.editingIncomeId != null) {
                updateIncomeUseCase(
                    id = state.editingIncomeId,
                    amountMinor = state.amountMinor,
                    sourceId = sourceId,
                    note = state.note.takeIf { it.isNotBlank() },
                    receivedAt = state.occurredInstant,
                )
            } else {
                addIncomeUseCase(
                    amountMinor = state.amountMinor,
                    sourceId = sourceId,
                    note = state.note.takeIf { it.isNotBlank() },
                    receivedAt = state.occurredInstant,
                )
            }
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
