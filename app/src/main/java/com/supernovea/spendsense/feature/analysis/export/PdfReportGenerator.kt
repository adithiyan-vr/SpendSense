/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.feature.analysis.export

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.supernovea.spendsense.core.util.Money
import com.supernovea.spendsense.domain.model.ExpenseType
import com.supernovea.spendsense.domain.model.Insight
import com.supernovea.spendsense.domain.model.PeriodSummary
import com.supernovea.spendsense.domain.model.UserProfileType
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.max

/**
 * Native Android [PdfDocument] report generator conforming to FR-083 and EXPORT-01.
 * Generates an A4 PDF document (595 x 842 pt) with clean typography, SpendSense brand mark,
 * period summary cards, Need vs Waste ratio bar, category breakdown, income breakdown,
 * highlights, and insights.
 */
object PdfReportGenerator {

    const val PAGE_WIDTH = 595
    const val PAGE_HEIGHT = 842
    const val MARGIN_LEFT = 40f
    const val MARGIN_RIGHT = 555f
    const val CONTENT_WIDTH = MARGIN_RIGHT - MARGIN_LEFT // 515f

    fun getReportFileName(startDate: LocalDate, endDate: LocalDate): String {
        return "SpendSense_Report_${startDate}_to_${endDate}.pdf"
    }

    private class PdfRenderContext(
        val document: PdfDocument,
    ) {
        var pageNumber = 1
        private var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var currentPage = document.startPage(pageInfo)
        var canvas: Canvas = currentPage.canvas
        var currentY = 40f

        fun checkOverflow(heightNeeded: Float) {
            if (currentY + heightNeeded > PAGE_HEIGHT - 45f) {
                drawFooter()
                document.finishPage(currentPage)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                currentPage = document.startPage(pageInfo)
                canvas = currentPage.canvas
                currentY = 40f
            }
        }

        fun drawFooter() {
            val footerPaint = Paint().apply {
                color = Color.parseColor("#94A3B8")
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            val footerText = "SpendSense • Confidential Personal Finance • Page $pageNumber"
            canvas.drawText(footerText, MARGIN_LEFT, PAGE_HEIGHT - 25f, footerPaint)
        }

        fun finish() {
            drawFooter()
            document.finishPage(currentPage)
        }
    }

    fun generate(
        outputStream: OutputStream,
        summary: PeriodSummary,
        insights: List<Insight>,
        userProfile: UserProfileType,
        currencySymbol: String,
        periodLabel: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ) {
        val document = PdfDocument()
        val context = PdfRenderContext(document)

        try {
            drawHeader(context, userProfile)
            drawPeriodBanner(context, periodLabel, startDate, endDate)
            drawSummaryCards(context, summary, currencySymbol)
            drawNeedWasteSection(context, summary, currencySymbol)
            drawCategoryTable(context, summary, currencySymbol)

            if (summary.incomeMinor > 0L && summary.incomeBySource.isNotEmpty()) {
                drawIncomeTable(context, summary, currencySymbol)
            }

            drawHighlights(context, summary, currencySymbol)

            if (insights.isNotEmpty()) {
                drawInsights(context, insights)
            }

            context.finish()
            document.writeTo(outputStream)
        } finally {
            document.close()
        }
    }

    private fun drawHeader(context: PdfRenderContext, userProfile: UserProfileType) {
        val canvas = context.canvas
        val y = context.currentY

        // Brand mark: two angled pill bars in SpendSense brand colors Teal (#2AC1AE) and Orange (#F5943B)
        val markPaint = Paint().apply { isAntiAlias = true }

        markPaint.color = Color.parseColor("#2AC1AE")
        canvas.drawRoundRect(RectF(MARGIN_LEFT, y, MARGIN_LEFT + 6f, y + 24f), 3f, 3f, markPaint)

        markPaint.color = Color.parseColor("#F5943B")
        canvas.drawRoundRect(RectF(MARGIN_LEFT + 10f, y + 4f, MARGIN_LEFT + 16f, y + 28f), 3f, 3f, markPaint)

        // Brand title
        val titlePaint = Paint().apply {
            color = Color.parseColor("#1B1D23")
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("SPENDSENSE", MARGIN_LEFT + 24f, y + 18f, titlePaint)

        val subTitlePaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("FINANCIAL SUMMARY REPORT", MARGIN_LEFT + 24f, y + 30f, subTitlePaint)

        // Right side info: generation date & profile
        val infoPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }

        val nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.US))
        canvas.drawText("Generated: $nowStr", MARGIN_RIGHT, y + 16f, infoPaint)

