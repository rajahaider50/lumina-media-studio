package com.example.premiumapp.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.dimensions.LuminaIconSize
import com.example.premiumapp.design.dimensions.LuminaRadius
import com.example.premiumapp.design.dimensions.LuminaSpacing
import com.example.premiumapp.design.dimensions.LuminaTouchTarget
import com.example.premiumapp.design.theme.LuminaCyan
import com.example.premiumapp.design.theme.LuminaThemeTokens
import com.example.premiumapp.design.theme.StatusError

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Inbox,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val extended = LuminaThemeTokens.extended

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(LuminaSpacing.xxLarge),
        horizontalAlignment = Alignment.CenterVertically,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(LuminaIconSize.hero)
            )
        }

        Spacer(modifier = Modifier.height(LuminaSpacing.medium))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(LuminaSpacing.xSmall))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = extended.textSecondary,
            textAlign = TextAlign.Center
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(LuminaSpacing.large))
            LuminaPrimaryButton(
                text = actionText,
                onClick = onActionClick
            )
        }
    }
}

@Composable
fun ErrorState(
    title: String = "Something Went Wrong",
    message: String,
    modifier: Modifier = Modifier,
    onRetryClick: (() -> Unit)? = null
) {
    val extended = LuminaThemeTokens.extended

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(LuminaSpacing.xxLarge),
        horizontalAlignment = Alignment.CenterVertically,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(StatusError.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = null,
                tint = StatusError,
                modifier = Modifier.size(LuminaIconSize.hero)
            )
        }

        Spacer(modifier = Modifier.height(LuminaSpacing.medium))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(LuminaSpacing.xSmall))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = extended.textSecondary,
            textAlign = TextAlign.Center
        )

        if (onRetryClick != null) {
            Spacer(modifier = Modifier.height(LuminaSpacing.large))
            LuminaPrimaryButton(
                text = "Try Again",
                onClick = onRetryClick
            )
        }
    }
}

@Composable
fun LuminaDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            if (isDestructive) {
                LuminaDestructiveButton(
                    text = confirmText,
                    onClick = onConfirm
                )
            } else {
                LuminaPrimaryButton(
                    text = confirmText,
                    onClick = onConfirm
                )
            }
        },
        dismissButton = {
            LuminaOutlinedButton(
                text = dismissText,
                onClick = onDismiss
            )
        },
        shape = RoundedCornerShape(LuminaRadius.large),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun SettingRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    trailingText: String? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val extended = LuminaThemeTokens.extended

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LuminaRadius.medium))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = LuminaSpacing.small, horizontal = LuminaSpacing.medium)
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(LuminaIconSize.standard)
                )
            }
            Spacer(modifier = Modifier.width(LuminaSpacing.medium))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = extended.textSecondary
                )
            }
        }

        if (checked != null && onCheckedChange != null) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        } else if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodyMedium,
                color = extended.textSecondary
            )
            if (onClick != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = extended.textTertiary,
                    modifier = Modifier.size(LuminaIconSize.small)
                )
            }
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = extended.textTertiary,
                modifier = Modifier.size(LuminaIconSize.standard)
            )
        }
    }
}

@Composable
fun StorageBar(
    usedRatio: Float,
    appMediaRatio: Float,
    appCacheRatio: Float,
    modifier: Modifier = Modifier
) {
    val extended = LuminaThemeTokens.extended

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(LuminaRadius.full))
                .background(extended.borderSubtle)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val clampedMedia = appMediaRatio.coerceIn(0f, 1f)
                val clampedCache = appCacheRatio.coerceIn(0f, 1f)
                val otherUsed = (usedRatio - clampedMedia - clampedCache).coerceIn(0f, 1f)

                if (clampedMedia > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(clampedMedia.coerceAtLeast(0.01f))
                            .height(10.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
                if (clampedCache > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(clampedCache.coerceAtLeast(0.01f))
                            .height(10.dp)
                            .background(LuminaCyan)
                    )
                }
                if (otherUsed > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(otherUsed.coerceAtLeast(0.01f))
                            .height(10.dp)
                            .background(extended.textTertiary)
                    )
                }
                val freeRatio = (1f - usedRatio).coerceIn(0f, 1f)
                if (freeRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(freeRatio.coerceAtLeast(0.01f))
                            .height(10.dp)
                            .background(extended.borderSubtle)
                    )
                }
            }
        }
    }
}
