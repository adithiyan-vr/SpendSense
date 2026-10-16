/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class UpdateIncomeUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val incomeSourceRepository: IncomeSourceRepository,
) {
    suspend operator fun invoke(
        id: Long,
        amountMinor: Long,
        sourceId: Long,
        note: String? = null,
        receivedAt: Instant,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ) {
        require(amountMinor > 0L) { "Income amount must be greater than zero." }

        val existing = incomeRepository.getById(id)
        requireNotNull(existing) { "Income with ID $id does not exist." }

        val source = incomeSourceRepository.getById(sourceId)
        requireNotNull(source) { "Income source with ID $sourceId does not exist." }

        val now = Instant.now()
        val receivedDate = receivedAt.atZone(zoneId).toLocalDate()

        val updated = existing.income.copy(
            amountMinor = amountMinor,
            sourceId = sourceId,
            note = note?.trim()?.ifEmpty { null },
            receivedAt = receivedAt,
            receivedDate = receivedDate,
            updatedAt = now,
        )

        incomeRepository.updateIncome(updated)
    }
}
