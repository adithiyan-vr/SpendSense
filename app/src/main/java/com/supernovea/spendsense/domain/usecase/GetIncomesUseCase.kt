/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.IncomeWithSource
import com.supernovea.spendsense.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetIncomesUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
) {
    fun observeBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<IncomeWithSource>> {
        return incomeRepository.observeBetween(startDate, endDate)
    }

    fun observeAll(): Flow<List<IncomeWithSource>> {
        return incomeRepository.observeAll()
    }

    fun observeById(id: Long): Flow<IncomeWithSource?> {
        return incomeRepository.observeById(id)
    }

    fun observeTotalBetween(startDate: LocalDate, endDate: LocalDate): Flow<Long> {
        return incomeRepository.observeTotalBetween(startDate, endDate)
    }

    fun observeTotalAllTime(): Flow<Long> {
        return incomeRepository.observeTotalAllTime()
    }
}
