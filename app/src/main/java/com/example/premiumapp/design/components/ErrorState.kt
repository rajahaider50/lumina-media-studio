package com.example.premiumapp.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun ErrorState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = AppIcons.Error,
    retryText: String = "Try Again",
    onRetry: (() -> Unit)? = null,
    secondaryText: String? = null,
    onSecondaryAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppSpacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(PremiumTheme.colors.error.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Error",
                modifier = Modifier.size(AppIconSize.large),
                tint = PremiumTheme.colors.error
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        Text(
            text = title,
            style = PremiumTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = PremiumTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        Text(
            text = message,
            style = PremiumTheme.typography.bodyMedium,
            color = PremiumTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (secondaryText != null && onSecondaryAction != null) {
                PremiumButton(
                    text = secondaryText,
                    onClick = onSecondaryAction,
                    variant = ButtonVariant.SECONDARY
                )
                Spacer(modifier = Modifier.width(AppSpacing.md))
            }
            if (onRetry != null) {
                PremiumButton(
                    text = retryText,
                    onClick = onRetry,
                    variant = ButtonVariant.PRIMARY
                )
            }
        }
    }
}
