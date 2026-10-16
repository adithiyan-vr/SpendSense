/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.ui.DayHeader
import com.supernovea.spendsense.core.ui.EmptyState
import com.supernovea.spendsense.core.ui.ExpenseRow
import com.supernovea.spendsense.core.ui.IncomeRow
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.model.PaymentMethod

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    onExpenseClick: (Long) -> Unit,
    onNavigateToAddExpense: () -> Unit,
    modifier: Modifier = Modifier,
    onIncomeClick: (Long) -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val undoLabel = stringResource(id = R.string.action_undo)
    LaunchedEffect(state.snackbarMessage) {
        val message = state.snackbarMessage
        if (message != null) {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (state.canUndoDelete) undoLabel else null,
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.onEvent(HistoryEvent.OnUndoDelete)
            } else {
                viewModel.onEvent(HistoryEvent.OnDismissSnackbar)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header: Title & Tab Switcher & Search Field
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.history_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // History Tabs: All / Expenses / Income
                HistoryTabSegmentedControl(
                    selectedTab = state.selectedTab,
                    onTabSelected = { viewModel.onEvent(HistoryEvent.OnTabSelected(it)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(HistoryEvent.OnSearchQueryChanged(it)) },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.history_search_hint),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.cd_search),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onEvent(HistoryEvent.OnSearchQueryChanged("")) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(id = R.string.cd_clear_search),
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                // Filter Chips Row (only relevant when viewing Expenses or All)
                if (state.selectedTab != HistoryTab.INCOME) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // All Chip
                        FilterChip(
                            selected = state.selectedTypeFilter == null,
                            onClick = { viewModel.onEvent(HistoryEvent.OnTypeFilterSelected(null)) },
                            label = { Text(text = stringResource(id = R.string.history_filter_all)) },
                            shape = RoundedCornerShape(12.dp),
                        )

                        // Need Chip
                        FilterChip(
                            selected = state.selectedTypeFilter == ExpenseType.NEED,
                            onClick = {
                                val newType = if (state.selectedTypeFilter == ExpenseType.NEED) null else ExpenseType.NEED
                                viewModel.onEvent(HistoryEvent.OnTypeFilterSelected(newType))
                            },
                            label = { Text(text = stringResource(id = R.string.history_filter_need)) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.semantic.needContainer,
                                selectedLabelColor = MaterialTheme.semantic.need,
                            ),
                        )

                        // Waste Chip
                        FilterChip(
                            selected = state.selectedTypeFilter == ExpenseType.WASTE,
                            onClick = {
                                val newType = if (state.selectedTypeFilter == ExpenseType.WASTE) null else ExpenseType.WASTE
                                viewModel.onEvent(HistoryEvent.OnTypeFilterSelected(newType))
                            },
                            label = { Text(text = stringResource(id = R.string.history_filter_waste)) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.semantic.wasteContainer,
                                selectedLabelColor = MaterialTheme.semantic.waste,
                            ),
                        )

                        // Payment Method filter chips
                        PaymentMethod.entries.forEach { method ->
                            val isSelected = state.selectedPaymentMethod == method
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val next = if (isSelected) null else method
                                    viewModel.onEvent(HistoryEvent.OnPaymentFilterSelected(next))
                                },
                                label = { Text(text = method.name) },
                                shape = RoundedCornerShape(12.dp),
                            )
                        }
                    }
                }
            }

            // Body: Loading, Empty, or List
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.dayGroups.isEmpty() -> {
                    if (state.totalItemCount == 0) {
                        EmptyState(
                            icon = Icons.AutoMirrored.Filled.ReceiptLong,
                            title = stringResource(id = R.string.home_empty_title),
                            subtitle = stringResource(id = R.string.home_empty_subtitle),
                            actionLabel = stringResource(id = R.string.home_add_first_expense),
                            onActionClick = onNavigateToAddExpense,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        EmptyState(
                            icon = Icons.Default.Search,
                            title = stringResource(id = R.string.history_empty_title),
                            subtitle = stringResource(id = R.string.history_empty_subtitle),
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                    ) {
                        state.dayGroups.forEach { dayGroup ->
                            stickyHeader(key = "header_${dayGroup.date}") {
                                DayHeader(
                                    date = dayGroup.date,
                                    totalMinor = dayGroup.spentMinor,
                                    needMinor = dayGroup.needMinor,
                                    wasteMinor = dayGroup.wasteMinor,
                                    receivedMinor = dayGroup.receivedMinor,
                                )
                            }

                            items(
                                items = dayGroup.items,
                                key = { "${it::class.simpleName}_${it.id}" }
                            ) { item ->
                                when (item) {
                                    is HistoryTransaction.ExpenseItem -> {
                                        DismissibleExpenseRow(
                                            item = item.expenseWithCategory,
                                            onClick = { onExpenseClick(item.id) },
                                            onDelete = { viewModel.onEvent(HistoryEvent.OnDeleteTransaction(item)) },
                                        )
                                    }
                                    is HistoryTransaction.IncomeItem -> {
                                        DismissibleIncomeRow(
                                            item = item.incomeWithSource,
                                            onClick = { onIncomeClick(item.id) },
                                            onDelete = { viewModel.onEvent(HistoryEvent.OnDeleteTransaction(item)) },
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
}

@Composable
private fun HistoryTabSegmentedControl(
    selectedTab: HistoryTab,
    onTabSelected: (HistoryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            HistoryTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val label = when (tab) {
                    HistoryTab.ALL -> stringResource(R.string.history_tab_all)
                    HistoryTab.EXPENSES -> stringResource(R.string.history_tab_expenses)
                    HistoryTab.INCOME -> stringResource(R.string.history_tab_income)
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .clickable { onTabSelected(tab) },
                    shape = RoundedCornerShape(11.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                    tonalElevation = if (isSelected) 3.dp else 0.dp,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                            isSelected && tab == HistoryTab.INCOME -> MaterialTheme.semantic.income
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissibleExpenseRow(
    item: ExpenseWithCategory,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color.Transparent
                },
                label = "swipeDismissColor"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.action_delete),
                            tint = MaterialTheme.colorScheme.onError,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.action_delete),
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        },
        content = {
            ExpenseRow(
                expenseWithCategory = item,
                onClick = onClick,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissibleIncomeRow(
    item: IncomeWithSource,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color.Transparent
                },
                label = "swipeDismissIncomeColor"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.action_delete),
                            tint = MaterialTheme.colorScheme.onError,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.action_delete),
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        },
        content = {
            IncomeRow(
                incomeWithSource = item,
                onClick = onClick,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            )
        }
    )
}
