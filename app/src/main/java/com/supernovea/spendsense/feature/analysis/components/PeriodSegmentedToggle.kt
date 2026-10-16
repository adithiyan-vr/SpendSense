/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.feature.analysis.AnalysisPeriodMode

@Composable
fun PeriodSegmentedToggle(
    selectedMode: AnalysisPeriodMode,
    onModeSelected: (AnalysisPeriodMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val weekSelected = selectedMode == AnalysisPeriodMode.WEEK
        val weekBgColor by animateColorAsState(
            targetValue = if (weekSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
            animationSpec = tween(200),
            label = "week_bg"
        )
        val weekTextColor by animateColorAsState(
            targetValue = if (weekSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            animationSpec = tween(200),
            label = "week_text"
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(weekBgColor)
                .clickable(role = Role.Tab) { onModeSelected(AnalysisPeriodMode.WEEK) }
                .semantics { role = Role.Tab },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(id = R.string.period_week),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (weekSelected) FontWeight.Bold else FontWeight.Medium,
                color = weekTextColor,
            )
        }

        val monthSelected = selectedMode == AnalysisPeriodMode.MONTH
        val monthBgColor by animateColorAsState(
            targetValue = if (monthSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
            animationSpec = tween(200),
            label = "month_bg"
        )
        val monthTextColor by animateColorAsState(
            targetValue = if (monthSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            animationSpec = tween(200),
            label = "month_text"
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(monthBgColor)
                .clickable(role = Role.Tab) { onModeSelected(AnalysisPeriodMode.MONTH) }
                .semantics { role = Role.Tab },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(id = R.string.period_month),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (monthSelected) FontWeight.Bold else FontWeight.Medium,
                color = monthTextColor,
            )
        }

        val customSelected = selectedMode == AnalysisPeriodMode.CUSTOM
        val customBgColor by animateColorAsState(
            targetValue = if (customSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
            animationSpec = tween(200),
            label = "custom_bg"
        )
        val customTextColor by animateColorAsState(
            targetValue = if (customSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            animationSpec = tween(200),
            label = "custom_text"
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(customBgColor)
                .clickable(role = Role.Tab) { onModeSelected(AnalysisPeriodMode.CUSTOM) }
                .semantics { role = Role.Tab },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(id = R.string.period_custom),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (customSelected) FontWeight.Bold else FontWeight.Medium,
                color = customTextColor,
            )
        }
    }
}
