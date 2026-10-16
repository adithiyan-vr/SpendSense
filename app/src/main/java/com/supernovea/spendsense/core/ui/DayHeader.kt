/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.util.Money
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DayHeader(
    date: LocalDate,
    totalMinor: Long,
    modifier: Modifier = Modifier,
    needMinor: Long = 0L,
    wasteMinor: Long = 0L,
    receivedMinor: Long = 0L,
    today: LocalDate = LocalDate.now(),
) {
    val dateText = when (date) {
        today -> stringResource(id = R.string.history_day_today)
        today.minusDays(1) -> stringResource(id = R.string.history_day_yesterday)
        else -> {
            val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())
            date.format(formatter)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (receivedMinor > 0L) {
                    Text(
                        text = "+${Money.formatPaise(receivedMinor)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.semantic.income,
                    )
                }
                if (totalMinor > 0L || receivedMinor == 0L) {
                    Text(
                        text = Money.formatPaise(totalMinor),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        if (needMinor > 0L || wasteMinor > 0L) {
            Spacer(modifier = Modifier.height(4.dp))
            NeedWasteBar(
                needMinor = needMinor,
                wasteMinor = wasteMinor,
                barHeight = 4,
                showLabels = false,
            )
        }
    }
}
