/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.core.backup.CsvExporter
import com.supernovea.spendsense.core.backup.ImportMode
import com.supernovea.spendsense.core.backup.JsonBackupManager
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.feature.analysis.export.CsvReportGenerator
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.DeleteAllDataUseCase
import com.supernovea.spendsense.work.WorkScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val deleteAllDataUseCase: DeleteAllDataUseCase,
    private val jsonBackupManager: JsonBackupManager,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val workScheduler: WorkScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.observeSettings().collectLatest { settings ->
                _uiState.update { current ->
                    current.copy(
                        currencySymbol = settings.currencySymbol,
                        weekStartDay = settings.weekStartDay,
                        themeMode = settings.themeMode,
                        pureBlack = settings.pureBlack,
                        dynamicColor = settings.dynamicColor,
                        dailyReminderEnabled = settings.reminderEnabled,
                        weeklySummaryEnabled = settings.weeklySummaryEnabled,
                        monthlySummaryEnabled = settings.monthlySummaryEnabled,
                        lastBackupEpochMillis = settings.lastBackupEpochMillis,
                    )
                }
            }
        }
    }

    suspend fun getCsvContent(): String {
        val expenses = expenseRepository.observeAll().first()
        val incomes = incomeRepository.observeAll().first()
        return CsvReportGenerator.generate(expenses, incomes, includeNotes = true)
    }

    suspend fun getJsonBackupContent(): String {
        return jsonBackupManager.exportJson()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ChangeCurrencySymbol -> {
                viewModelScope.launch {
                    settingsRepository.setCurrencySymbol(event.symbol)
                }
            }
            is SettingsEvent.ChangeWeekStartDay -> {
                viewModelScope.launch {
                    settingsRepository.setWeekStartDay(event.day)
                }
            }
            is SettingsEvent.ChangeThemeMode -> {
                viewModelScope.launch {
                    settingsRepository.setThemeMode(event.mode)
                }
            }
            is SettingsEvent.TogglePureBlack -> {
                viewModelScope.launch {
                    settingsRepository.setPureBlack(event.enabled)
                }
            }
            is SettingsEvent.ToggleDynamicColor -> {
                viewModelScope.launch {
                    settingsRepository.setDynamicColor(event.enabled)
                }
            }
            is SettingsEvent.ToggleDailyReminder -> {
                viewModelScope.launch {
                    settingsRepository.setReminderEnabled(event.enabled)
                    val settings = settingsRepository.getSettings().copy(reminderEnabled = event.enabled)
                    workScheduler.scheduleAll(settings)
                }
            }
            is SettingsEvent.ToggleWeeklySummary -> {
                viewModelScope.launch {
                    settingsRepository.setWeeklySummaryEnabled(event.enabled)
                    val settings = settingsRepository.getSettings().copy(weeklySummaryEnabled = event.enabled)
                    workScheduler.scheduleAll(settings)
                }
            }
            is SettingsEvent.ToggleMonthlySummary -> {
                viewModelScope.launch {
                    settingsRepository.setMonthlySummaryEnabled(event.enabled)
                    val settings = settingsRepository.getSettings().copy(monthlySummaryEnabled = event.enabled)
                    workScheduler.scheduleAll(settings)
                }
            }
            SettingsEvent.RequestDeleteAllData -> {
                _uiState.update { it.copy(showDeleteAllDialog = true) }
            }
            SettingsEvent.CancelDeleteAllData -> {
                _uiState.update { it.copy(showDeleteAllDialog = false) }
            }
            SettingsEvent.ConfirmDeleteAllData -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isDeletingData = true, showDeleteAllDialog = false) }
                    try {
                        deleteAllDataUseCase()
                        _uiState.update {
                            it.copy(
                                isDeletingData = false,
                                userMessage = "All data has been reset",
                                errorMessage = null,
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(
                                isDeletingData = false,
                                errorMessage = e.message ?: "Failed to reset data",
                            )
                        }
                    }
                }
            }
            is SettingsEvent.ImportFileSelected -> {
                _uiState.update {
                    it.copy(
                        pendingImportJson = event.jsonContent,
                        showImportDialog = true,
                    )
                }
            }
            is SettingsEvent.ConfirmImport -> {
                val jsonToImport = _uiState.value.pendingImportJson
                if (jsonToImport != null) {
                    viewModelScope.launch {
                        _uiState.update { it.copy(isExportingOrImporting = true, showImportDialog = false) }
                        try {
                            val result = jsonBackupManager.importJson(jsonToImport, event.mode)
                            _uiState.update {
                                it.copy(
                                    isExportingOrImporting = false,
                                    pendingImportJson = null,
                                    userMessage = "Backup restored successfully (${result.expensesRestoredCount} expenses restored)",
                                    errorMessage = null,
                                )
                            }
                        } catch (e: Exception) {
                            _uiState.update {
                                it.copy(
                                    isExportingOrImporting = false,
                                    pendingImportJson = null,
                                    errorMessage = e.message ?: "Failed to restore backup",
                                )
                            }
                        }
                    }
                } else {
                    _uiState.update { it.copy(showImportDialog = false) }
                }
            }
            SettingsEvent.CancelImport -> {
                _uiState.update { it.copy(showImportDialog = false, pendingImportJson = null) }
            }
            SettingsEvent.ExportCsvSuccess -> {
                _uiState.update { it.copy(userMessage = "Expenses exported to CSV successfully") }
            }
            SettingsEvent.ExportJsonSuccess -> {
                _uiState.update { it.copy(userMessage = "Backup exported to JSON successfully") }
            }
            is SettingsEvent.ExportFailed -> {
                _uiState.update { it.copy(errorMessage = event.message) }
            }
            SettingsEvent.DismissMessage -> {
                _uiState.update { it.copy(userMessage = null, errorMessage = null) }
            }
        }
    }
}
