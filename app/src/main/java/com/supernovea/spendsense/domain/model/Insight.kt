/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

enum class InsightSeverity {
    INFO,
    WARNING,
    POSITIVE,
}

data class Insight(
    val id: String,
    val title: String,
    val message: String,
    val severity: InsightSeverity,
)
