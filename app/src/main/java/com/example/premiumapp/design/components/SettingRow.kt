package com.example.premiumapp.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.dimensions.AppTouchTarget
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun SettingRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = PremiumTheme.colors.primary,
    trailingText: String? = null,
    showChevron: Boolean = true,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else if (checked != null && onCheckedChange != null) {
        modifier.clickable { onCheckedChange(!checked) }
    } else {
        modifier
    }

    Row(
        modifier = clickableModifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(AppIconSize.standard),
                    tint = iconTint
                )
            }
            Spacer(modifier = Modifier.width(AppSpacing.md))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = PremiumTheme.colors.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = PremiumTheme.typography.bodySmall,
                    color = PremiumTheme.colors.textSecondary
                )
            }
        }

        if (trailingText != null) {
            Text(
                text = trailingText,
                style = PremiumTheme.typography.bodyMedium,
                color = PremiumTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.width(AppSpacing.xs))
        }

        if (checked != null && onCheckedChange != null) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PremiumTheme.colors.primary,
                    uncheckedThumbColor = PremiumTheme.colors.textTertiary,
                    uncheckedTrackColor = PremiumTheme.colors.surfaceVariant
                )
            )
        } else if (showChevron && onClick != null) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(AppIconSize.standard),
                tint = PremiumTheme.colors.textTertiary
            )
        }
    }
}
