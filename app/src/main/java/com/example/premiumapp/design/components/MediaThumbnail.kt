package com.example.premiumapp.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.premiumapp.core.extensions.formatDuration
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppRadius
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme
import com.example.premiumapp.domain.model.MediaItem

@Composable
fun MediaThumbnail(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val imageModel = item.thumbnailUri ?: item.localPath ?: item.uri

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(AppRadius.medium))
            .background(PremiumTheme.colors.surfaceVariant)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, PremiumTheme.colors.primary, RoundedCornerShape(AppRadius.medium))
                } else Modifier
            )
            .clickable(onClick = onClick)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageModel)
                .crossfade(true)
                .build(),
            contentDescription = item.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PremiumTheme.colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isVideo) AppIcons.VideoPlaceholder else AppIcons.ImagePlaceholder,
                        contentDescription = null,
                        tint = PremiumTheme.colors.textTertiary,
                        modifier = Modifier.size(AppIconSize.standard)
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PremiumTheme.colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isVideo) AppIcons.VideoPlaceholder else AppIcons.ImagePlaceholder,
                        contentDescription = null,
                        tint = PremiumTheme.colors.textTertiary,
                        modifier = Modifier.size(AppIconSize.standard)
                    )
                }
            }
        )

        // Video Badge with Duration
        if (item.isVideo) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(AppSpacing.xs)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.Play,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    if (item.durationMs != null && item.durationMs > 0) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(2.dp))
                        Text(
                            text = item.durationMs.formatDuration(),
                            style = PremiumTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Favorite Badge
        if (item.isFavorite && !isSelectionMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppSpacing.xs)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.FavoritesFilled,
                    contentDescription = "Favorited",
                    tint = PremiumTheme.colors.error,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Selection Checkmark
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppSpacing.xs)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) PremiumTheme.colors.primary else Color.Black.copy(alpha = 0.5f))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = AppIcons.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
