/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisDateRangePickerDialog(
    initialStartDate: LocalDate,
    initialEndDate: LocalDate,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onDismiss: () -> Unit,
    today: LocalDate = LocalDate.now(),
) {
    val initialStartMillis = remember(initialStartDate) {
        initialStartDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
    val initialEndMillis = remember(initialEndDate) {
        initialEndDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val todayUtcMidnightMillis = remember(today) {
        today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val selectableDates = remember(todayUtcMidnightMillis, today) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= todayUtcMidnightMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= today.year && year >= (today.year - 5)
            }
        }
    }

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartMillis,
        initialSelectedEndDateMillis = initialEndMillis,
        selectableDates = selectableDates,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            val startMillis = dateRangePickerState.selectedStartDateMillis
            val endMillis = dateRangePickerState.selectedEndDateMillis
            val canConfirm = startMillis != null && endMillis != null

            TextButton(
                onClick = {
                    if (startMillis != null && endMillis != null) {
                        // Crucial date conversion: Material 3 date picker yields UTC midnight ms.
                        // Convert strictly via Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate()
                        // to avoid 1-day off-by-one errors for time zones east of UTC (e.g. IST +5:30).
                        val start = Instant.ofEpochMilli(startMillis).atZone(ZoneOffset.UTC).toLocalDate()
                        val end = Instant.ofEpochMilli(endMillis).atZone(ZoneOffset.UTC).toLocalDate()

                        if (!start.isAfter(end) && !end.isAfter(today) && ChronoUnit.YEARS.between(start, end) <= 5) {
                            onDateRangeSelected(start, end)
                        }
                    }
                },
                enabled = canConfirm,
            ) {
                Text(
                    text = stringResource(id = R.string.action_save),
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = stringResource(id = R.string.date_range_picker_title),
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}
