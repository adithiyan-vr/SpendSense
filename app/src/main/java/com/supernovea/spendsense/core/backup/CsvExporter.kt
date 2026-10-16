/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.backup

import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object CsvExporter {

    private const val HEADER = "id,date,time,amount,type,category,note,payment_method"
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

    fun export(expenses: List<ExpenseWithCategory>): String {
        val sb = StringBuilder()
        sb.append(HEADER).append("\n")

        val sorted = expenses.sortedByDescending { it.expense.occurredAt }
        for (item in sorted) {
            val expense = item.expense
            val category = item.category

            val id = expense.id
            val date = expense.occurredDate.toString()
            val time = expense.occurredAt.atZone(ZoneId.systemDefault()).format(timeFormatter)
            val amount = Money.toInputString(expense.amountMinor)
            val type = expense.type.name
            val categoryName = escapeCsv(category.name)
            val note = escapeCsv(expense.note ?: "")
            val paymentMethod = escapeCsv(expense.paymentMethod.name)

            sb.append("$id,$date,$time,$amount,$type,$categoryName,$note,$paymentMethod\n")
        }

        return sb.toString()
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
