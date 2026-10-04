package com.example.premiumapp.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = PremiumTheme.colors.textPrimary
        )
        if (actionText != null && onActionClick != null) {
            PremiumButton(
                text = actionText,
                onClick = onActionClick,
                variant = ButtonVariant.TEXT
            )
        }
    }
}
