/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.addexpense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.ui.CategoryIconProvider
import com.supernovea.spendsense.core.ui.GroupedCategoryBottomSheet
import com.supernovea.spendsense.core.ui.NumericKeypad
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.model.PaymentMethod
import com.supernovea.spendsense.domain.model.UserProfileType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onNavigateBack: () -> Unit,
    onExpenseSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddExpenseViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showCategoryBottomSheet by remember { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onExpenseSaved()
        }
    }

    val screenTitle = when {
        state.isEditing && state.entryKind == EntryKind.INCOME -> stringResource(R.string.edit_income_title)
        state.isEditing -> stringResource(R.string.edit_expense_title)
        state.entryKind == EntryKind.INCOME && state.userProfile == UserProfileType.STUDENT ->
            stringResource(R.string.add_pocket_money_title)
        state.entryKind == EntryKind.INCOME -> stringResource(R.string.add_income_title)
        else -> stringResource(R.string.add_expense_title)
    }

    val activePrimaryColor = if (state.entryKind == EntryKind.INCOME) {
        MaterialTheme.semantic.income
    } else {
        MaterialTheme.colorScheme.primary
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.cd_close),
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.onEvent(AddExpenseEvent.OnSaveClick) },
                        enabled = state.isValid && !state.isLoading,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activePrimaryColor,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        ),
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_save),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented toggle when creating new transaction (Expense vs Income)
            if (!state.isEditing) {
                Spacer(modifier = Modifier.height(4.dp))
                EntryKindSegmentedControl(
                    selectedKind = state.entryKind,
                    onKindSelected = { viewModel.onEvent(AddExpenseEvent.OnEntryKindSelected(it)) },
                    incomeLabel = if (state.userProfile == UserProfileType.STUDENT) {
                        stringResource(R.string.shortcut_add_income_short)
                    } else {
                        stringResource(R.string.tab_income)
                    }
                )
            }

            // Scrollable upper section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Large Amount Display
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = state.currencySymbol + " ",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = activePrimaryColor,
                    )
                    Text(
                        text = state.amountInput,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.entryKind == EntryKind.EXPENSE) {
                    // Type Toggle (Need vs Waste)
                    TypeToggle(
                        selectedType = state.selectedType,
                        onTypeSelected = { viewModel.onEvent(AddExpenseEvent.OnTypeSelected(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Decision Test Prompt Helper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.decision_test_helper),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Expense Category Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(id = R.string.category_section_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            TextButton(
                                onClick = { showCategoryBottomSheet = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(id = R.string.category_browse_all, state.categories.size),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        val quickCategories = remember(state.categories, state.selectedCategoryId) {
                            val selected = state.categories.find { it.id == state.selectedCategoryId }
                            val others = state.categories.filter { it.id != state.selectedCategoryId }
                            if (selected != null) listOf(selected) + others.take(9) else others.take(10)
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            items(quickCategories, key = { it.id }) { category ->
                                val isSelected = category.id == state.selectedCategoryId
                                CategoryChip(
                                    category = category,
                                    isSelected = isSelected,
                                    onClick = { viewModel.onEvent(AddExpenseEvent.OnCategorySelected(category)) },
                                )
                            }

                            item {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(role = Role.Button) {
                                            viewModel.onEvent(AddExpenseEvent.OnOpenNewCategoryDialog)
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = stringResource(id = R.string.category_add_new),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Income Source Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(id = R.string.income_source_label),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            items(state.incomeSources, key = { it.id }) { source ->
                                val isSelected = source.id == state.selectedSourceId
                                IncomeSourceChip(
                                    source = source,
                                    isSelected = isSelected,
                                    onClick = { viewModel.onEvent(AddExpenseEvent.OnIncomeSourceSelected(source)) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Note Field
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { viewModel.onEvent(AddExpenseEvent.OnNoteChanged(it)) },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.note_hint),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = activePrimaryColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    ),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date & Details Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Date Selector Chip
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDatePickerDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = stringResource(id = R.string.date_label),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val isToday = state.occurredDate == LocalDate.now()
                            val isYesterday = state.occurredDate == LocalDate.now().minusDays(1)
                            val dateLabel = when {
                                isToday -> stringResource(id = R.string.date_today)
                                isYesterday -> stringResource(id = R.string.date_yesterday)
                                else -> state.occurredDate.format(DateTimeFormatter.ofPattern("dd MMM"))
                            }
                            Text(
                                text = dateLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    // Payment Method Chips (only for Expense mode)
                    if (state.entryKind == EntryKind.EXPENSE) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PaymentMethod.entries.forEach { method ->
                                val isSelected = method == state.paymentMethod
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.onEvent(AddExpenseEvent.OnPaymentMethodSelected(method)) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    val methodLabel = when (method) {
                                        PaymentMethod.UPI -> stringResource(id = R.string.payment_upi)
                                        PaymentMethod.CASH -> stringResource(id = R.string.payment_cash)
                                        PaymentMethod.CARD -> stringResource(id = R.string.payment_card)
                                        PaymentMethod.OTHER -> stringResource(id = R.string.payment_other)
                                    }
                                    Text(
                                        text = methodLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Numeric Keypad at bottom
            Surface(
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
            ) {
                NumericKeypad(
                    onDigitClick = { viewModel.onEvent(AddExpenseEvent.OnDigitClick(it)) },
                    onDecimalClick = { viewModel.onEvent(AddExpenseEvent.OnDecimalClick) },
                    onBackspaceClick = { viewModel.onEvent(AddExpenseEvent.OnBackspaceClick) },
                    onClearClick = { viewModel.onEvent(AddExpenseEvent.OnClearClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.occurredDate
                .atStartOfDay(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        viewModel.onEvent(AddExpenseEvent.OnDateSelected(selected))
                    }
                    showDatePickerDialog = false
                }) {
                    Text(text = stringResource(id = R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Grouped Category Selection Bottom Sheet
    if (showCategoryBottomSheet) {
        GroupedCategoryBottomSheet(
            onDismissRequest = { showCategoryBottomSheet = false },
            sheetState = categorySheetState,
            categories = state.categories,
            selectedCategoryId = state.selectedCategoryId,
            onCategorySelected = { category ->
                viewModel.onEvent(AddExpenseEvent.OnCategorySelected(category))
                showCategoryBottomSheet = false
            },
            onAddNewCategory = {
                showCategoryBottomSheet = false
                viewModel.onEvent(AddExpenseEvent.OnOpenNewCategoryDialog)
            },
        )
    }

    // New Category Dialog
    if (state.showNewCategoryDialog) {
        NewCategoryDialog(
            onDismiss = { viewModel.onEvent(AddExpenseEvent.OnDismissNewCategoryDialog) },
            onConfirm = { name, defaultType ->
                viewModel.onEvent(AddExpenseEvent.OnCreateCategory(name = name, defaultType = defaultType))
            }
        )
    }
}

@Composable
private fun EntryKindSegmentedControl(
    selectedKind: EntryKind,
    onKindSelected: (EntryKind) -> Unit,
    modifier: Modifier = Modifier,
    incomeLabel: String = stringResource(R.string.tab_income),
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val isExpense = selectedKind == EntryKind.EXPENSE
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .clickable { onKindSelected(EntryKind.EXPENSE) },
                shape = RoundedCornerShape(11.dp),
                color = if (isExpense) MaterialTheme.colorScheme.surface else Color.Transparent,
                tonalElevation = if (isExpense) 3.dp else 0.dp,
            ) {
                Text(
                    text = stringResource(id = R.string.tab_expense),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium,
                    color = if (isExpense) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            val isIncome = selectedKind == EntryKind.INCOME
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .clickable { onKindSelected(EntryKind.INCOME) },
                shape = RoundedCornerShape(11.dp),
                color = if (isIncome) MaterialTheme.colorScheme.surface else Color.Transparent,
                tonalElevation = if (isIncome) 3.dp else 0.dp,
            ) {
                Text(
                    text = incomeLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                    color = if (isIncome) MaterialTheme.semantic.income else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun IncomeSourceChip(
    source: IncomeSource,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = CategoryIconProvider.getIcon(source.iconKey)
    val incomeColor = MaterialTheme.semantic.income
    val incomeContainer = MaterialTheme.semantic.incomeContainer

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(1.5.dp, incomeColor, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) incomeContainer else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) incomeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = source.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TypeToggle(
    selectedType: ExpenseType,
    onTypeSelected: (ExpenseType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val isNeed = selectedType == ExpenseType.NEED
        val isWaste = selectedType == ExpenseType.WASTE

        // Need Button
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onTypeSelected(ExpenseType.NEED) }
                .then(
                    if (isNeed) {
                        Modifier.border(2.dp, MaterialTheme.semantic.need, RoundedCornerShape(16.dp))
                    } else {
                        Modifier
                    }
                ),
            shape = RoundedCornerShape(16.dp),
            color = if (isNeed) MaterialTheme.semantic.needContainer else MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (isNeed) MaterialTheme.semantic.need else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.type_need),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isNeed) FontWeight.Bold else FontWeight.Medium,
                    color = if (isNeed) MaterialTheme.semantic.need else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Waste Button
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onTypeSelected(ExpenseType.WASTE) }
                .then(
                    if (isWaste) {
                        Modifier.border(2.dp, MaterialTheme.semantic.waste, RoundedCornerShape(16.dp))
                    } else {
                        Modifier
                    }
                ),
            shape = RoundedCornerShape(16.dp),
            color = if (isWaste) MaterialTheme.semantic.wasteContainer else MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isWaste) MaterialTheme.semantic.waste else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.type_waste),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isWaste) FontWeight.Bold else FontWeight.Medium,
                    color = if (isWaste) MaterialTheme.semantic.waste else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryColor = CategoryIconProvider.parseColorHex(category.colorHex)
    val categoryIcon = CategoryIconProvider.getIcon(category.iconKey)

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(1.5.dp, categoryColor, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) categoryColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = null,
                tint = if (isSelected) categoryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NewCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, defaultType: ExpenseType) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var defaultType by remember { mutableStateOf(ExpenseType.NEED) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.new_category_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(text = stringResource(id = R.string.category_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "Default Type",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { defaultType = ExpenseType.NEED },
                        color = if (defaultType == ExpenseType.NEED) MaterialTheme.semantic.needContainer else MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Text(
                            text = stringResource(id = R.string.type_need),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (defaultType == ExpenseType.NEED) MaterialTheme.semantic.need else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { defaultType = ExpenseType.WASTE },
                        color = if (defaultType == ExpenseType.WASTE) MaterialTheme.semantic.wasteContainer else MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Text(
                            text = stringResource(id = R.string.type_waste),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (defaultType == ExpenseType.WASTE) MaterialTheme.semantic.waste else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), defaultType) },
                enabled = name.isNotBlank(),
            ) {
                Text(text = stringResource(id = R.string.save_category))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        }
    )
}
