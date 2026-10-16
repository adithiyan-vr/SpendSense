/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.ui

import androidx.compose.ui.graphics.Color
import kotlin.math.floor

data class PieInputItem(
    val id: String,
    val label: String,
    val amountMinor: Long,
    val iconKey: String? = null,
    val preferredColorHex: String? = null,
    val preferredColor: Color? = null,
)

data class PieSlice(
    val id: String,
    val label: String,
    val amountMinor: Long,
    val percentage: Int, // exact integer percentage, sums to 100%
    val color: Color,
    val iconKey: String? = null,
    val isOthers: Boolean = false,
    val mergedItems: List<PieInputItem> = emptyList(),
    val startAngle: Float = 0f,
    val sweepAngle: Float = 0f,
)

/**
 * Pure function builder for pie/donut charts implementing:
 * 1. Top 6 + "Others" grouping, with N+1 (7 items) single-item exception (shows all 7 by name).
 * 2. Descending order sort by amount.
 * 3. Largest Remainder Method (Hare-Niemeyer) guaranteeing integer percentages sum to exactly 100%.
 * 4. Distinct color assignment preventing adjacent or duplicate collisions.
 * 5. 12 o'clock start (-90f) with 2° slice gaps.
 */
object PieSliceBuilder {

    const val MAX_SLICES_BEFORE_OTHERS = 6
    const val SLICE_GAP_DEGREES = 2f

    fun build(
        items: List<PieInputItem>,
        palette: List<Color>,
        othersLabel: String = "Others",
        othersColor: Color = Color(0xFF64748B),
    ): List<PieSlice> {
        val validItems = items.filter { it.amountMinor > 0L }.sortedByDescending { it.amountMinor }
        if (validItems.isEmpty()) return emptyList()

        val totalMinor = validItems.sumOf { it.amountMinor }
        if (totalMinor <= 0L) return emptyList()

        // Grouping: top 6 + Others, unless total == 7 (N+1 exception)
        val (primaryItems, othersItems) = when {
            validItems.size <= MAX_SLICES_BEFORE_OTHERS -> Pair(validItems, emptyList())
            validItems.size == MAX_SLICES_BEFORE_OTHERS + 1 -> Pair(validItems, emptyList())
            else -> Pair(
                validItems.take(MAX_SLICES_BEFORE_OTHERS),
                validItems.drop(MAX_SLICES_BEFORE_OTHERS),
            )
        }

        data class IntermediateSlice(
            val id: String,
            val label: String,
            val amountMinor: Long,
            val iconKey: String?,
            val preferredColor: Color?,
            val isOthers: Boolean,
            val mergedItems: List<PieInputItem>,
        )

        val intermediate = buildList {
            primaryItems.forEach { item ->
                add(
                    IntermediateSlice(
                        id = item.id,
                        label = item.label,
                        amountMinor = item.amountMinor,
                        iconKey = item.iconKey,
                        preferredColor = item.preferredColor,
                        isOthers = false,
                        mergedItems = emptyList(),
                    )
                )
            }
            if (othersItems.isNotEmpty()) {
                val othersAmount = othersItems.sumOf { it.amountMinor }
                add(
                    IntermediateSlice(
                        id = "others",
                        label = othersLabel,
                        amountMinor = othersAmount,
                        iconKey = "more_horiz",
                        preferredColor = othersColor,
                        isOthers = true,
                        mergedItems = othersItems,
                    )
                )
            }
        }

        // Largest Remainder Method
        val exactShares = intermediate.map { (it.amountMinor.toDouble() * 100.0) / totalMinor.toDouble() }
        val floored = exactShares.map { floor(it).toInt() }.toMutableList()
        val remainders = exactShares.mapIndexed { index, share -> index to (share - floored[index]) }
            .sortedByDescending { it.second }

        val currentSum = floored.sum()
        var deficit = 100 - currentSum
        var rIdx = 0
        while (deficit > 0 && rIdx < remainders.size) {
            val targetIdx = remainders[rIdx].first
            floored[targetIdx] += 1
            deficit -= 1
            rIdx += 1
        }

        // Color allocation (distinct colors)
        val usedColors = mutableSetOf<Color>()
        val safePalette = if (palette.isEmpty()) listOf(Color(0xFF2563EB)) else palette

        val assignedColors = intermediate.mapIndexed { index, item ->
            if (item.isOthers) {
                othersColor
            } else {
                val preferred = item.preferredColor
                if (preferred != null && !usedColors.contains(preferred)) {
                    usedColors.add(preferred)
                    preferred
                } else {
                    val colorFromPalette = safePalette.firstOrNull { !usedColors.contains(it) }
                        ?: safePalette[index % safePalette.size]
                    usedColors.add(colorFromPalette)
                    colorFromPalette
                }
            }
        }

        // Angle calculation: starts at 12 o'clock (-90f), 2° gap if > 1 slice
        val count = intermediate.size
        val gap = if (count > 1) SLICE_GAP_DEGREES else 0f
        val totalGap = count * gap
        val totalSweep = 360f - totalGap

        var currentStartAngle = -90f

        return intermediate.mapIndexed { index, item ->
            val fraction = item.amountMinor.toDouble() / totalMinor.toDouble()
            val sweep = if (count == 1) 360f else (fraction.toFloat() * totalSweep).coerceAtLeast(0.5f)
            val slice = PieSlice(
                id = item.id,
                label = item.label,
                amountMinor = item.amountMinor,
                percentage = floored[index],
                color = assignedColors[index],
                iconKey = item.iconKey,
                isOthers = item.isOthers,
                mergedItems = item.mergedItems,
                startAngle = currentStartAngle,
                sweepAngle = sweep,
            )
            currentStartAngle += sweep + gap
            slice
        }
    }
}
