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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.ExpenseWithCategory
import com.supernovea.spendsense.domain.model.PaymentMethod
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExpenseRow(
    expenseWithCategory: ExpenseWithCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val expense = expenseWithCategory.expense
    val category = expenseWithCategory.category
    val categoryColor = CategoryIconProvider.parseColorHex(category.colorHex)
    val categoryIcon = CategoryIconProvider.getIcon(category.iconKey)

    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
    val timeStr = expense.occurredAt.atZone(ZoneId.systemDefault()).format(timeFormatter)

    val paymentMethodStr = when (expense.paymentMethod) {
        PaymentMethod.CASH -> stringResource(id = R.string.payment_cash)
        PaymentMethod.UPI -> stringResource(id = R.string.payment_upi)
        PaymentMethod.CARD -> stringResource(id = R.string.payment_card)
        PaymentMethod.OTHER -> stringResource(id = R.string.payment_other)
    }

    val title = if (!expense.note.isNullOrBlank()) expense.note else category.name
    val subtitle = if (!expense.note.isNullOrBlank()) {
        "${category.name} • $paymentMethodStr • $timeStr"
    } else {
        "$paymentMethodStr • $timeStr"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Leading category icon in tinted circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(categoryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = stringResource(id = R.string.cd_category_icon),
                tint = categoryColor,
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

        // Trailing amount + Need/Waste badge
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = Money.formatPaise(expense.amountMinor),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            NeedWasteBadge(
                type = expense.type,
                compact = true,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
