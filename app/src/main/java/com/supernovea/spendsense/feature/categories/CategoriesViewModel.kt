/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.usecase.ManageCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val manageCategoriesUseCase: ManageCategoriesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            manageCategoriesUseCase.getCategories(includeArchived = true)
                .collect { allCategories ->
                    val active = allCategories.filter { !it.isArchived }
                    val archived = allCategories.filter { it.isArchived }
                    _uiState.value = _uiState.value.copy(
                        activeCategories = active,
                        archivedCategories = archived,
                        isLoading = false,
                    )
                }
        }
    }

    fun onEvent(event: CategoriesEvent) {
        when (event) {
            is CategoriesEvent.SelectTab -> {
                _uiState.value = _uiState.value.copy(selectedTab = event.tab)
            }
            CategoriesEvent.OpenAddSheet -> {
                _uiState.value = _uiState.value.copy(
                    isSheetVisible = true,
                    editingCategoryId = null,
                    nameInput = "",
                    selectedIconKey = "restaurant",
                    selectedColorHex = "#F59E0B",
                    selectedDefaultType = ExpenseType.NEED,
                    errorMessage = null,
                )
            }
            is CategoriesEvent.OpenEditSheet -> {
                val cat = event.category
                _uiState.value = _uiState.value.copy(
                    isSheetVisible = true,
                    editingCategoryId = cat.id,
                    nameInput = cat.name,
                    selectedIconKey = cat.iconKey,
                    selectedColorHex = cat.colorHex,
                    selectedDefaultType = cat.defaultType,
                    errorMessage = null,
                )
            }
            CategoriesEvent.DismissSheet -> {
                _uiState.value = _uiState.value.copy(
                    isSheetVisible = false,
                    editingCategoryId = null,
                    errorMessage = null,
                )
            }
            is CategoriesEvent.UpdateName -> {
                _uiState.value = _uiState.value.copy(
                    nameInput = event.name,
                    errorMessage = null,
                )
            }
            is CategoriesEvent.SelectIcon -> {
                _uiState.value = _uiState.value.copy(selectedIconKey = event.iconKey)
            }
            is CategoriesEvent.SelectColor -> {
                _uiState.value = _uiState.value.copy(selectedColorHex = event.colorHex)
            }
            is CategoriesEvent.SelectDefaultType -> {
                _uiState.value = _uiState.value.copy(selectedDefaultType = event.defaultType)
            }
            CategoriesEvent.SaveCategory -> {
                saveCategory()
            }
            is CategoriesEvent.ToggleArchive -> {
                viewModelScope.launch {
                    manageCategoriesUseCase.archiveCategory(
                        id = event.category.id,
                        isArchived = !event.category.isArchived,
                    )
                }
            }
        }
    }

    private fun saveCategory() {
        val state = _uiState.value
        val trimmed = state.nameInput.trim()
        if (trimmed.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "Category name cannot be empty")
            return
        }

        viewModelScope.launch {
            val editingId = state.editingCategoryId
            if (editingId == null || editingId == 0L) {
                // Add new category
                manageCategoriesUseCase.addCategory(
                    name = trimmed,
                    iconKey = state.selectedIconKey,
                    colorHex = state.selectedColorHex,
                    defaultType = state.selectedDefaultType,
                )
            } else {
                // Update existing category
                val existing = manageCategoriesUseCase.getById(editingId)
                if (existing != null) {
                    val updated = existing.copy(
                        name = trimmed,
                        iconKey = state.selectedIconKey,
                        colorHex = state.selectedColorHex,
                        defaultType = state.selectedDefaultType,
                    )
                    manageCategoriesUseCase.updateCategory(updated)
                }
            }

            _uiState.value = _uiState.value.copy(
                isSheetVisible = false,
                editingCategoryId = null,
                nameInput = "",
                errorMessage = null,
            )
        }
    }
}
