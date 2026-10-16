/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.Budget
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.GetBudgetStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val budgetRepository: BudgetRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private var statusObservationJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.observeSettings().collectLatest { settings ->
                _uiState.update { it.copy(currencySymbol = settings.currencySymbol) }
            }
        }
        loadMonth(_uiState.value.selectedMonth)
    }

    fun onEvent(event: BudgetEvent) {
        when (event) {
            BudgetEvent.PreviousMonth -> {
                val prev = _uiState.value.selectedMonth.minusMonths(1)
                loadMonth(prev)
            }
            BudgetEvent.NextMonth -> {
                val next = _uiState.value.selectedMonth.plusMonths(1)
                loadMonth(next)
            }
            is BudgetEvent.SelectMonth -> {
                loadMonth(event.month)
            }
            is BudgetEvent.TotalLimitChanged -> {
                _uiState.update { it.copy(totalLimitInput = event.input, errorMessage = null) }
            }
            is BudgetEvent.WasteLimitChanged -> {
                _uiState.update { it.copy(wasteLimitInput = event.input, errorMessage = null) }
            }
            BudgetEvent.SaveBudget -> saveBudget()
            BudgetEvent.DeleteBudget -> deleteBudget()
            BudgetEvent.DismissMessage -> {
                _uiState.update { it.copy(userMessage = null, errorMessage = null) }
            }
        }
    }

    private fun loadMonth(month: YearMonth) {
        _uiState.update {
            it.copy(
                selectedMonth = month,
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            val existing = budgetRepository.getByMonth(month.toString())
            _uiState.update {
                it.copy(
                    totalLimitInput = existing?.let { b -> Money.toInputString(b.totalLimitMinor) } ?: "",
                    wasteLimitInput = existing?.wasteLimitMinor?.let { w -> Money.toInputString(w) } ?: "",
                )
            }
        }

        statusObservationJob?.cancel()
        statusObservationJob = viewModelScope.launch {
            getBudgetStatusUseCase(month).collectLatest { status ->
                _uiState.update { it.copy(status = status) }
            }
        }
    }

    private fun saveBudget() {
        val totalMinor = Money.parseToMinor(_uiState.value.totalLimitInput)
        if (totalMinor == null || totalMinor <= 0L) {
            _uiState.update { it.copy(errorMessage = "Please enter a total budget greater than 0") }
            return
        }

        val wasteTrimmed = _uiState.value.wasteLimitInput.trim()
        val wasteMinor = if (wasteTrimmed.isNotEmpty()) {
            Money.parseToMinor(wasteTrimmed, allowZero = true)
        } else {
            null
        }

        if (wasteTrimmed.isNotEmpty()) {
            if (wasteMinor == null || wasteMinor < 0L || wasteMinor > totalMinor) {
                _uiState.update { it.copy(errorMessage = "Waste limit cannot exceed total budget") }
                return
            }
        }

        viewModelScope.launch {
            try {
                val budget = Budget(
                    monthKey = _uiState.value.selectedMonth.toString(),
                    totalLimitMinor = totalMinor,
                    wasteLimitMinor = wasteMinor,
                )
                getBudgetStatusUseCase.setBudget(budget)
                _uiState.update {
                    it.copy(
                        userMessage = "Budget saved",
                        errorMessage = null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Failed to save budget") }
            }
        }
    }

    private fun deleteBudget() {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(_uiState.value.selectedMonth.toString())
                _uiState.update {
                    it.copy(
                        totalLimitInput = "",
                        wasteLimitInput = "",
                        userMessage = "Budget removed",
                        errorMessage = null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Failed to remove budget") }
            }
        }
    }
}
