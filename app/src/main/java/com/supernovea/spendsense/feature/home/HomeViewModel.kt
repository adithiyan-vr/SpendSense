/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supernovea.spendsense.domain.analytics.AnalyticsEngine
import com.supernovea.spendsense.domain.analytics.PeriodCalculator
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.domain.usecase.GetExpensesUseCase
import com.supernovea.spendsense.domain.usecase.GetIncomesUseCase
import com.supernovea.spendsense.domain.usecase.GetPeriodSummaryUseCase
import com.supernovea.spendsense.feature.history.HistoryTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPeriodSummaryUseCase: GetPeriodSummaryUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getIncomesUseCase: GetIncomesUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val selectedPeriodFlow = MutableStateFlow(HomePeriod.MONTH)
    private val isAmountsHiddenFlow = MutableStateFlow<Boolean?>(null)
    private var loadJob: Job? = null

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        loadJob?.cancel()
        val today = LocalDate.now()
        val currentMonthPeriod = Period.Month(YearMonth.from(today))

        loadJob = viewModelScope.launch {
            settingsRepository.observeSettings().flatMapLatest { settings ->
                val currentWeekPeriod = PeriodCalculator.getWeekPeriod(today, settings.weekStartDay)

                val monthSummaryFlow = getPeriodSummaryUseCase(currentMonthPeriod, today)
                val weekSummaryFlow = getPeriodSummaryUseCase(currentWeekPeriod, today)
                val todayExpensesFlow = getExpensesUseCase.getBetween(today, today)
                val todayIncomesFlow = getIncomesUseCase.observeBetween(today, today)
                val allExpensesFlow = getExpensesUseCase.getAll()
                val allIncomesFlow = getIncomesUseCase.observeAll()
                val totalIncomeAllTimeFlow = getIncomesUseCase.observeTotalAllTime()

                combine(
                    monthSummaryFlow,
                    weekSummaryFlow,
                    todayExpensesFlow,
                    todayIncomesFlow,
                    allExpensesFlow,
                    allIncomesFlow,
                    totalIncomeAllTimeFlow,
                    selectedPeriodFlow,
                    isAmountsHiddenFlow,
                ) { flows ->
                    val monthRes = flows[0] as com.supernovea.spendsense.domain.usecase.PeriodSummaryResult
                    val weekRes = flows[1] as com.supernovea.spendsense.domain.usecase.PeriodSummaryResult
                    @Suppress("UNCHECKED_CAST")
                    val todayExpenses = flows[2] as List<com.supernovea.spendsense.domain.model.ExpenseWithCategory>
                    @Suppress("UNCHECKED_CAST")
                    val todayIncomes = flows[3] as List<com.supernovea.spendsense.domain.model.IncomeWithSource>
                    @Suppress("UNCHECKED_CAST")
                    val allExpenses = flows[4] as List<com.supernovea.spendsense.domain.model.ExpenseWithCategory>
                    @Suppress("UNCHECKED_CAST")
                    val allIncomes = flows[5] as List<com.supernovea.spendsense.domain.model.IncomeWithSource>
                    val totalIncomeAllTime = flows[6] as Long
                    val selectedPeriod = flows[7] as HomePeriod
                    val userHiddenPref = flows[8] as Boolean?

                    val isHidden = userHiddenPref ?: settings.hideAmountsDefault

                    val totalExpenseAllTime = allExpenses.sumOf { it.expense.amountMinor }
                    val availableBalance = AnalyticsEngine.calculateAvailableBalance(
                        openingBalanceMinor = settings.openingBalanceMinor,
                        totalIncomeMinor = totalIncomeAllTime,
                        totalExpenseMinor = totalExpenseAllTime
                    )

                    val daysLeft = AnalyticsEngine.calculateDaysLeftEstimate(
                        availableBalanceMinor = availableBalance,
                        expensesLast14Days = allExpenses,
                        today = today,
                    )

                    // Today metrics
                    val todayTotal = todayExpenses.sumOf { it.expense.amountMinor }
                    val todayWaste = todayExpenses
                        .filter { it.expense.type == ExpenseType.WASTE }
                        .sumOf { it.expense.amountMinor }
                    val todayRatio = if (todayTotal > 0L) (todayWaste.toDouble() / todayTotal.toDouble()) * 100.0 else 0.0

                    // Week metrics
                    val weekTotal = weekRes.summary.totalMinor
                    val weekWaste = weekRes.summary.wasteMinor
                    val weekRatio = weekRes.summary.wasteRatioPct

                    // Selected period breakdown
                    val (rec, spent, net, savRate, savRateDelta, need, waste, wasteRatio, wasteDelta) = when (selectedPeriod) {
                        HomePeriod.TODAY -> {
                            val r = todayIncomes.sumOf { it.income.amountMinor }
                            val s = todayTotal
                            val n = r - s
                            val sav = if (r > 0L) (n.toDouble() / r.toDouble()) * 100.0 else null
                            val nd = todayExpenses.filter { it.expense.type == ExpenseType.NEED }.sumOf { it.expense.amountMinor }
                            val ws = todayWaste
                            val wr = todayRatio
                            NineTuple(r, s, n, sav, null, nd, ws, wr, null)
                        }
                        HomePeriod.WEEK -> {
                            val r = weekRes.summary.incomeMinor
                            val s = weekRes.summary.totalMinor
                            val n = weekRes.summary.netMinor
                            val sav = weekRes.summary.savingsRatePct
                            val savDelta = weekRes.summary.previous?.savingsRateDeltaPts
                            val nd = weekRes.summary.needMinor
                            val ws = weekRes.summary.wasteMinor
                            val wr = weekRes.summary.wasteRatioPct
                            val wrDelta = weekRes.summary.previous?.wasteRatioDeltaPts
                            NineTuple(r, s, n, sav, savDelta, nd, ws, wr, wrDelta)
                        }
                        HomePeriod.MONTH -> {
                            val r = monthRes.summary.incomeMinor
                            val s = monthRes.summary.totalMinor
                            val n = monthRes.summary.netMinor
                            val sav = monthRes.summary.savingsRatePct
                            val savDelta = monthRes.summary.previous?.savingsRateDeltaPts
                            val nd = monthRes.summary.needMinor
                            val ws = monthRes.summary.wasteMinor
                            val wr = monthRes.summary.wasteRatioPct
                            val wrDelta = monthRes.summary.previous?.wasteRatioDeltaPts
                            NineTuple(r, s, n, sav, savDelta, nd, ws, wr, wrDelta)
                        }
                    }

                    // Combined Recent Transactions
                    val expenseTransactions: List<HistoryTransaction> = allExpenses.map { HistoryTransaction.ExpenseItem(it) }
                    val incomeTransactions: List<HistoryTransaction> = allIncomes.map { HistoryTransaction.IncomeItem(it) }
                    val combinedRecent = (expenseTransactions + incomeTransactions)
                        .sortedWith(
                            compareByDescending<HistoryTransaction> { it.occurredInstant }
                                .thenByDescending { it.id }
                        )
                        .take(5)

                    HomeUiState(
                        userName = "Friend",
                        userProfile = settings.userProfile,
                        currencySymbol = settings.currencySymbol,
                        availableBalanceMinor = availableBalance,
                        openingBalanceMinor = settings.openingBalanceMinor,
                        daysLeftEstimate = daysLeft,
                        isAmountsHidden = isHidden,
                        selectedPeriod = selectedPeriod,
                        receivedMinor = rec,
                        spentMinor = spent,
                        netMinor = net,
                        savingsRatePct = savRate,
                        savingsRateDeltaPts = savRateDelta,
                        needMinor = need,
                        wasteMinor = waste,
                        wasteRatioPct = wasteRatio,
                        wasteRatioDeltaPts = wasteDelta,
                        monthSummary = monthRes.summary,
                        budgetStatus = monthRes.budgetStatus,
                        topInsight = monthRes.insights.firstOrNull(),
                        todaySpend = MiniPeriodSpend(todayTotal, todayWaste, todayRatio),
                        weekSpend = MiniPeriodSpend(weekTotal, weekWaste, weekRatio),
                        recentExpenses = allExpenses.take(5),
                        recentTransactions = combinedRecent,
                        hasAnyExpenses = allExpenses.isNotEmpty(),
                        hasAnyTransactions = combinedRecent.isNotEmpty(),
                        isLoading = false,
                    )
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnSelectPeriod -> {
                selectedPeriodFlow.value = event.period
            }
            is HomeEvent.OnToggleHideAmounts -> {
                val current = _uiState.value.isAmountsHidden
                isAmountsHiddenFlow.value = !current
            }
            is HomeEvent.OnRefresh -> {
                loadHomeData()
            }
        }
    }
}

private data class NineTuple(
    val received: Long,
    val spent: Long,
    val net: Long,
    val savingsRatePct: Double?,
    val savingsRateDeltaPts: Double?,
    val need: Long,
    val waste: Long,
    val wasteRatio: Double,
    val wasteRatioDeltaPts: Double?,
)