        val profileLabel = when (userProfile) {
            UserProfileType.STUDENT -> "Profile: Student"
            UserProfileType.WORKING_PROFESSIONAL -> "Profile: Professional"
            UserProfileType.OTHER -> "Profile: Standard"
        }
        canvas.drawText(profileLabel, MARGIN_RIGHT, y + 28f, infoPaint)

        // Header divider
        val linePaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            strokeWidth = 1f
            isAntiAlias = true
        }
        canvas.drawLine(MARGIN_LEFT, y + 42f, MARGIN_RIGHT, y + 42f, linePaint)

        context.currentY += 52f
    }

    private fun drawPeriodBanner(
        context: PdfRenderContext,
        periodLabel: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ) {
        val canvas = context.canvas
        val y = context.currentY
        val height = 36f

        // Rounded banner background
        val bgPaint = Paint().apply {
            color = Color.parseColor("#F1F5F9")
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(MARGIN_LEFT, y, MARGIN_RIGHT, y + height), 6f, 6f, bgPaint)

        val labelPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val daysCount = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1
        canvas.drawText("Period: $periodLabel ($daysCount days)", MARGIN_LEFT + 14f, y + 23f, labelPaint)

        val dateRangePaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        val rangeFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)
        canvas.drawText("${startDate.format(rangeFmt)} – ${endDate.format(rangeFmt)}", MARGIN_RIGHT - 14f, y + 23f, dateRangePaint)

        context.currentY += height + 14f
    }

    private fun drawSummaryCards(
        context: PdfRenderContext,
        summary: PeriodSummary,
        currencySymbol: String,
    ) {
        val canvas = context.canvas
        val y = context.currentY
        val cardHeight = 60f
        val cardGap = 8f
        val cardWidth = (CONTENT_WIDTH - (cardGap * 3f)) / 4f

        val bgPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            isAntiAlias = true
        }
        val borderPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }
        val titlePaint = Paint().apply {
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val valuePaint = Paint().apply {
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val deltaPaint = Paint().apply {
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        data class CardData(
            val title: String,
            val value: String,
            val titleColor: String,
            val valueColor: String,
            val deltaText: String? = null,
        )

        val netFormatted = if (summary.netMinor >= 0) {
            "+" + Money.format(summary.netMinor, currencySymbol)
        } else {
            Money.format(summary.netMinor, currencySymbol)
        }

        val cards = listOf(
            CardData(
                title = "RECEIVED",
                value = Money.format(summary.incomeMinor, currencySymbol),
                titleColor = "#4E73DF",
                valueColor = "#1E293B",
                deltaText = summary.previous?.incomeDeltaPct?.let {
                    val sign = if (it >= 0) "▲" else "▼"
                    "$sign ${String.format(Locale.US, "%.1f%%", kotlin.math.abs(it))} vs prev"
                },
            ),
            CardData(
                title = "SPENT",
                value = Money.format(summary.totalMinor, currencySymbol),
                titleColor = "#F5943B",
                valueColor = "#1E293B",
                deltaText = summary.previous?.totalDeltaPct?.let {
                    val sign = if (it >= 0) "▲" else "▼"
                    "$sign ${String.format(Locale.US, "%.1f%%", kotlin.math.abs(it))} vs prev"
                },
            ),
            CardData(
                title = "NET",
                value = netFormatted,
                titleColor = if (summary.netMinor >= 0) "#2AC1AE" else "#EF4444",
                valueColor = if (summary.netMinor >= 0) "#047857" else "#B91C1C",
                deltaText = summary.previous?.netDeltaPct?.let {
                    val sign = if (it >= 0) "▲" else "▼"
                    "$sign ${String.format(Locale.US, "%.1f%%", kotlin.math.abs(it))}"
                },
            ),
            CardData(
                title = "SAVINGS RATE",
                value = summary.savingsRatePct?.let { String.format(Locale.US, "%.1f%%", it) } ?: "N/A",
                titleColor = "#6366F1",
                valueColor = "#1E293B",
                deltaText = summary.previous?.savingsRateDeltaPts?.let {
                    val sign = if (it >= 0) "▲" else "▼"
                    String.format(Locale.US, "$sign %.1f pts", kotlin.math.abs(it))
                },
            ),
        )

        for (i in cards.indices) {
            val card = cards[i]
            val left = MARGIN_LEFT + (i * (cardWidth + cardGap))
            val rect = RectF(left, y, left + cardWidth, y + cardHeight)

            canvas.drawRoundRect(rect, 6f, 6f, bgPaint)
            canvas.drawRoundRect(rect, 6f, 6f, borderPaint)

            titlePaint.color = Color.parseColor(card.titleColor)
            canvas.drawText(card.title, left + 10f, y + 17f, titlePaint)

            valuePaint.color = Color.parseColor(card.valueColor)
            canvas.drawText(card.value, left + 10f, y + 36f, valuePaint)

            if (card.deltaText != null) {
                deltaPaint.color = Color.parseColor("#64748B")
                canvas.drawText(card.deltaText, left + 10f, y + 51f, deltaPaint)
            }
        }

        context.currentY += cardHeight + 14f
    }

    private fun drawNeedWasteSection(
        context: PdfRenderContext,
        summary: PeriodSummary,
        currencySymbol: String,
    ) {
        val canvas = context.canvas
        val y = context.currentY

        val headingPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Need vs Waste Breakdown", MARGIN_LEFT, y + 11f, headingPaint)

        val barY = y + 20f
        val barHeight = 12f
        val needRatio = if (summary.totalMinor > 0L) summary.needMinor.toFloat() / summary.totalMinor.toFloat() else 0.5f
        val needWidth = CONTENT_WIDTH * needRatio

        // Draw Need portion (Teal)
        val needPaint = Paint().apply {
            color = Color.parseColor("#2AC1AE")
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(MARGIN_LEFT, barY, MARGIN_LEFT + needWidth, barY + barHeight), 4f, 4f, needPaint)

        // Draw Waste portion (Orange)
        if (summary.totalMinor > 0L && summary.wasteMinor > 0L) {
            val wastePaint = Paint().apply {
                color = Color.parseColor("#F5943B")
                isAntiAlias = true
            }
            canvas.drawRoundRect(RectF(MARGIN_LEFT + needWidth - 2f, barY, MARGIN_RIGHT, barY + barHeight), 4f, 4f, wastePaint)
        }

        // Subtext / Legend
        val textPaint = Paint().apply {
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val dotPaint = Paint().apply { isAntiAlias = true }

        // Need Dot + Text
        dotPaint.color = Color.parseColor("#2AC1AE")
        canvas.drawCircle(MARGIN_LEFT + 5f, barY + barHeight + 14f, 4f, dotPaint)
        textPaint.color = Color.parseColor("#1E293B")
        val needText = "Need: ${Money.format(summary.needMinor, currencySymbol)} (${String.format(Locale.US, "%.1f%%", summary.needRatioPct)})"
        canvas.drawText(needText, MARGIN_LEFT + 14f, barY + barHeight + 17f, textPaint)

        // Waste Dot + Text
        val wasteStartX = MARGIN_LEFT + 200f
        dotPaint.color = Color.parseColor("#F5943B")
        canvas.drawCircle(wasteStartX + 5f, barY + barHeight + 14f, 4f, dotPaint)
        val wasteText = "Waste: ${Money.format(summary.wasteMinor, currencySymbol)} (${String.format(Locale.US, "%.1f%%", summary.wasteRatioPct)})"
        canvas.drawText(wasteText, wasteStartX + 14f, barY + barHeight + 17f, textPaint)

        // Waste ratio right aligned
        val ratioPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        val ratioText = String.format(Locale.US, "Waste Ratio: %.1f%%", summary.wasteRatioPct)
        canvas.drawText(ratioText, MARGIN_RIGHT, barY + barHeight + 17f, ratioPaint)

        context.currentY = barY + barHeight + 28f
    }

    private fun drawCategoryTable(
        context: PdfRenderContext,
        summary: PeriodSummary,
        currencySymbol: String,
    ) {
        val categories = summary.topCategories
        if (categories.isEmpty()) return

        context.checkOverflow(40f + (categories.size * 20f))

        val canvas = context.canvas
        var y = context.currentY

        val headingPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Spending by Category", MARGIN_LEFT, y + 11f, headingPaint)
        y += 20f

        // Table Header
        val headerBg = Paint().apply {
            color = Color.parseColor("#F1F5F9")
            isAntiAlias = true
        }
        canvas.drawRect(RectF(MARGIN_LEFT, y, MARGIN_RIGHT, y + 18f), headerBg)

        val headerTextPaint = Paint().apply {
            color = Color.parseColor("#475569")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("CATEGORY", MARGIN_LEFT + 8f, y + 12f, headerTextPaint)
        canvas.drawText("TYPE", MARGIN_LEFT + 220f, y + 12f, headerTextPaint)

        val headerRightPaint = Paint().apply {
            color = Color.parseColor("#475569")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText("AMOUNT", MARGIN_RIGHT - 80f, y + 12f, headerRightPaint)
        canvas.drawText("SHARE", MARGIN_RIGHT - 10f, y + 12f, headerRightPaint)

        y += 18f

        val rowBgPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            isAntiAlias = true
        }
        val cellTextPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        val cellRightPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        val typeBadgePaint = Paint().apply { isAntiAlias = true }
        val typeBadgeTextPaint = Paint().apply {
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        for (i in categories.indices) {
            context.checkOverflow(22f)
            val currentCanvas = context.canvas
            val currentLineY = y

            val cat = categories[i]
            if (i % 2 == 1) {
                currentCanvas.drawRect(RectF(MARGIN_LEFT, currentLineY, MARGIN_RIGHT, currentLineY + 18f), rowBgPaint)
            }

            // Category name
            currentCanvas.drawText(cat.category.name, MARGIN_LEFT + 8f, currentLineY + 12f, cellTextPaint)

            // Type badge
            val isNeed = cat.defaultType == ExpenseType.NEED
            typeBadgePaint.color = if (isNeed) Color.parseColor("#DCFCE7") else Color.parseColor("#FFEDD5")
            typeBadgeTextPaint.color = if (isNeed) Color.parseColor("#15803D") else Color.parseColor("#C2410C")

            val typeBadgeRect = RectF(MARGIN_LEFT + 220f, currentLineY + 3f, MARGIN_LEFT + 258f, currentLineY + 15f)
            currentCanvas.drawRoundRect(typeBadgeRect, 3f, 3f, typeBadgePaint)
            currentCanvas.drawText(if (isNeed) "Need" else "Waste", MARGIN_LEFT + 225f, currentLineY + 12f, typeBadgeTextPaint)

            // Amount
            currentCanvas.drawText(Money.format(cat.totalMinor, currencySymbol), MARGIN_RIGHT - 80f, currentLineY + 12f, cellRightPaint)

            // Share %
            val shareText = String.format(Locale.US, "%.1f%%", cat.sharePct)
            currentCanvas.drawText(shareText, MARGIN_RIGHT - 10f, currentLineY + 12f, cellRightPaint)

            y += 18f
        }

        context.currentY = y + 14f
    }

    private fun drawIncomeTable(
        context: PdfRenderContext,
        summary: PeriodSummary,
        currencySymbol: String,
    ) {
        val sources = summary.incomeBySource
        if (sources.isEmpty()) return

        context.checkOverflow(40f + (sources.size * 20f))

        val canvas = context.canvas
        var y = context.currentY

        val headingPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Income by Source", MARGIN_LEFT, y + 11f, headingPaint)
        y += 20f

        // Table Header
        val headerBg = Paint().apply {
            color = Color.parseColor("#EFF6FF")
            isAntiAlias = true
        }
        canvas.drawRect(RectF(MARGIN_LEFT, y, MARGIN_RIGHT, y + 18f), headerBg)

        val headerTextPaint = Paint().apply {
            color = Color.parseColor("#1E40AF")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("SOURCE", MARGIN_LEFT + 8f, y + 12f, headerTextPaint)

        val headerRightPaint = Paint().apply {
            color = Color.parseColor("#1E40AF")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        canvas.drawText("AMOUNT", MARGIN_RIGHT - 80f, y + 12f, headerRightPaint)
        canvas.drawText("SHARE", MARGIN_RIGHT - 10f, y + 12f, headerRightPaint)

        y += 18f

        val rowBgPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            isAntiAlias = true
        }
        val cellTextPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        val cellRightPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }

        for (i in sources.indices) {
            context.checkOverflow(22f)
            val currentCanvas = context.canvas
            val currentLineY = y

            val src = sources[i]
            if (i % 2 == 1) {
                currentCanvas.drawRect(RectF(MARGIN_LEFT, currentLineY, MARGIN_RIGHT, currentLineY + 18f), rowBgPaint)
            }

            currentCanvas.drawText(src.source.name, MARGIN_LEFT + 8f, currentLineY + 12f, cellTextPaint)
            currentCanvas.drawText(Money.format(src.totalMinor, currencySymbol), MARGIN_RIGHT - 80f, currentLineY + 12f, cellRightPaint)
            val shareText = String.format(Locale.US, "%.1f%%", src.sharePct)
            currentCanvas.drawText(shareText, MARGIN_RIGHT - 10f, currentLineY + 12f, cellRightPaint)

            y += 18f
        }

        context.currentY = y + 14f
    }

    private fun drawHighlights(
        context: PdfRenderContext,
        summary: PeriodSummary,
        currencySymbol: String,
    ) {
        context.checkOverflow(65f)

        val canvas = context.canvas
        val y = context.currentY

        val headingPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Period Highlights", MARGIN_LEFT, y + 11f, headingPaint)

        val boxY = y + 20f
        val boxHeight = 44f
        val boxGap = 10f
        val boxWidth = (CONTENT_WIDTH - (boxGap * 2f)) / 3f

        val bgPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            isAntiAlias = true
        }
        val borderPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }
        val labelPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val valPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        // 1. Peak Spending Day
        val peakLeft = MARGIN_LEFT
        canvas.drawRoundRect(RectF(peakLeft, boxY, peakLeft + boxWidth, boxY + boxHeight), 6f, 6f, bgPaint)
        canvas.drawRoundRect(RectF(peakLeft, boxY, peakLeft + boxWidth, boxY + boxHeight), 6f, 6f, borderPaint)
        canvas.drawText("PEAK SPENDING DAY", peakLeft + 10f, boxY + 16f, labelPaint)
        val peakVal = summary.peakDay?.let {
            "${it.date.dayOfMonth} ${it.date.month.name.take(3)} • ${Money.format(it.totalMinor, currencySymbol)}"
        } ?: "None"
        canvas.drawText(peakVal, peakLeft + 10f, boxY + 34f, valPaint)

        // 2. Average Daily Spend
        val avgLeft = peakLeft + boxWidth + boxGap
        canvas.drawRoundRect(RectF(avgLeft, boxY, avgLeft + boxWidth, boxY + boxHeight), 6f, 6f, bgPaint)
        canvas.drawRoundRect(RectF(avgLeft, boxY, avgLeft + boxWidth, boxY + boxHeight), 6f, 6f, borderPaint)
        canvas.drawText("AVG DAILY SPEND", avgLeft + 10f, boxY + 16f, labelPaint)
        val avgVal = Money.format(summary.avgDailyMinor, currencySymbol) + " / day"
        canvas.drawText(avgVal, avgLeft + 10f, boxY + 34f, valPaint)

        // 3. Clean Days
        val cleanLeft = avgLeft + boxWidth + boxGap
        canvas.drawRoundRect(RectF(cleanLeft, boxY, cleanLeft + boxWidth, boxY + boxHeight), 6f, 6f, bgPaint)
        canvas.drawRoundRect(RectF(cleanLeft, boxY, cleanLeft + boxWidth, boxY + boxHeight), 6f, 6f, borderPaint)
        canvas.drawText("CLEAN DAYS (ZERO SPEND)", cleanLeft + 10f, boxY + 16f, labelPaint)
        val cleanVal = "${summary.cleanDays} days"
        canvas.drawText(cleanVal, cleanLeft + 10f, boxY + 34f, valPaint)

        context.currentY = boxY + boxHeight + 16f
    }

    private fun drawInsights(
        context: PdfRenderContext,
        insights: List<Insight>,
    ) {
        val topInsights = insights.take(3)
        context.checkOverflow(30f + (topInsights.size * 34f))

        val canvas = context.canvas
        var y = context.currentY

        val headingPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Key Insights", MARGIN_LEFT, y + 11f, headingPaint)
        y += 20f

        val cardBgPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            isAntiAlias = true
        }
        val cardBorderPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }
        val titlePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val descPaint = Paint().apply {
            color = Color.parseColor("#475569")
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        for (insight in topInsights) {
            context.checkOverflow(32f)
            val currentCanvas = context.canvas
            val currentY = y

            currentCanvas.drawRoundRect(RectF(MARGIN_LEFT, currentY, MARGIN_RIGHT, currentY + 28f), 4f, 4f, cardBgPaint)
            currentCanvas.drawRoundRect(RectF(MARGIN_LEFT, currentY, MARGIN_RIGHT, currentY + 28f), 4f, 4f, cardBorderPaint)

            currentCanvas.drawText("💡  ${insight.title}", MARGIN_LEFT + 8f, currentY + 12f, titlePaint)
            val shortDesc = if (insight.message.length > 95) insight.message.take(92) + "..." else insight.message
            currentCanvas.drawText(shortDesc, MARGIN_LEFT + 8f, currentY + 23f, descPaint)

            y += 34f
        }

        context.currentY = y + 10f
    }
}
