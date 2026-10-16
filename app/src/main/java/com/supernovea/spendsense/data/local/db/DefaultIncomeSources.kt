/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.db

import com.supernovea.spendsense.data.local.entity.IncomeSourceEntity

object DefaultIncomeSources {
    val items = listOf(
        IncomeSourceEntity(
            defaultKey = "pocket_money",
            name = "Pocket Money / Allowance",
            iconKey = "savings",
            colorHex = "#10B981",
            isDefault = true,
            sortOrder = 1,
        ),
        IncomeSourceEntity(
            defaultKey = "stipend",
            name = "Stipend / Internship",
            iconKey = "work_history",
            colorHex = "#06B6D4",
            isDefault = true,
            sortOrder = 2,
        ),
        IncomeSourceEntity(
            defaultKey = "part_time",
            name = "Part-time Job",
            iconKey = "schedule",
            colorHex = "#3B82F6",
            isDefault = true,
            sortOrder = 3,
        ),
        IncomeSourceEntity(
            defaultKey = "scholarship",
            name = "Scholarship and Grants",
            iconKey = "workspace_premium",
            colorHex = "#8B5CF6",
            isDefault = true,
            sortOrder = 4,
        ),
        IncomeSourceEntity(
            defaultKey = "prizes",
            name = "Prizes and Hackathons",
            iconKey = "emoji_events",
            colorHex = "#F59E0B",
            isDefault = true,
            sortOrder = 5,
        ),
        IncomeSourceEntity(
            defaultKey = "salary",
            name = "Salary",
            iconKey = "payments",
            colorHex = "#059669",
            isDefault = true,
            sortOrder = 6,
        ),
        IncomeSourceEntity(
            defaultKey = "bonus",
            name = "Bonus and Incentives",
            iconKey = "military_tech",
            colorHex = "#D97706",
            isDefault = true,
            sortOrder = 7,
        ),
        IncomeSourceEntity(
            defaultKey = "business",
            name = "Business Income",
            iconKey = "storefront",
            colorHex = "#2563EB",
            isDefault = true,
            sortOrder = 8,
        ),
        IncomeSourceEntity(
            defaultKey = "rental_income",
            name = "Rental Income",
            iconKey = "real_estate_agent",
            colorHex = "#7C3AED",
            isDefault = true,
            sortOrder = 9,
        ),
        IncomeSourceEntity(
            defaultKey = "freelance",
            name = "Freelance and Side Projects",
            iconKey = "laptop_mac",
            colorHex = "#0D9488",
            isDefault = true,
            sortOrder = 10,
        ),
        IncomeSourceEntity(
            defaultKey = "interest_returns",
            name = "Interest and Investment Returns",
            iconKey = "trending_up",
            colorHex = "#16A34A",
            isDefault = true,
            sortOrder = 11,
        ),
        IncomeSourceEntity(
            defaultKey = "gifts_received",
            name = "Gifts Received",
            iconKey = "card_giftcard",
            colorHex = "#EC4899",
            isDefault = true,
            sortOrder = 12,
        ),
        IncomeSourceEntity(
            defaultKey = "refunds",
            name = "Refunds and Reimbursements",
            iconKey = "undo",
            colorHex = "#64748B",
            isDefault = true,
            sortOrder = 13,
        ),
        IncomeSourceEntity(
            defaultKey = "other_income",
            name = "Other Income",
            iconKey = "add_card",
            colorHex = "#475569",
            isDefault = true,
            sortOrder = 14,
        ),
    )
}
