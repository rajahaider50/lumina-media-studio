package com.example.premiumapp.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.dimensions.AppRadius

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(AppRadius.small),
    small = RoundedCornerShape(AppRadius.small),
    medium = RoundedCornerShape(AppRadius.medium),
    large = RoundedCornerShape(AppRadius.large),
    extraLarge = RoundedCornerShape(AppRadius.extraLarge)
)
