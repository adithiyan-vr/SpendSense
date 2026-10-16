/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.analytics

import com.supernovea.spendsense.domain.model.Period
import com.supernovea.spendsense.domain.model.WeekStartDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class PeriodCalculatorTest {

    @Test
    fun `getWeekPeriod calculates Monday start correctly for Monday, midweek, and Sunday`() {
        val monday = LocalDate.of(2026, 10, 12)
        val wednesday = LocalDate.of(2026, 10, 14)
        val sunday = LocalDate.of(2026, 10, 18)

        val weekFromMon = PeriodCalculator.getWeekPeriod(monday, WeekStartDay.MONDAY)
        val weekFromWed = PeriodCalculator.getWeekPeriod(wednesday, WeekStartDay.MONDAY)
        val weekFromSun = PeriodCalculator.getWeekPeriod(sunday, WeekStartDay.MONDAY)

        assertEquals(LocalDate.of(2026, 10, 12), weekFromMon.start)
        assertEquals(LocalDate.of(2026, 10, 12), weekFromWed.start)
        assertEquals(LocalDate.of(2026, 10, 12), weekFromSun.start)
    }

    @Test
    fun `getWeekPeriod calculates Sunday start correctly`() {
        val sunday = LocalDate.of(2026, 10, 11)
        val monday = LocalDate.of(2026, 10, 12)
        val saturday = LocalDate.of(2026, 10, 17)

        val weekFromSun = PeriodCalculator.getWeekPeriod(sunday, WeekStartDay.SUNDAY)
        val weekFromMon = PeriodCalculator.getWeekPeriod(monday, WeekStartDay.SUNDAY)
        val weekFromSat = PeriodCalculator.getWeekPeriod(saturday, WeekStartDay.SUNDAY)

        assertEquals(LocalDate.of(2026, 10, 11), weekFromSun.start)
        assertEquals(LocalDate.of(2026, 10, 11), weekFromMon.start)
        assertEquals(LocalDate.of(2026, 10, 11), weekFromSat.start)
    }

    @Test
    fun `getMonthPeriod resolves YearMonth accurately`() {
        val date = LocalDate.of(2026, 10, 15)
        val monthPeriod = PeriodCalculator.getMonthPeriod(date)
        assertEquals(YearMonth.of(2026, 10), monthPeriod.ym)
    }

    @Test
    fun `getDateRange calculates week range and contains logic`() {
        val week = Period.Week(LocalDate.of(2026, 10, 12))
        val range = PeriodCalculator.getDateRange(week)

        assertEquals(LocalDate.of(2026, 10, 12), range.start)
        assertEquals(LocalDate.of(2026, 10, 18), range.end)
        assertEquals(7, range.daysCount)

        assertTrue(range.contains(LocalDate.of(2026, 10, 12)))
        assertTrue(range.contains(LocalDate.of(2026, 10, 15)))
        assertTrue(range.contains(LocalDate.of(2026, 10, 18)))
        assertFalse(range.contains(LocalDate.of(2026, 10, 11)))
        assertFalse(range.contains(LocalDate.of(2026, 10, 19)))
    }

    @Test
    fun `getDateRange handles 31-day months and leap years correctly`() {
        val oct2026 = Period.Month(YearMonth.of(2026, 10))
        val octRange = PeriodCalculator.getDateRange(oct2026)
        assertEquals(LocalDate.of(2026, 10, 1), octRange.start)
        assertEquals(LocalDate.of(2026, 10, 31), octRange.end)
        assertEquals(31, octRange.daysCount)

        // Leap year February (2024 has 29 days)
        val febLeap = Period.Month(YearMonth.of(2024, 2))
        val leapRange = PeriodCalculator.getDateRange(febLeap)
        assertEquals(LocalDate.of(2024, 2, 1), leapRange.start)
        assertEquals(LocalDate.of(2024, 2, 29), leapRange.end)
        assertEquals(29, leapRange.daysCount)

        // Non-leap year February (2025 has 28 days)
        val febNonLeap = Period.Month(YearMonth.of(2025, 2))
        val nonLeapRange = PeriodCalculator.getDateRange(febNonLeap)
        assertEquals(LocalDate.of(2025, 2, 1), nonLeapRange.start)
        assertEquals(LocalDate.of(2025, 2, 28), nonLeapRange.end)
        assertEquals(28, nonLeapRange.daysCount)
    }

    @Test
    fun `getPreviousPeriod and getNextPeriod navigate correctly across years`() {
        val week = Period.Week(LocalDate.of(2026, 1, 5))
        val prevWeek = PeriodCalculator.getPreviousPeriod(week) as Period.Week
        val nextWeek = PeriodCalculator.getNextPeriod(week) as Period.Week
        assertEquals(LocalDate.of(2025, 12, 29), prevWeek.start)
        assertEquals(LocalDate.of(2026, 1, 12), nextWeek.start)

        val janMonth = Period.Month(YearMonth.of(2026, 1))
        val prevMonth = PeriodCalculator.getPreviousPeriod(janMonth) as Period.Month
        val nextMonth = PeriodCalculator.getNextPeriod(janMonth) as Period.Month
        assertEquals(YearMonth.of(2025, 12), prevMonth.ym)
        assertEquals(YearMonth.of(2026, 2), nextMonth.ym)
    }

    @Test
    fun `formatPeriodLabel formats week and month labels cleanly`() {
        val month = Period.Month(YearMonth.of(2026, 10))
        assertEquals("October 2026", PeriodCalculator.formatPeriodLabel(month))

        val sameMonthWeek = Period.Week(LocalDate.of(2026, 10, 12))
        assertEquals("12 – 18 Oct 2026", PeriodCalculator.formatPeriodLabel(sameMonthWeek))

        val crossMonthWeek = Period.Week(LocalDate.of(2026, 9, 28))
        assertEquals("28 Sep – 4 Oct 2026", PeriodCalculator.formatPeriodLabel(crossMonthWeek))

        val crossYearWeek = Period.Week(LocalDate.of(2026, 12, 28))
        assertEquals("28 Dec 2026 – 3 Jan 2027", PeriodCalculator.formatPeriodLabel(crossYearWeek))

        val singleDayCustom = Period.Custom(LocalDate.of(2026, 10, 14), LocalDate.of(2026, 10, 14))
        assertEquals("14 Oct 2026", PeriodCalculator.formatPeriodLabel(singleDayCustom))

        val sameMonthCustom = Period.Custom(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 15))
        assertEquals("1 – 15 Oct 2026", PeriodCalculator.formatPeriodLabel(sameMonthCustom))

        val crossMonthCustom = Period.Custom(LocalDate.of(2026, 9, 20), LocalDate.of(2026, 10, 5))
        assertEquals("20 Sep – 5 Oct 2026", PeriodCalculator.formatPeriodLabel(crossMonthCustom))

        val crossYearCustom = Period.Custom(LocalDate.of(2026, 12, 25), LocalDate.of(2027, 1, 5))
        assertEquals("25 Dec 2026 – 5 Jan 2027", PeriodCalculator.formatPeriodLabel(crossYearCustom))
    }

    @Test
    fun `Period Custom calculates range, navigation and rejects invalid dates`() {
        val custom = Period.Custom(LocalDate.of(2026, 10, 11), LocalDate.of(2026, 10, 20))
        val range = PeriodCalculator.getDateRange(custom)

        assertEquals(LocalDate.of(2026, 10, 11), range.start)
        assertEquals(LocalDate.of(2026, 10, 20), range.end)
        assertEquals(10, range.daysCount)
        assertTrue(range.contains(LocalDate.of(2026, 10, 15)))
        assertFalse(range.contains(LocalDate.of(2026, 10, 10)))

        val prev = PeriodCalculator.getPreviousPeriod(custom) as Period.Custom
        assertEquals(LocalDate.of(2026, 10, 1), prev.startDate)
        assertEquals(LocalDate.of(2026, 10, 10), prev.endDate)

        val next = PeriodCalculator.getNextPeriod(custom) as Period.Custom
        assertEquals(LocalDate.of(2026, 10, 21), next.startDate)
        assertEquals(LocalDate.of(2026, 10, 30), next.endDate)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Period Custom rejects startDate after endDate`() {
        Period.Custom(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 10))
    }
}
