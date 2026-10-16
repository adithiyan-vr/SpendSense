/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

enum class CategoryGroup(val key: String, val title: String) {
    FOOD_DRINK("food_drink", "Food and Drink"),
    TRANSPORT("transport", "Transport"),
    EDUCATION_WORK("education_work", "Education and Work"),
    HOUSING_BILLS("housing_bills", "Housing and Bills"),
    HEALTH_PERSONAL("health_personal", "Health and Personal"),
    LIFESTYLE_FUN("lifestyle_fun", "Lifestyle and Fun"),
    MONEY_OBLIGATIONS("money_obligations", "Money and Obligations"),
    FAMILY_PETS("family_pets", "Family and Pets"),
    OTHER("other", "Other");

    companion object {
        fun fromKey(key: String): CategoryGroup {
            return entries.find { it.key.equals(key, ignoreCase = true) } ?: OTHER
        }
    }
}
