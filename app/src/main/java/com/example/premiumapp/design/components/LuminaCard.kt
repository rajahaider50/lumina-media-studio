package com.example.premiumapp.design.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.dimensions.LuminaRadius
import com.example.premiumapp.design.dimensions.LuminaSpacing
import com.example.premiumapp.design.theme.LuminaThemeTokens

@Composable
fun LuminaCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(LuminaRadius.medium),
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val extended = LuminaThemeTokens.extended

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = extended.cardBackground
        ),
        border = BorderStroke(borderWidth, extended.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Box(
            modifier = Modifier.padding(LuminaSpacing.medium),
            content = content
        )
    }
}
