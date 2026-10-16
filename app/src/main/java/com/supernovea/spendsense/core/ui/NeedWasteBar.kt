/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.util.Money

@Composable
fun NeedWasteBar(
    needMinor: Long,
    wasteMinor: Long,
    modifier: Modifier = Modifier,
    barHeight: Int = 12,
    showLabels: Boolean = true,
) {
    val total = needMinor + wasteMinor
    val needFraction = if (total > 0L) (needMinor.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0.5f
    val wasteFraction = if (total > 0L) (wasteMinor.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0.5f

    val needPct = if (total > 0L) Math.round((needMinor.toDouble() / total.toDouble()) * 100.0).toInt() else 0
    val wastePct = if (total > 0L) (100 - needPct) else 0

    val animatedNeedWeight by animateFloatAsState(
        targetValue = if (total > 0L) needFraction else 0.5f,
        animationSpec = tween(durationMillis = 600),
        label = "needWeightAnimation"
    )
    val animatedWasteWeight by animateFloatAsState(
        targetValue = if (total > 0L) wasteFraction else 0.5f,
        animationSpec = tween(durationMillis = 600),
        label = "wasteWeightAnimation"
    )

    val a11yDescription = if (total > 0L) {
        "Need: ${Money.formatPaise(needMinor)} ($needPct percent), Waste: ${Money.formatPaise(wasteMinor)} ($wastePct percent)"
    } else {
        "No spending recorded yet"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = a11yDescription }
    ) {
        // Stacked bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (total > 0L) {
                Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                    if (animatedNeedWeight > 0.001f) {
                        Box(
                            modifier = Modifier
                                .weight(animatedNeedWeight)
                                .fillMaxHeight()
                                .background(MaterialTheme.semantic.need)
                        )
                    }
                    if (animatedWasteWeight > 0.001f) {
                        Box(
                            modifier = Modifier
                                .weight(animatedWasteWeight)
                                .fillMaxHeight()
                                .background(MaterialTheme.semantic.waste)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                )
            }
        }

        if (showLabels) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Need label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.semantic.need)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(id = R.string.type_need) + ": " + Money.formatPaise(needMinor) + " ($needPct%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                    )
                }

                // Waste label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.semantic.waste)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(id = R.string.type_waste) + ": " + Money.formatPaise(wasteMinor) + " ($wastePct%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
