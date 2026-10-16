/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.domain.model.ExpenseType

@Composable
fun NeedWasteBadge(
    type: ExpenseType,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val isNeed = type == ExpenseType.NEED
    val backgroundColor = if (isNeed) {
        MaterialTheme.semantic.needContainer
    } else {
        MaterialTheme.semantic.wasteContainer
    }
    val contentColor = if (isNeed) {
        MaterialTheme.semantic.need
    } else {
        MaterialTheme.semantic.waste
    }
    val label = if (isNeed) {
        stringResource(id = R.string.type_need)
    } else {
        stringResource(id = R.string.type_waste)
    }
    val icon = if (isNeed) Icons.Default.Check else Icons.Default.Warning

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(
                horizontal = if (compact) 6.dp else 8.dp,
                vertical = if (compact) 2.dp else 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // decorative within badge with visible text
            tint = contentColor,
            modifier = Modifier.size(if (compact) 12.dp else 14.dp),
        )
        Spacer(modifier = Modifier.width(if (compact) 2.dp else 4.dp))
        Text(
            text = label,
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
        )
    }
}
