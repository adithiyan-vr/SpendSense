/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.usecase

import com.supernovea.spendsense.domain.model.Income
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

/**
 * Use case to validate and persist a new income entry.
 * Derives local [receivedDate] from [receivedAt] and enforces amount > 0.
 */
class AddIncomeUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val incomeSourceRepository: IncomeSourceRepository,
) {
    suspend operator fun invoke(
        amountMinor: Long,
        sourceId: Long,
        note: String? = null,
        receivedAt: Instant = Instant.now(),
        zoneId: ZoneId = ZoneId.systemDefault(),
        recurringRuleId: Long? = null,
    ): Long {
        require(amountMinor > 0L) { "Income amount must be greater than zero." }

        val source = incomeSourceRepository.getById(sourceId)
        requireNotNull(source) { "Income source with ID $sourceId does not exist." }

        val now = Instant.now()
        val receivedDate = receivedAt.atZone(zoneId).toLocalDate()

        val income = Income(
            id = 0L,
            amountMinor = amountMinor,
            sourceId = sourceId,
            note = note?.trim()?.ifEmpty { null },
            receivedAt = receivedAt,
            receivedDate = receivedDate,
            createdAt = now,
            updatedAt = now,
            deletedAt = null,
            recurringRuleId = recurringRuleId,
        )

        return incomeRepository.insertIncome(income)
    }
}
