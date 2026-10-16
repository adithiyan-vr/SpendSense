/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.categories

import com.supernovea.spendsense.domain.model.Category
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.usecase.ManageCategoriesUseCase
import com.supernovea.spendsense.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CategoriesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val manageCategoriesUseCase = mockk<ManageCategoriesUseCase>(relaxed = true)

    private val activeCat = Category(
        id = 1L,
        name = "Groceries",
        iconKey = "restaurant",
        colorHex = "#4CAF50",
        defaultType = ExpenseType.NEED,
        isArchived = false,
        isDefault = true,
    )

    private val archivedCat = Category(
        id = 2L,
        name = "Old Hobby",
        iconKey = "sports",
        colorHex = "#9C27B0",
        defaultType = ExpenseType.WASTE,
        isArchived = true,
        isDefault = false,
    )

    @Before
    fun setup() {
        every { manageCategoriesUseCase.getCategories(includeArchived = true) } returns flowOf(
            listOf(activeCat, archivedCat)
        )
    }

    @Test
    fun `initial state loads active and archived categories`() = runTest {
        val viewModel = CategoriesViewModel(manageCategoriesUseCase)

        val state = viewModel.uiState.value
        assertEquals(CategoriesTab.ACTIVE, state.selectedTab)
        assertEquals(1, state.activeCategories.size)
        assertEquals(1, state.archivedCategories.size)
        assertEquals("Groceries", state.activeCategories.first().name)
        assertEquals("Old Hobby", state.archivedCategories.first().name)
    }

    @Test
    fun `selectTab updates selectedTab in uiState`() = runTest {
        val viewModel = CategoriesViewModel(manageCategoriesUseCase)

        viewModel.onEvent(CategoriesEvent.SelectTab(CategoriesTab.ARCHIVED))
        assertEquals(CategoriesTab.ARCHIVED, viewModel.uiState.value.selectedTab)

        viewModel.onEvent(CategoriesEvent.SelectTab(CategoriesTab.ACTIVE))
        assertEquals(CategoriesTab.ACTIVE, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `openAddSheet sets up default add sheet state`() = runTest {
        val viewModel = CategoriesViewModel(manageCategoriesUseCase)

        viewModel.onEvent(CategoriesEvent.OpenAddSheet)

        val state = viewModel.uiState.value
        assertTrue(state.isSheetVisible)
        assertNull(state.editingCategoryId)
        assertEquals("", state.nameInput)
        assertEquals(ExpenseType.NEED, state.selectedDefaultType)
    }

    @Test
    fun `openEditSheet populates sheet fields with category data`() = runTest {
        val viewModel = CategoriesViewModel(manageCategoriesUseCase)

        viewModel.onEvent(CategoriesEvent.OpenEditSheet(activeCat))

        val state = viewModel.uiState.value
        assertTrue(state.isSheetVisible)
        assertEquals(1L, state.editingCategoryId)
        assertEquals("Groceries", state.nameInput)
        assertEquals("restaurant", state.selectedIconKey)
        assertEquals("#4CAF50", state.selectedColorHex)
        assertEquals(ExpenseType.NEED, state.selectedDefaultType)
    }

    @Test
    fun `dismissSheet hides sheet`() = runTest {
        val viewModel = CategoriesViewModel(manageCategoriesUseCase)
        viewModel.onEvent(CategoriesEvent.OpenAddSheet)
        assertTrue(viewModel.uiState.value.isSheetVisible)

        viewModel.onEvent(CategoriesEvent.DismissSheet)
        assertFalse(viewModel.uiState.value.isSheetVisible)
    }

    @Test
    fun `saveCategory with blank name sets errorMessage`() = runTest {
        val viewModel = CategoriesViewModel(manageCategoriesUseCase)
        viewModel.onEvent(CategoriesEvent.OpenAddSheet)
        viewModel.onEvent(CategoriesEvent.UpdateName("   "))
        viewModel.onEvent(CategoriesEvent.SaveCategory)

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        coVerify(exactly = 0) { manageCategoriesUseCase.addCategory(any(), any(), any(), any()) }
    }

    @Test
    fun `saveCategory creates category when editingCategoryId is null`() = runTest {
        val viewModel = CategoriesViewModel(manageCategoriesUseCase)
        viewModel.onEvent(CategoriesEvent.OpenAddSheet)
        viewModel.onEvent(CategoriesEvent.UpdateName("Dining Out"))
        viewModel.onEvent(CategoriesEvent.SelectIcon("restaurant"))
        viewModel.onEvent(CategoriesEvent.SelectColor("#FF5722"))
        viewModel.onEvent(CategoriesEvent.SelectDefaultType(ExpenseType.WASTE))

        viewModel.onEvent(CategoriesEvent.SaveCategory)

        coVerify {
            manageCategoriesUseCase.addCategory(
                name = "Dining Out",
                iconKey = "restaurant",
                colorHex = "#FF5722",
                defaultType = ExpenseType.WASTE,
            )
        }
        assertFalse(viewModel.uiState.value.isSheetVisible)
    }

    @Test
    fun `saveCategory updates category when editingCategoryId is present`() = runTest {
        coEvery { manageCategoriesUseCase.getById(1L) } returns activeCat

        val viewModel = CategoriesViewModel(manageCategoriesUseCase)
        viewModel.onEvent(CategoriesEvent.OpenEditSheet(activeCat))
        viewModel.onEvent(CategoriesEvent.UpdateName("Fresh Groceries"))

        viewModel.onEvent(CategoriesEvent.SaveCategory)

        coVerify {
            manageCategoriesUseCase.updateCategory(
                activeCat.copy(name = "Fresh Groceries")
            )
        }
        assertFalse(viewModel.uiState.value.isSheetVisible)
    }

    @Test
    fun `toggleArchive updates archive status via manageCategoriesUseCase`() = runTest {
        val viewModel = CategoriesViewModel(manageCategoriesUseCase)

        viewModel.onEvent(CategoriesEvent.ToggleArchive(activeCat))

        coVerify {
            manageCategoriesUseCase.archiveCategory(
                id = 1L,
                isArchived = true,
            )
        }
    }
}
