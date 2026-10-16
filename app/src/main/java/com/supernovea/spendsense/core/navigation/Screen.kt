/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.supernovea.spendsense.R

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object History : Screen("history")
    data object Analysis : Screen("analysis")
    data object Settings : Screen("settings")
    data object Categories : Screen("categories")
    data object Budget : Screen("budget")
    data object PrivacySecurity : Screen("privacy_security")
    data object Onboarding : Screen("onboarding")
    data object AddExpense : Screen("add_expense?expenseId={expenseId}&incomeId={incomeId}&initialTab={initialTab}") {
        const val ARG_EXPENSE_ID = "expenseId"
        const val ARG_INCOME_ID = "incomeId"
        const val ARG_INITIAL_TAB = "initialTab"

        fun createRoute(
            expenseId: Long? = null,
            incomeId: Long? = null,
            initialTab: String? = null,
        ): String {
            val params = mutableListOf<String>()
            if (expenseId != null && expenseId > 0) params.add("$ARG_EXPENSE_ID=$expenseId")
            if (incomeId != null && incomeId > 0) params.add("$ARG_INCOME_ID=$incomeId")
            if (!initialTab.isNullOrBlank()) params.add("$ARG_INITIAL_TAB=$initialTab")
            return if (params.isNotEmpty()) {
                "add_expense?${params.joinToString("&")}"
            } else {
                "add_expense"
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    @StringRes val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Home : BottomNavItem(
        route = Screen.Home.route,
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    data object History : BottomNavItem(
        route = Screen.History.route,
        labelResId = R.string.nav_history,
        selectedIcon = Icons.AutoMirrored.Filled.ReceiptLong,
        unselectedIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
    )

    data object Analysis : BottomNavItem(
        route = Screen.Analysis.route,
        labelResId = R.string.nav_analysis,
        selectedIcon = Icons.Filled.PieChart,
        unselectedIcon = Icons.Outlined.PieChart,
    )

    data object Settings : BottomNavItem(
        route = Screen.Settings.route,
        labelResId = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    )

    companion object {
        val items = listOf(Home, History, Analysis, Settings)
    }
}
