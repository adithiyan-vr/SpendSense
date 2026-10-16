/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supernovea.spendsense.R
import com.supernovea.spendsense.core.designsystem.semantic
import com.supernovea.spendsense.core.ui.EmptyState
import com.supernovea.spendsense.core.ui.ExpenseRow
import com.supernovea.spendsense.core.ui.IncomeRow
import com.supernovea.spendsense.core.ui.InsightCard
import com.supernovea.spendsense.core.ui.MiniNeedWasteDonut
import com.supernovea.spendsense.core.ui.NeedWasteBar
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.BudgetStatus
import com.supernovea.spendsense.feature.history.HistoryTransaction
import java.time.LocalTime
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onIncomeClick: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val currentHour = LocalTime.now().hour
    val greetingRes = when {
        currentHour < 12 -> R.string.greeting_morning
        currentHour < 17 -> R.string.greeting_afternoon
        else -> R.string.greeting_evening
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (!state.hasAnyTransactions) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                HomeTopBar(
                    greetingText = "${stringResource(id = greetingRes)}, ${state.userName}",
                    isAmountsHidden = state.isAmountsHidden,
                    onToggleHideAmounts = { viewModel.onEvent(HomeEvent.OnToggleHideAmounts) },
                    onSettingsClick = onNavigateToSettings,
                )

                EmptyState(
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    title = stringResource(id = R.string.home_empty_title),
                    subtitle = stringResource(id = R.string.home_empty_subtitle),
                    actionLabel = stringResource(id = R.string.home_add_first_expense),
                    onActionClick = onNavigateToAddExpense,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Top Greeting, Hide Amounts & Settings
                item {
                    HomeTopBar(
                        greetingText = "${stringResource(id = greetingRes)}, ${state.userName}",
                        isAmountsHidden = state.isAmountsHidden,
                        onToggleHideAmounts = { viewModel.onEvent(HomeEvent.OnToggleHideAmounts) },
                        onSettingsClick = onNavigateToSettings,
                    )
                }

                // Hero Available Balance Card
                item {
                    AvailableBalanceCard(
                        availableBalanceMinor = state.availableBalanceMinor,
                        openingBalanceMinor = state.openingBalanceMinor,
                        daysLeftEstimate = state.daysLeftEstimate,
                        isAmountsHidden = state.isAmountsHidden,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }

                // Period Selector Chips
                item {
                    PeriodSelectorRow(
                        selectedPeriod = state.selectedPeriod,
                        onPeriodSelected = { period ->
                            viewModel.onEvent(HomeEvent.OnSelectPeriod(period))
                        },
                    )
                }

                // Received & Spent Side-by-Side Cards
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        val receivedDisplay = if (state.isAmountsHidden) {
                            stringResource(id = R.string.home_hidden_amounts_mask)
                        } else {
                            Money.formatPaise(state.receivedMinor)
                        }
                        MetricMiniCard(
                            title = stringResource(id = R.string.home_received),
                            amountText = receivedDisplay,
                            titleColor = MaterialTheme.semantic.income,
                            amountColor = MaterialTheme.colorScheme.onSurface,
                            icon = Icons.Default.ArrowDownward,
                            modifier = Modifier.weight(1f),
                        )

                        val spentDisplay = if (state.isAmountsHidden) {
                            stringResource(id = R.string.home_hidden_amounts_mask)
                        } else {
                            Money.formatPaise(state.spentMinor)
                        }
                        MetricMiniCard(
                            title = stringResource(id = R.string.home_spent),
                            amountText = spentDisplay,
                            titleColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            amountColor = MaterialTheme.colorScheme.onSurface,
                            icon = Icons.Default.ArrowUpward,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                // Net & Savings Rate Summary
                item {
                    NetSavingsSummary(
                        netMinor = state.netMinor,
                        savingsRatePct = state.savingsRatePct,
                        savingsRateDeltaPts = state.savingsRateDeltaPts,
                        isAmountsHidden = state.isAmountsHidden,
                    )
                }

                // Need vs Waste & Budget Progress Cards
                item {
                    BreakdownAndBudgetRow(
                        needMinor = state.needMinor,
                        wasteMinor = state.wasteMinor,
                        budgetStatus = state.budgetStatus,
                    )
                }

                // Top Insight (if present)
                state.topInsight?.let { insight ->
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            InsightCard(insight = insight)
                        }
                    }
                }

                // Recent Transactions Header & Items
                if (state.recentTransactions.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(id = R.string.home_recent_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            TextButton(onClick = onNavigateToHistory) {
                                Text(
                                    text = stringResource(id = R.string.home_see_all),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }

                    items(
                        items = state.recentTransactions,
                        key = { transaction ->
                            when (transaction) {
                                is HistoryTransaction.ExpenseItem -> "exp_${transaction.id}"
                                is HistoryTransaction.IncomeItem -> "inc_${transaction.id}"
                            }
                        }
                    ) { transaction ->
                        when (transaction) {
                            is HistoryTransaction.ExpenseItem -> {
                                ExpenseRow(
                                    expenseWithCategory = transaction.expenseWithCategory,
                                    onClick = { onExpenseClick(transaction.id) },
                                )
                            }
                            is HistoryTransaction.IncomeItem -> {
                                IncomeRow(
                                    incomeWithSource = transaction.incomeWithSource,
                                    onClick = { onIncomeClick(transaction.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    greetingText: String,
    isAmountsHidden: Boolean,
    onToggleHideAmounts: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greetingText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(id = R.string.app_tagline),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onToggleHideAmounts) {
                Icon(
                    imageVector = if (isAmountsHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = stringResource(
                        id = if (isAmountsHidden) R.string.cd_show_amounts else R.string.cd_hide_amounts
                    ),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.cd_settings),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AvailableBalanceCard(
    availableBalanceMinor: Long,
    openingBalanceMinor: Long,
    daysLeftEstimate: Int?,
    isAmountsHidden: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.home_available_balance),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            val displayAmount = if (isAmountsHidden) {
                stringResource(id = R.string.home_hidden_amounts_mask)
            } else {
                Money.formatPaise(availableBalanceMinor)
            }

            Text(
                text = displayAmount,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val openingText = if (isAmountsHidden) {
                    stringResource(
                        id = R.string.home_opening_balance,
                        stringResource(id = R.string.home_hidden_amounts_mask)
                    )
                } else {
                    stringResource(
                        id = R.string.home_opening_balance,
                        Money.formatPaise(openingBalanceMinor)
                    )
                }
                Text(
                    text = openingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (daysLeftEstimate != null && daysLeftEstimate > 0) {
                    val daysText = if (daysLeftEstimate == 1) {
                        stringResource(id = R.string.home_days_left_singular)
                    } else {
                        stringResource(id = R.string.home_days_left, daysLeftEstimate)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.semantic.needContainer,
                    ) {
                        Text(
                            text = daysText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.semantic.need,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodSelectorRow(
    selectedPeriod: HomePeriod,
    onPeriodSelected: (HomePeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HomePeriod.entries.forEach { period ->
            val isSelected = period == selectedPeriod
            val labelRes = when (period) {
                HomePeriod.TODAY -> R.string.period_today
                HomePeriod.WEEK -> R.string.period_this_week
                HomePeriod.MONTH -> R.string.period_this_month
            }
            Surface(
                selected = isSelected,
                onClick = { onPeriodSelected(period) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = labelRes),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    amountText: String,
    titleColor: Color,
    amountColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = titleColor,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = amountText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = amountColor,
            )
        }
    }
}

@Composable
private fun NetSavingsSummary(
    netMinor: Long,
    savingsRatePct: Double?,
    savingsRateDeltaPts: Double?,
    isAmountsHidden: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        val netPrefix = when {
            netMinor > 0L -> "+"
            netMinor < 0L -> "-"
            else -> ""
        }
        val netText = if (isAmountsHidden) {
            stringResource(id = R.string.home_net) + " " + stringResource(id = R.string.home_hidden_amounts_mask)
        } else {
            val formatted = Money.formatPaise(abs(netMinor))
            stringResource(id = R.string.home_net) + " " + netPrefix + formatted
        }

        val netColor = when {
            netMinor > 0L -> MaterialTheme.semantic.need
            netMinor < 0L -> MaterialTheme.semantic.waste
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }

        Text(
            text = netText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = netColor,
        )

        if (savingsRatePct != null) {
            Text(
                text = "  ·  ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val savedRate = savingsRatePct.roundToInt()
            Text(
                text = stringResource(id = R.string.home_saved_rate, savedRate),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (savingsRateDeltaPts != null && savingsRateDeltaPts != 0.0) {
                val isUp = savingsRateDeltaPts > 0.0
                val ptsText = if (isUp) {
                    stringResource(id = R.string.home_points_up, abs(savingsRateDeltaPts))
                } else {
                    stringResource(id = R.string.home_points_down, abs(savingsRateDeltaPts))
                }
                val deltaColor = if (isUp) MaterialTheme.semantic.need else MaterialTheme.semantic.waste
                Text(
                    text = "  $ptsText",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = deltaColor,
                )
            }
        }
    }
}

@Composable
private fun BreakdownAndBudgetRow(
    needMinor: Long,
    wasteMinor: Long,
    budgetStatus: BudgetStatus?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Need vs Waste Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MiniNeedWasteDonut(
                    needMinor = needMinor,
                    wasteMinor = wasteMinor,
                    size = 46.dp,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.home_need_vs_waste),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val total = needMinor + wasteMinor
                    val needPct = if (total > 0L) Math.round((needMinor.toDouble() / total.toDouble()) * 100.0).toInt() else 0
                    val wastePct = if (total > 0L) (100 - needPct) else 0
                    Text(
                        text = "Need $needPct% · Waste $wastePct%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        // Budget Progress Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.home_budget_progress),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (budgetStatus != null) {
                    val progressFraction = (budgetStatus.totalProgressPct / 100.0).toFloat().coerceIn(0f, 1f)
                    val progressColor = when {
                        budgetStatus.isOverTotal -> MaterialTheme.semantic.waste
                        budgetStatus.totalProgressPct >= 80.0 -> MaterialTheme.semantic.warning
                        else -> MaterialTheme.colorScheme.primary
                    }
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val label = if (budgetStatus.isOverTotal) {
                        val overMinor = budgetStatus.totalSpentMinor - budgetStatus.budget.totalLimitMinor
                        stringResource(id = R.string.home_budget_over, Money.formatPaise(overMinor))
                    } else {
                        stringResource(id = R.string.home_budget_used, budgetStatus.totalProgressPct.toInt())
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (budgetStatus.isOverTotal) MaterialTheme.semantic.waste else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.home_no_budget_set),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
