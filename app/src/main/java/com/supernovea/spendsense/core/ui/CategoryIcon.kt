/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CastForEducation
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RealEstateAgent
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconProvider {
    fun getIcon(iconKey: String): ImageVector {
        return when (iconKey) {
            // Food and Drink
            "restaurant" -> Icons.Default.Restaurant
            "shopping_basket" -> Icons.Default.ShoppingBasket
            "fastfood" -> Icons.Default.Fastfood
            "local_cafe" -> Icons.Default.LocalCafe
            "local_dining" -> Icons.Default.LocalDining
            "delivery_dining" -> Icons.Default.DeliveryDining

            // Transport
            "directions_bus" -> Icons.Default.DirectionsBus
            "local_gas_station" -> Icons.Default.LocalGasStation
            "build" -> Icons.Default.Build
            "local_parking" -> Icons.Default.LocalParking
            "flight" -> Icons.Default.Flight

            // Education and Work
            "school" -> Icons.Default.School
            "menu_book" -> Icons.AutoMirrored.Filled.MenuBook
            "cast_for_education" -> Icons.Default.CastForEducation
            "print" -> Icons.Default.Print
            "terminal" -> Icons.Default.Terminal

            // Housing and Bills
            "home" -> Icons.Default.Home
            "bolt" -> Icons.Default.Bolt
            "receipt_long" -> Icons.AutoMirrored.Filled.ReceiptLong
            "wifi" -> Icons.Default.Wifi
            "handyman" -> Icons.Default.Handyman
            "local_laundry_service" -> Icons.Default.LocalLaundryService

            // Health and Personal
            "medical_services" -> Icons.Default.MedicalServices
            "shield" -> Icons.Default.Shield
            "spa" -> Icons.Default.Spa
            "fitness_center" -> Icons.Default.FitnessCenter

            // Lifestyle and Fun
            "movie" -> Icons.Default.Movie
            "sports_esports" -> Icons.Default.SportsEsports
            "checkroom" -> Icons.Default.Checkroom
            "devices" -> Icons.Default.Devices
            "shopping_bag" -> Icons.Default.ShoppingBag
            "subscriptions" -> Icons.Default.Subscriptions
            "palette" -> Icons.Default.Palette
            "celebration" -> Icons.Default.Celebration
            "groups" -> Icons.Default.Groups
            "redeem" -> Icons.Default.Redeem
            "smoking_rooms" -> Icons.Default.SmokingRooms

            // Money and Obligations
            "account_balance" -> Icons.Default.AccountBalance
            "request_quote" -> Icons.Default.RequestQuote
            "family_restroom" -> Icons.Default.FamilyRestroom
            "volunteer_activism" -> Icons.Default.VolunteerActivism
            "gavel" -> Icons.Default.Gavel

            // Family and Pets
            "child_care" -> Icons.Default.ChildCare
            "pets" -> Icons.Default.Pets

            // Income Sources
            "savings" -> Icons.Default.Savings
            "work_history" -> Icons.Default.WorkHistory
            "schedule" -> Icons.Default.Schedule
            "workspace_premium" -> Icons.Default.WorkspacePremium
            "emoji_events" -> Icons.Default.EmojiEvents
            "payments" -> Icons.Default.Payments
            "military_tech" -> Icons.Default.MilitaryTech
            "storefront" -> Icons.Default.Storefront
            "real_estate_agent" -> Icons.Default.RealEstateAgent
            "laptop_mac" -> Icons.Default.LaptopMac
            "trending_up" -> Icons.AutoMirrored.Filled.TrendingUp
            "card_giftcard" -> Icons.Default.CardGiftcard
            "undo" -> Icons.AutoMirrored.Filled.Undo
            "add_card" -> Icons.Default.AddCard

            else -> Icons.Default.Category
        }
    }

    fun parseColorHex(hex: String, fallback: Color = Color(0xFF6366F1)): Color {
        return try {
            val cleanHex = hex.removePrefix("#")
            val colorLong = cleanHex.toLong(16)
            if (cleanHex.length == 6) {
                Color(colorLong or 0xFF000000L)
            } else if (cleanHex.length == 8) {
                Color(colorLong)
            } else {
                fallback
            }
        } catch (_: Exception) {
            fallback
        }
    }
}
