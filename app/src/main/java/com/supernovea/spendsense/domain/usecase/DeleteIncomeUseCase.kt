/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.repository.IncomeRepository
import java.time.Instant
import javax.inject.Inject

class DeleteIncomeUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
) {
    suspend operator fun invoke(id: Long, deletedAt: Instant = Instant.now()) {
        incomeRepository.softDeleteIncome(id, deletedAt)
    }
}
