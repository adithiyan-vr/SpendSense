/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.supernovea.spendsense.core.ui.NeedWasteBadge
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.feature.analysis.AnalysisPeriodMode
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun AnalysisSummaryHeader(
    summary: PeriodSummary,
    mode: AnalysisPeriodMode,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Label and delta badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.analysis_total_spent),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                val periodName = when (mode) {
                    AnalysisPeriodMode.WEEK -> stringResource(id = R.string.period_week).lowercase()
                    AnalysisPeriodMode.MONTH -> stringResource(id = R.string.period_month).lowercase()
                    AnalysisPeriodMode.CUSTOM -> stringResource(id = R.string.period_custom).lowercase()
                }

                val deltaPct = summary.previous?.totalDeltaPct
                if (deltaPct != null) {
                    val deltaAbs = abs(deltaPct).roundToInt()
                    val isUp = deltaPct > 0
                    val deltaText = if (isUp) {
                        stringResource(id = R.string.analysis_delta_up, deltaAbs, periodName)
                    } else {
                        stringResource(id = R.string.analysis_delta_down, deltaAbs, periodName)
                    }

                    val containerColor = if (isUp) {
                        MaterialTheme.semantic.wasteContainer
                    } else {
                        MaterialTheme.semantic.needContainer
                    }
                    val textColor = if (isUp) {
                        MaterialTheme.semantic.waste
                    } else {
                        MaterialTheme.semantic.need
                    }

                    Text(
                        text = deltaText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(containerColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Total spent amount
            Text(
                text = Money.format(summary.totalMinor),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Side by Side Need & Waste Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Need Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.semantic.needContainer.copy(alpha = 0.5f),
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            NeedWasteBadge(type = ExpenseType.NEED)
                            Text(
                                text = "${summary.needRatioPct.roundToInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.semantic.need,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = Money.format(summary.needMinor),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                // Waste Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.semantic.wasteContainer.copy(alpha = 0.5f),
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            NeedWasteBadge(type = ExpenseType.WASTE)
                            Text(
                                text = "${summary.wasteRatioPct.roundToInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.semantic.waste,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = Money.format(summary.wasteMinor),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            val pts = summary.previous?.wasteRatioDeltaPts
                            if (pts != null && abs(pts) >= 0.5) {
                                Spacer(modifier = Modifier.width(6.dp))
                                val ptsRound = abs(pts).roundToInt()
                                val isPtsUp = pts > 0.0
                                val ptsText = if (isPtsUp) {
                                    stringResource(id = R.string.analysis_pts_up, ptsRound)
                                } else {
                                    stringResource(id = R.string.analysis_pts_down, ptsRound)
                                }
                                val ptsColor = if (isPtsUp) MaterialTheme.semantic.waste else MaterialTheme.semantic.need

                                Text(
                                    text = ptsText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ptsColor,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
