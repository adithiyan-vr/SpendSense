/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.IncomeWithSource
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun IncomeRow(
    incomeWithSource: IncomeWithSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val income = incomeWithSource.income
    val source = incomeWithSource.source
    val sourceIcon = CategoryIconProvider.getIcon(source.iconKey)
    val incomeColor = MaterialTheme.semantic.income
    val incomeContainer = MaterialTheme.semantic.incomeContainer

    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
    val timeStr = income.receivedAt.atZone(ZoneId.systemDefault()).format(timeFormatter)

    val title = if (!income.note.isNullOrBlank()) income.note else source.name
    val subtitle = if (!income.note.isNullOrBlank()) {
        "${source.name} • $timeStr"
    } else {
        timeStr
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Leading income source icon in tinted circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(incomeContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = sourceIcon,
                contentDescription = source.name,
                tint = incomeColor,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Subtitle
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Trailing amount + Received badge
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "+${Money.formatPaise(income.amountMinor)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = incomeColor,
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = incomeContainer,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.income_badge),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = incomeColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
