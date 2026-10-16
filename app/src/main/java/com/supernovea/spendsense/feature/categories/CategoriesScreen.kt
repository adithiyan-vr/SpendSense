/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.categories

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.ui.CategoryIconProvider
import com.supernovea.spendsense.core.ui.EmptyState
import com.supernovea.spendsense.core.ui.NeedWasteBadge
import com.supernovea.spendsense.core.ui.categoryGroupTitle
import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.CategoryGroup
import com.supernovea.spendsense.domain.model.ExpenseType

private val AVAILABLE_ICONS = listOf(
    "restaurant", "shopping_basket", "directions_bus", "school",
    "home", "receipt_long", "medical_services", "fastfood",
    "local_dining", "movie", "sports_esports", "shopping_bag",
    "subscriptions"
)

private val CURATED_COLORS = listOf(
    "#F59E0B", "#3B82F6", "#10B981", "#8B5CF6",
    "#EC4899", "#EF4444", "#14B8A6", "#F97316",
    "#6366F1", "#06B6D4"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.categories_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_back),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onEvent(CategoriesEvent.OpenAddSheet) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.categories_add_category),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Tabs: Active vs Archived
            TabRow(
                selectedTabIndex = if (uiState.selectedTab == CategoriesTab.ACTIVE) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Tab(
                    selected = uiState.selectedTab == CategoriesTab.ACTIVE,
                    onClick = { viewModel.onEvent(CategoriesEvent.SelectTab(CategoriesTab.ACTIVE)) },
                    text = {
                        Text(
                            text = "${stringResource(id = R.string.categories_tab_active)} (${uiState.activeCategories.size})",
                            fontWeight = if (uiState.selectedTab == CategoriesTab.ACTIVE) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                )
                Tab(
                    selected = uiState.selectedTab == CategoriesTab.ARCHIVED,
                    onClick = { viewModel.onEvent(CategoriesEvent.SelectTab(CategoriesTab.ARCHIVED)) },
                    text = {
                        Text(
                            text = "${stringResource(id = R.string.categories_tab_archived)} (${uiState.archivedCategories.size})",
                            fontWeight = if (uiState.selectedTab == CategoriesTab.ARCHIVED) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.category_search_hint),
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
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.cd_clear_search),
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                ),
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                val currentList = if (uiState.selectedTab == CategoriesTab.ACTIVE) {
                    uiState.activeCategories
                } else {
                    uiState.archivedCategories
                }

                val filteredList = remember(currentList, searchQuery) {
                    if (searchQuery.isBlank()) {
                        currentList
                    } else {
                        currentList.filter { category ->
                            category.name.contains(searchQuery, ignoreCase = true) ||
                                CategoryGroup.fromKey(category.groupKey).title.contains(searchQuery, ignoreCase = true)
                        }
                    }
                }

                if (currentList.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Category,
                        title = if (uiState.selectedTab == CategoriesTab.ACTIVE) {
                            stringResource(id = R.string.home_empty_title)
                        } else {
                            stringResource(id = R.string.categories_empty_archived)
                        },
                        subtitle = if (uiState.selectedTab == CategoriesTab.ACTIVE) {
                            stringResource(id = R.string.categories_empty_active)
                        } else {
                            stringResource(id = R.string.categories_archive_desc)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(id = R.string.category_no_results),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else if (uiState.selectedTab == CategoriesTab.ACTIVE && searchQuery.isBlank()) {
                    // Grouped by CategoryGroup
                    val grouped = remember(filteredList) {
                        filteredList.groupBy { CategoryGroup.fromKey(it.groupKey) }
                    }
                    val groupsInOrder = CategoryGroup.entries.filter { grouped.containsKey(it) }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        groupsInOrder.forEach { group ->
                            val groupItems = grouped[group].orEmpty()
                            item(key = "group_header_${group.key}") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = categoryGroupTitle(group),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(${groupItems.size})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            items(items = groupItems, key = { it.id }) { category ->
                                CategoryItemRow(
                                    category = category,
                                    onEdit = { viewModel.onEvent(CategoriesEvent.OpenEditSheet(category)) },
                                    onToggleArchive = { viewModel.onEvent(CategoriesEvent.ToggleArchive(category)) },
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(items = filteredList, key = { it.id }) { category ->
                            CategoryItemRow(
                                category = category,
                                onEdit = { viewModel.onEvent(CategoriesEvent.OpenEditSheet(category)) },
                                onToggleArchive = { viewModel.onEvent(CategoriesEvent.ToggleArchive(category)) },
                            )
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (uiState.isSheetVisible) {
        AddEditCategoryDialog(
            isEditing = uiState.editingCategoryId != null,
            name = uiState.nameInput,
            selectedIconKey = uiState.selectedIconKey,
            selectedColorHex = uiState.selectedColorHex,
            defaultType = uiState.selectedDefaultType,
            errorMessage = uiState.errorMessage,
            onNameChange = { viewModel.onEvent(CategoriesEvent.UpdateName(it)) },
            onIconSelected = { viewModel.onEvent(CategoriesEvent.SelectIcon(it)) },
            onColorSelected = { viewModel.onEvent(CategoriesEvent.SelectColor(it)) },
            onDefaultTypeSelected = { viewModel.onEvent(CategoriesEvent.SelectDefaultType(it)) },
            onSave = { viewModel.onEvent(CategoriesEvent.SaveCategory) },
            onDismiss = { viewModel.onEvent(CategoriesEvent.DismissSheet) },
        )
    }
}

@Composable
private fun CategoryItemRow(
    category: Category,
    onEdit: () -> Unit,
    onToggleArchive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = CategoryIconProvider.parseColorHex(category.colorHex)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = CategoryIconProvider.getIcon(category.iconKey),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (category.isDefault) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    NeedWasteBadge(type = category.defaultType)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.cd_edit),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                IconButton(onClick = onToggleArchive) {
                    Icon(
                        imageVector = if (category.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                        contentDescription = if (category.isArchived) {
                            stringResource(id = R.string.categories_unarchive)
                        } else {
                            stringResource(id = R.string.categories_archive)
                        },
                        tint = if (category.isArchived) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddEditCategoryDialog(
    isEditing: Boolean,
    name: String,
    selectedIconKey: String,
    selectedColorHex: String,
    defaultType: ExpenseType,
    errorMessage: String?,
    onNameChange: (String) -> Unit,
    onIconSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onDefaultTypeSelected: (ExpenseType) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) stringResource(id = R.string.categories_edit_category)
                else stringResource(id = R.string.categories_add_category),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(text = stringResource(id = R.string.categories_name_label)) },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(text = it) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Default Type
                Text(
                    text = stringResource(id = R.string.categories_default_type_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                ) {
                    val isNeed = defaultType == ExpenseType.NEED
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isNeed) MaterialTheme.semantic.needContainer else Color.Transparent)
                            .clickable { onDefaultTypeSelected(ExpenseType.NEED) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(id = R.string.type_need),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isNeed) FontWeight.Bold else FontWeight.Medium,
                            color = if (isNeed) MaterialTheme.semantic.need else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    val isWaste = defaultType == ExpenseType.WASTE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isWaste) MaterialTheme.semantic.wasteContainer else Color.Transparent)
                            .clickable { onDefaultTypeSelected(ExpenseType.WASTE) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(id = R.string.type_waste),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isWaste) FontWeight.Bold else FontWeight.Medium,
                            color = if (isWaste) MaterialTheme.semantic.waste else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Icon Picker
                Text(
                    text = stringResource(id = R.string.categories_icon_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(AVAILABLE_ICONS) { iconKey ->
                        val isSelected = selectedIconKey == iconKey
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { onIconSelected(iconKey) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = CategoryIconProvider.getIcon(iconKey),
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }

                // Color Picker
                Text(
                    text = stringResource(id = R.string.categories_color_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(CURATED_COLORS) { hex ->
                        val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                        val color = CategoryIconProvider.parseColorHex(hex)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { onColorSelected(hex) }
                                .then(
                                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text(text = stringResource(id = R.string.action_save))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        },
    )
}
