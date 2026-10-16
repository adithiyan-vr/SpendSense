/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.components

import androidx.annotation.StringRes
import com.supernovea.spendsense.R
import com.supernovea.spendsense.domain.model.Period
import java.time.LocalDate
import java.time.YearMonth

enum class DateRangePreset(
    @StringRes val labelRes: Int,
) {
    TODAY(R.string.preset_today),
    LAST_7_DAYS(R.string.preset_last_7_days),
    LAST_30_DAYS(R.string.preset_last_30_days),
    THIS_MONTH(R.string.preset_this_month),
    LAST_MONTH(R.string.preset_last_month),
    LAST_3_MONTHS(R.string.preset_last_3_months),
    THIS_YEAR(R.string.preset_this_year),
    CUSTOM(R.string.preset_custom_range);

    fun calculateRange(today: LocalDate): Period.Custom? {
        return when (this) {
            TODAY -> Period.Custom(today, today)
            LAST_7_DAYS -> Period.Custom(today.minusDays(6), today)
            LAST_30_DAYS -> Period.Custom(today.minusDays(29), today)
            THIS_MONTH -> Period.Custom(today.withDayOfMonth(1), today)
            LAST_MONTH -> {
                val prevMonth = YearMonth.from(today).minusMonths(1)
                Period.Custom(prevMonth.atDay(1), prevMonth.atEndOfMonth())
            }
            LAST_3_MONTHS -> Period.Custom(today.minusMonths(3).plusDays(1), today)
            THIS_YEAR -> Period.Custom(today.withDayOfYear(1), today)
            CUSTOM -> null
        }
    }
}
