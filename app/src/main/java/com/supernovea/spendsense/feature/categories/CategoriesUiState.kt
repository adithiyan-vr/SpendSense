/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.categories

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType

enum class CategoriesTab {
    ACTIVE,
    ARCHIVED,
}

data class CategoriesUiState(
    val activeCategories: List<Category> = emptyList(),
    val archivedCategories: List<Category> = emptyList(),
    val selectedTab: CategoriesTab = CategoriesTab.ACTIVE,
    val isSheetVisible: Boolean = false,
    val editingCategoryId: Long? = null,
    val nameInput: String = "",
    val selectedIconKey: String = "restaurant",
    val selectedColorHex: String = "#F59E0B",
    val selectedDefaultType: ExpenseType = ExpenseType.NEED,
    val errorMessage: String? = null,
    val isLoading: Boolean = true,
)
