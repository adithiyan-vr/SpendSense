package com.supernovea.spendsense.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val SpendSenseShapes = Shapes(
    small = RoundedCornerShape(12.dp),      // Chips
    medium = RoundedCornerShape(16.dp),     // Cards
    large = RoundedCornerShape(20.dp),      // Buttons
    extraLarge = RoundedCornerShape(28.dp), // Sheets, Dialogs
)
