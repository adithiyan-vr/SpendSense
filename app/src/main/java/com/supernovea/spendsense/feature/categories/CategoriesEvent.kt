/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.categories

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType

sealed interface CategoriesEvent {
    data class SelectTab(val tab: CategoriesTab) : CategoriesEvent
    data object OpenAddSheet : CategoriesEvent
    data class OpenEditSheet(val category: Category) : CategoriesEvent
    data object DismissSheet : CategoriesEvent
    data class UpdateName(val name: String) : CategoriesEvent
    data class SelectIcon(val iconKey: String) : CategoriesEvent
    data class SelectColor(val colorHex: String) : CategoriesEvent
    data class SelectDefaultType(val defaultType: ExpenseType) : CategoriesEvent
    data object SaveCategory : CategoriesEvent
    data class ToggleArchive(val category: Category) : CategoriesEvent
}
