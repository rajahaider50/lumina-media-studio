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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.premiumapp.core.extensions.formatDate
import com.example.premiumapp.core.extensions.formatFileSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme
import com.example.premiumapp.domain.model.MediaItem

@Composable
fun MediaListItem(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onFavoriteToggle: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelectionMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PremiumTheme.colors.primary,
                    uncheckedColor = PremiumTheme.colors.textTertiary
                )
            )
            Spacer(modifier = Modifier.width(AppSpacing.xs))
        }

        Box(modifier = Modifier.size(64.dp)) {
            MediaThumbnail(
                item = item,
                onClick = onClick,
                isSelected = isSelected,
                isSelectionMode = false
            )
        }

        Spacer(modifier = Modifier.width(AppSpacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.displayName,
                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = PremiumTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${if (item.isVideo) "Video" else "Image"} • ${item.sizeBytes.formatFileSize()} • ${item.dateAdded.formatDate()}",
                style = PremiumTheme.typography.bodySmall,
                color = PremiumTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (onFavoriteToggle != null && !isSelectionMode) {
            PremiumIconButton(
                icon = if (item.isFavorite) AppIcons.FavoritesFilled else AppIcons.FavoritesOutlined,
                contentDescription = if (item.isFavorite) "Remove from favorites" else "Add to favorites",
                onClick = onFavoriteToggle,
                tint = if (item.isFavorite) PremiumTheme.colors.error else PremiumTheme.colors.textTertiary
            )
        }
    }
}
