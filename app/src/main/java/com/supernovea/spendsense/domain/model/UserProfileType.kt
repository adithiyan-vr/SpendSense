/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.domain.model

enum class UserProfileType {
    STUDENT,
    WORKING_PROFESSIONAL,
    OTHER;

    companion object {
        fun fromName(name: String?): UserProfileType {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: STUDENT
        }
    }
}
