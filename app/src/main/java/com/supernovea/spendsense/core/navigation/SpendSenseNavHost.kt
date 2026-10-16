/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.feature.addexpense.AddExpenseScreen
import com.supernovea.spendsense.feature.analysis.AnalysisScreen
import com.supernovea.spendsense.feature.budget.BudgetScreen
import com.supernovea.spendsense.feature.categories.CategoriesScreen
import com.supernovea.spendsense.feature.history.HistoryScreen
import com.supernovea.spendsense.feature.home.HomeScreen
import com.supernovea.spendsense.feature.onboarding.OnboardingScreen
import com.supernovea.spendsense.feature.settings.PrivacySecurityScreen
import com.supernovea.spendsense.feature.settings.SettingsScreen

@Composable
fun SpendSenseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onboardingDone: Boolean = true,
    initialShortcutAction: String? = null,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(initialShortcutAction) {
        when (initialShortcutAction) {
            "add_expense" -> navController.navigate(Screen.AddExpense.createRoute(initialTab = "expense"))
            "add_income" -> navController.navigate(Screen.AddExpense.createRoute(initialTab = "income"))
        }
    }

    val hideBottomBar = currentRoute?.startsWith("add_expense") == true ||
        currentRoute == Screen.Categories.route ||
        currentRoute == Screen.Budget.route ||
        currentRoute == Screen.PrivacySecurity.route ||
        currentRoute == Screen.Onboarding.route

    var isSpeedDialExpanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        isSpeedDialExpanded = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (!hideBottomBar) {
                    SpendSenseBottomBar(
                        navController = navController,
                        currentRoute = currentRoute,
                        isSpeedDialExpanded = isSpeedDialExpanded,
                        onToggleSpeedDial = { isSpeedDialExpanded = !isSpeedDialExpanded },
                        onAddExpense = {
                            isSpeedDialExpanded = false
                            navController.navigate(Screen.AddExpense.createRoute(initialTab = "expense"))
                        },
                        onAddIncome = {
                            isSpeedDialExpanded = false
                            navController.navigate(Screen.AddExpense.createRoute(initialTab = "income"))
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (onboardingDone) Screen.Home.route else Screen.Onboarding.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onFinished = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToAddExpense = {
                            navController.navigate(Screen.AddExpense.createRoute(initialTab = "expense"))
                        },
                        onNavigateToHistory = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onExpenseClick = { expenseId ->
                            navController.navigate(Screen.AddExpense.createRoute(expenseId = expenseId))
                        },
                        onIncomeClick = { incomeId ->
                            navController.navigate(Screen.AddExpense.createRoute(incomeId = incomeId))
                        }
                    )
                }

                composable(Screen.History.route) {
                    HistoryScreen(
                        onExpenseClick = { expenseId ->
                            navController.navigate(Screen.AddExpense.createRoute(expenseId = expenseId))
                        },
                        onIncomeClick = { incomeId ->
                            navController.navigate(Screen.AddExpense.createRoute(incomeId = incomeId))
                        },
                        onNavigateToAddExpense = {
                            navController.navigate(Screen.AddExpense.createRoute(initialTab = "expense"))
                        }
                    )
                }

                composable(Screen.Analysis.route) {
                    AnalysisScreen()
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onNavigateToCategories = {
                            navController.navigate(Screen.Categories.route)
                        },
                        onNavigateToBudget = {
                            navController.navigate(Screen.Budget.route)
                        },
                        onNavigateToPrivacySecurity = {
                            navController.navigate(Screen.PrivacySecurity.route)
                        },
                    )
                }

                composable(Screen.PrivacySecurity.route) {
                    PrivacySecurityScreen(
                        onNavigateBack = { navController.popBackStack() },
                    )
                }

                composable(Screen.Categories.route) {
                    CategoriesScreen(
                        onNavigateBack = { navController.popBackStack() },
                    )
                }

                composable(Screen.Budget.route) {
                    BudgetScreen(
                        onNavigateBack = { navController.popBackStack() },
                    )
                }

                composable(
                    route = Screen.AddExpense.route,
                    arguments = listOf(
                        navArgument(Screen.AddExpense.ARG_EXPENSE_ID) {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument(Screen.AddExpense.ARG_INCOME_ID) {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument(Screen.AddExpense.ARG_INITIAL_TAB) {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) {
                    AddExpenseScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onExpenseSaved = { navController.popBackStack() },
                    )
                }
            }
        }

        // Speed Dial Scrim & Actions Overlay
        if (isSpeedDialExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { isSpeedDialExpanded = false }
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 96.dp)
                ) {
                    AnimatedVisibility(
                        visible = isSpeedDialExpanded,
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut() + slideOutVertically { it / 2 }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Add Income Speed Dial Action
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        isSpeedDialExpanded = false
                                        navController.navigate(Screen.AddExpense.createRoute(initialTab = "income"))
                                    }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 4.dp
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.speed_dial_income),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.semantic.income,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                SmallFloatingActionButton(
                                    onClick = {
                                        isSpeedDialExpanded = false
                                        navController.navigate(Screen.AddExpense.createRoute(initialTab = "income"))
                                    },
                                    containerColor = MaterialTheme.semantic.incomeContainer,
                                    contentColor = MaterialTheme.semantic.income,
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = stringResource(id = R.string.speed_dial_income),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Add Expense Speed Dial Action
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        isSpeedDialExpanded = false
                                        navController.navigate(Screen.AddExpense.createRoute(initialTab = "expense"))
                                    }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 4.dp
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.speed_dial_expense),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                SmallFloatingActionButton(
                                    onClick = {
                                        isSpeedDialExpanded = false
                                        navController.navigate(Screen.AddExpense.createRoute(initialTab = "expense"))
                                    },
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = stringResource(id = R.string.speed_dial_expense),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpendSenseBottomBar(
    navController: NavHostController,
    currentRoute: String?,
    isSpeedDialExpanded: Boolean,
    onToggleSpeedDial: () -> Unit,
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
) {
    val fabRotation by animateFloatAsState(
        targetValue = if (isSpeedDialExpanded) 45f else 0f,
        label = "FabRotation"
    )

    Box(contentAlignment = Alignment.BottomCenter) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
        ) {
            // First 2 items (Home, History)
            val firstHalf = BottomNavItem.items.take(2)
            firstHalf.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = stringResource(id = item.labelResId),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = item.labelResId),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                )
            }

            // Center Spacer for FAB
            NavigationBarItem(
                selected = false,
                onClick = onToggleSpeedDial,
                icon = { Box(modifier = Modifier.size(24.dp)) },
                label = { Text(text = "") },
                enabled = false,
            )

            // Second 2 items (Analysis, Settings)
            val secondHalf = BottomNavItem.items.drop(2)
            secondHalf.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = stringResource(id = item.labelResId),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = item.labelResId),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                )
            }
        }

        // Elevated Center FAB with animated rotation for Speed Dial
        FloatingActionButton(
            onClick = onToggleSpeedDial,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .offset(y = (-24).dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.nav_add_expense),
                modifier = Modifier
                    .size(28.dp)
                    .rotate(fabRotation)
            )
        }
    }
}
