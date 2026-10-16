/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.export

import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.IncomeWithSource
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Generates RFC 4180 compliant CSV exports for the selected date range.
 * Meets FR-084 and FR-085:
 * Header: kind,date,time,amount,type,category_or_source,note,payment_method
 */
object CsvReportGenerator {

    const val HEADER = "kind,date,time,amount,type,category_or_source,note,payment_method"

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US)

    private data class CombinedRecord(
        val timestamp: Instant,
        val kind: String,
        val date: String,
        val time: String,
        val amountStr: String,
        val type: String,
        val categoryOrSource: String,
        val note: String,
        val paymentMethod: String,
    )

    fun generate(
        expenses: List<ExpenseWithCategory>,
        incomes: List<IncomeWithSource>,
        includeNotes: Boolean = false,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): String {
        val records = mutableListOf<CombinedRecord>()

        for (item in expenses) {
            val expense = item.expense
            val category = item.category
            val noteText = if (includeNotes) (expense.note ?: "") else ""
            val timeText = expense.occurredAt.atZone(zoneId).format(timeFormatter)

            records.add(
                CombinedRecord(
                    timestamp = expense.occurredAt,
                    kind = "EXPENSE",
                    date = expense.occurredDate.toString(),
                    time = timeText,
                    amountStr = Money.toInputString(expense.amountMinor),
                    type = expense.type.name,
                    categoryOrSource = category.name,
                    note = noteText,
                    paymentMethod = expense.paymentMethod.name,
                )
            )
        }

        for (item in incomes) {
            val income = item.income
            val source = item.source
            val noteText = if (includeNotes) (income.note ?: "") else ""
            val timeText = income.receivedAt.atZone(zoneId).format(timeFormatter)

            records.add(
                CombinedRecord(
                    timestamp = income.receivedAt,
                    kind = "INCOME",
                    date = income.receivedDate.toString(),
                    time = timeText,
                    amountStr = Money.toInputString(income.amountMinor),
                    type = "",
                    categoryOrSource = source.name,
                    note = noteText,
                    paymentMethod = "",
                )
            )
        }

        // Sort descending by timestamp
        records.sortByDescending { it.timestamp }

        val sb = StringBuilder()
        sb.append(HEADER).append("\n")

        for (record in records) {
            sb.append(escapeCsv(record.kind)).append(",")
                .append(escapeCsv(record.date)).append(",")
                .append(escapeCsv(record.time)).append(",")
                .append(escapeCsv(record.amountStr)).append(",")
                .append(escapeCsv(record.type)).append(",")
                .append(escapeCsv(record.categoryOrSource)).append(",")
                .append(escapeCsv(record.note)).append(",")
                .append(escapeCsv(record.paymentMethod))
                .append("\n")
        }

        return sb.toString()
    }

    fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
