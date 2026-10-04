package com.example.premiumapp.design.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.dimensions.AppRadius
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.dimensions.AppTouchTarget
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    DESTRUCTIVE,
    TEXT
}

@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val buttonModifier = modifier.defaultMinSize(minHeight = AppTouchTarget.minSize)

    when (variant) {
        ButtonVariant.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = AppShapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PremiumTheme.colors.primary,
                    contentColor = Color.White,
                    disabledContainerColor = PremiumTheme.colors.border,
                    disabledContentColor = PremiumTheme.colors.textTertiary
                ),
                contentPadding = PaddingValues(horizontal = AppSpacing.lg, vertical = AppSpacing.sm)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.androidx.compose.foundation.layout.width(AppSpacing.xs))
                }
                Text(
                    text = text,
                    style = PremiumTheme.typography.labelLarge
                )
            }
        }
        ButtonVariant.SECONDARY -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = AppShapes.medium,
                border = BorderStroke(1.dp, PremiumTheme.colors.border),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PremiumTheme.colors.textPrimary
                ),
                contentPadding = PaddingValues(horizontal = AppSpacing.lg, vertical = AppSpacing.sm)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.androidx.compose.foundation.layout.width(AppSpacing.xs))
                }
                Text(
                    text = text,
                    style = PremiumTheme.typography.labelLarge
                )
            }
        }
        ButtonVariant.DESTRUCTIVE -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = AppShapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PremiumTheme.colors.error,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = AppSpacing.lg, vertical = AppSpacing.sm)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.androidx.compose.foundation.layout.width(AppSpacing.xs))
                }
                Text(
                    text = text,
                    style = PremiumTheme.typography.labelLarge
                )
            }
        }
        ButtonVariant.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = PremiumTheme.colors.primary
                ),
                contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.xs)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.androidx.compose.foundation.layout.width(AppSpacing.xs))
                }
                Text(
                    text = text,
                    style = PremiumTheme.typography.labelLarge
                )
            }
        }
    }
}
