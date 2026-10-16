/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.IncomeSource
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageIncomeSourcesUseCase @Inject constructor(
    private val incomeSourceRepository: IncomeSourceRepository,
) {
    fun observeActive(): Flow<List<IncomeSource>> = incomeSourceRepository.observeActive()

    fun observeAll(): Flow<List<IncomeSource>> = incomeSourceRepository.observeAll()

    suspend fun createSource(
        name: String,
        iconKey: String = "payments",
        colorHex: String = "#3B82F6",
    ): Long {
        val trimmed = name.trim()
        require(trimmed.isNotEmpty()) { "Source name cannot be blank." }
        val existing = incomeSourceRepository.getByName(trimmed)
        require(existing == null) { "Source '$trimmed' already exists." }

        val source = IncomeSource(
            id = 0L,
            name = trimmed,
            iconKey = iconKey,
            colorHex = colorHex,
            isArchived = false,
            isDefault = false,
            sortOrder = incomeSourceRepository.count(),
        )
        return incomeSourceRepository.upsertIncomeSource(source)
    }

    suspend fun setArchived(id: Long, isArchived: Boolean) {
        incomeSourceRepository.setArchived(id, isArchived)
    }
}
