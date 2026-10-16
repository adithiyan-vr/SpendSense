/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.analytics

import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.WeekStartDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * Pure Kotlin calculator for analytical periods (Week / Month), date ranges,
 * and previous/next period navigation.
 * Adheres strictly to PRD §8 definitions with zero Android dependencies.
 */
object PeriodCalculator {

    data class DateRange(
        val start: LocalDate,
        val end: LocalDate,
    ) {
        val startEpochDay: Long get() = start.toEpochDay()
        val endEpochDay: Long get() = end.toEpochDay()

        operator fun contains(date: LocalDate): Boolean {
            return !date.isBefore(start) && !date.isAfter(end)
        }

        val daysCount: Int
            get() = (ChronoUnit.DAYS.between(start, end) + 1).toInt()
    }

    /**
     * Resolves the [Period.Week] containing [date], aligned to [weekStartDay].
     */
    fun getWeekPeriod(
        date: LocalDate,
        weekStartDay: WeekStartDay = WeekStartDay.MONDAY,
    ): Period.Week {
        val targetDayOfWeek = when (weekStartDay) {
            WeekStartDay.MONDAY -> DayOfWeek.MONDAY
            WeekStartDay.SUNDAY -> DayOfWeek.SUNDAY
        }
        val start = date.with(TemporalAdjusters.previousOrSame(targetDayOfWeek))
        return Period.Week(start)
    }

    /**
     * Resolves the [Period.Month] containing [date].
     */
    fun getMonthPeriod(date: LocalDate): Period.Month {
        return Period.Month(YearMonth.from(date))
    }

    /**
     * Calculates the inclusive start and end [LocalDate]s for [period].
     */
    fun getDateRange(period: Period): DateRange {
        return when (period) {
            is Period.Week -> {
                val start = period.start
                val end = start.plusDays(6)
                DateRange(start, end)
            }
            is Period.Month -> {
                val start = period.ym.atDay(1)
                val end = period.ym.atEndOfMonth()
                DateRange(start, end)
            }
            is Period.Custom -> {
                DateRange(period.startDate, period.endDate)
            }
        }
    }

    /**
     * Computes the immediately preceding [Period].
     * For a week: previous 7 days (same start day of week).
     * For a month: previous calendar month.
     * For a custom range: equal number of days immediately preceding startDate.
     */
    fun getPreviousPeriod(period: Period): Period {
        return when (period) {
            is Period.Week -> Period.Week(period.start.minusWeeks(1))
            is Period.Month -> Period.Month(period.ym.minusMonths(1))
            is Period.Custom -> {
                val days = ChronoUnit.DAYS.between(period.startDate, period.endDate) + 1
                val prevEnd = period.startDate.minusDays(1)
                val prevStart = prevEnd.minusDays(days - 1)
                Period.Custom(prevStart, prevEnd)
            }
        }
    }

    /**
     * Computes the immediately subsequent [Period].
     */
    fun getNextPeriod(period: Period): Period {
        return when (period) {
            is Period.Week -> Period.Week(period.start.plusWeeks(1))
            is Period.Month -> Period.Month(period.ym.plusMonths(1))
            is Period.Custom -> {
                val days = ChronoUnit.DAYS.between(period.startDate, period.endDate) + 1
                val nextStart = period.endDate.plusDays(1)
                val nextEnd = nextStart.plusDays(days - 1)
                Period.Custom(nextStart, nextEnd)
            }
        }
    }

    /**
     * Formats a [Period] to a human-readable title.
     * Examples:
     * - Month: "October 2026"
     * - Week within month: "12 – 18 Oct 2026"
     * - Week crossing month: "28 Sep – 4 Oct 2026"
     * - Week crossing year: "28 Dec 2026 – 3 Jan 2027"
     * - Custom single day: "14 Oct 2026"
     * - Custom range: "1 – 15 Oct 2026"
     */
    fun formatPeriodLabel(period: Period): String {
        return when (period) {
            is Period.Month -> {
                val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
                period.ym.format(formatter)
            }
            is Period.Week -> {
                val start = period.start
                val end = start.plusDays(6)

                when {
                    start.year != end.year -> {
                        val startFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
                        val endFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
                        "${start.format(startFmt)} – ${end.format(endFmt)}"
                    }
                    start.month != end.month -> {
                        val startFmt = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
                        val endFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
                        "${start.format(startFmt)} – ${end.format(endFmt)}"
                    }
                    else -> {
                        val endFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
                        "${start.dayOfMonth} – ${end.format(endFmt)}"
                    }
                }
            }
            is Period.Custom -> {
                val start = period.startDate
                val end = period.endDate

                when {
                    start == end -> {
                        val fmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
                        start.format(fmt)
                    }
                    start.year != end.year -> {
                        val startFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
                        val endFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
                        "${start.format(startFmt)} – ${end.format(endFmt)}"
                    }
                    start.month != end.month -> {
                        val startFmt = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
                        val endFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
                        "${start.format(startFmt)} – ${end.format(endFmt)}"
                    }
                    else -> {
                        val endFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
                        "${start.dayOfMonth} – ${end.format(endFmt)}"
                    }
                }
            }
        }
    }
}
