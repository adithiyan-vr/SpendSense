/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Brand Values
val BrandTeal = Color(0xFF2AC1AE)
val BrandOrange = Color(0xFFF5943B)
val BrandCharcoal = Color(0xFF1B1D23)

// Core Light Palette (Neutral Surface + Sapphire Blue Primary)
val PrimaryLight = Color(0xFF1E60B4)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD6E4F7)
val OnPrimaryContainerLight = Color(0xFF0A3063)

val BackgroundLight = Color(0xFFFAFAFA)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF1F3F5)
val OnSurfaceLight = Color(0xFF14161B)
val OnSurfaceVariantLight = Color(0xFF595E6B)
val OutlineLight = Color(0xFFD4D8E0)
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)

// Core Dark Palette (Charcoal #1B1D23 Family + Vibrant Sapphire Primary)
val PrimaryDark = Color(0xFF7BB1F4)
val OnPrimaryDark = Color(0xFF072E5D)
val PrimaryContainerDark = Color(0xFF13427D)
val OnPrimaryContainerDark = Color(0xFFCCE1FD)

val BackgroundDark = Color(0xFF14161B)
val SurfaceDark = Color(0xFF1B1D23) // Exact Brand Charcoal
val SurfaceVariantDark = Color(0xFF242831)
val OnSurfaceDark = Color(0xFFE8EAEF)
val OnSurfaceVariantDark = Color(0xFF9FA5B2)
val OutlineDark = Color(0xFF383E4C)
val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)

// Semantic Need (Teal family #2AC1AE)
val NeedLight = Color(0xFF0C7A6D)
val NeedContainerLight = Color(0xFFD2F4EE)
val OnNeedLight = Color(0xFFFFFFFF)
val OnNeedContainerLight = Color(0xFF084E46)

val NeedDark = Color(0xFF2AC1AE) // Brand Teal
val NeedContainerDark = Color(0xFF0E3B34)
val OnNeedDark = Color(0xFF032B25)
val OnNeedContainerDark = Color(0xFF68EBD9)

// Semantic Waste (Orange family #F5943B)
val WasteLight = Color(0xFFBA5400)
val WasteContainerLight = Color(0xFFFFE8D6)
val OnWasteLight = Color(0xFFFFFFFF)
val OnWasteContainerLight = Color(0xFF683000)

val WasteDark = Color(0xFFF5943B) // Brand Orange
val WasteContainerDark = Color(0xFF4A2B0E)
val OnWasteDark = Color(0xFF3E1C00)
val OnWasteContainerDark = Color(0xFFFED2A4)

// Warning (Sunny Gold/Yellow, distinct from Waste orange and paired with icon)
val WarningLight = Color(0xFF9A6200)
val WarningContainerLight = Color(0xFFFEF3C7)
val OnWarningLight = Color(0xFFFFFFFF)
val OnWarningContainerLight = Color(0xFF5B3900)

val WarningDark = Color(0xFFFACC15)
val WarningContainerDark = Color(0xFF473600)
val OnWarningDark = Color(0xFF2E2200)
val OnWarningContainerDark = Color(0xFFFEF08A)

// Semantic Income (Royal Blue family, distinct from Need teal & Waste orange)
val IncomeLight = Color(0xFF1D4ED8)
val IncomeContainerLight = Color(0xFFDBEAFE)
val OnIncomeLight = Color(0xFFFFFFFF)
val OnIncomeContainerLight = Color(0xFF1E3A8A)

val IncomeDark = Color(0xFF60A5FA)
val IncomeContainerDark = Color(0xFF1E3A8A)
val OnIncomeDark = Color(0xFF0F172A)
val OnIncomeContainerDark = Color(0xFFBFDBFE)

// 12-color categorical chart palette (excludes Need and Waste hues)
val ChartPaletteLight = listOf(
    Color(0xFF2563EB), // Royal Blue
    Color(0xFF7C3AED), // Violet
    Color(0xFFDB2777), // Pink
    Color(0xFF059669), // Emerald
    Color(0xFFD97706), // Amber
    Color(0xFF0891B2), // Cyan
    Color(0xFF4F46E5), // Indigo
    Color(0xFFDC2626), // Crimson
    Color(0xFF65A30D), // Lime
    Color(0xFF9333EA), // Purple
    Color(0xFF0D9488), // Teal-Dark
    Color(0xFF475569), // Slate
)

val ChartPaletteDark = listOf(
    Color(0xFF60A5FA), // Royal Blue
    Color(0xFFA78BFA), // Violet
    Color(0xFFF472B6), // Pink
    Color(0xFF34D399), // Emerald
    Color(0xFFFBBF24), // Amber
    Color(0xFF22D3EE), // Cyan
    Color(0xFF818CF8), // Indigo
    Color(0xFFF87171), // Crimson
    Color(0xFFA3E635), // Lime
    Color(0xFFC084FC), // Purple
    Color(0xFF2DD4BF), // Teal
    Color(0xFF94A3B8), // Slate
)

@Immutable
data class SemanticColors(
    val need: Color,
    val needContainer: Color,
    val onNeed: Color = Color.White,
    val onNeedContainer: Color = Color.Black,
    val waste: Color,
    val wasteContainer: Color,
    val onWaste: Color = Color.White,
    val onWasteContainer: Color = Color.Black,
    val income: Color,
    val incomeContainer: Color,
    val onIncome: Color = Color.White,
    val onIncomeContainer: Color = Color.Black,
    val warning: Color,
    val warningContainer: Color = Color.Unspecified,
    val onWarning: Color = Color.White,
    val onWarningContainer: Color = Color.Black,
    val chartPalette: List<Color> = emptyList(),
)

val LightSemanticColors = SemanticColors(
    need = NeedLight,
    needContainer = NeedContainerLight,
    onNeed = OnNeedLight,
    onNeedContainer = OnNeedContainerLight,
    waste = WasteLight,
    wasteContainer = WasteContainerLight,
    onWaste = OnWasteLight,
    onWasteContainer = OnWasteContainerLight,
    income = IncomeLight,
    incomeContainer = IncomeContainerLight,
    onIncome = OnIncomeLight,
    onIncomeContainer = OnIncomeContainerLight,
    warning = WarningLight,
    warningContainer = WarningContainerLight,
    onWarning = OnWarningLight,
    onWarningContainer = OnWarningContainerLight,
    chartPalette = ChartPaletteLight,
)

val DarkSemanticColors = SemanticColors(
    need = NeedDark,
    needContainer = NeedContainerDark,
    onNeed = OnNeedDark,
    onNeedContainer = OnNeedContainerDark,
    waste = WasteDark,
    wasteContainer = WasteContainerDark,
    onWaste = OnWasteDark,
    onWasteContainer = OnWasteContainerDark,
    income = IncomeDark,
    incomeContainer = IncomeContainerDark,
    onIncome = OnIncomeDark,
    onIncomeContainer = OnIncomeContainerDark,
    warning = WarningDark,
    warningContainer = WarningContainerDark,
    onWarning = OnWarningDark,
    onWarningContainer = OnWarningContainerDark,
    chartPalette = ChartPaletteDark,
)

val LocalSemanticColors = staticCompositionLocalOf { LightSemanticColors }
