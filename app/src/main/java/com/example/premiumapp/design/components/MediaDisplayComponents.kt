package com.example.premiumapp.design.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.premiumapp.design.dimensions.LuminaIconSize
import com.example.premiumapp.design.dimensions.LuminaRadius
import com.example.premiumapp.design.dimensions.LuminaSpacing
import com.example.premiumapp.design.dimensions.LuminaTouchTarget
import com.example.premiumapp.design.theme.LuminaRose
import com.example.premiumapp.design.theme.LuminaThemeTokens
import com.example.premiumapp.domain.model.MediaItem
import com.example.premiumapp.domain.model.MediaType

@Composable
fun MediaThumbnail(
    uri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val extended = LuminaThemeTokens.extended
    val context = LocalContext.current

    Box(
        modifier = modifier
            .background(extended.borderSubtle)
    ) {
        if (!uri.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Image,
                    contentDescription = null,
                    tint = extended.textTertiary,
                    modifier = Modifier.size(LuminaIconSize.large)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaGridItem(
    item: MediaItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onFavoriteToggle: (() -> Unit)? = null
) {
    val extended = LuminaThemeTokens.extended
    val thumbnailTarget = item.thumbnailUri ?: item.uri

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(LuminaRadius.small))
            .border(
                width = if (isSelected) 2.5.dp else 0.5.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else extended.borderColor,
                shape = RoundedCornerShape(LuminaRadius.small)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        MediaThumbnail(
            uri = thumbnailTarget,
            contentDescription = item.displayName,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay at bottom for text/badge readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA000000))
                    )
                )
                .padding(LuminaSpacing.xSmall)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (item.mediaType == MediaType.VIDEO) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Video",
                        tint = Color.White,
                        modifier = Modifier.size(LuminaIconSize.small)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.formattedDuration ?: "",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (item.isFavorite && !isSelectionMode) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = "Favorite",
                        tint = LuminaRose,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Selection checkbox overlay
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .padding(LuminaSpacing.xSmall)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = if (isSelected) "Selected" else "Not selected",
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                    modifier = Modifier
                        .size(LuminaIconSize.standard)
                        .background(Color(0x66000000), CircleShape)
                )
            }
        }
    }
}

@Composable
fun MediaListItem(
    item: MediaItem,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extended = LuminaThemeTokens.extended
    val thumbnailTarget = item.thumbnailUri ?: item.uri

    LuminaCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(LuminaRadius.small))
            ) {
                MediaThumbnail(
                    uri = thumbnailTarget,
                    contentDescription = item.displayName,
                    modifier = Modifier.fillMaxSize()
                )
                if (item.mediaType == MediaType.VIDEO) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color(0x99000000), RoundedCornerShape(topStart = 4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.formattedDuration ?: "",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(LuminaSpacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = "${item.resolutionText} • ${item.formattedSize}",
                    style = MaterialTheme.typography.bodySmall,
                    color = extended.textSecondary
                )
            }

            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier.size(LuminaTouchTarget.minimum)
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = if (item.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (item.isFavorite) LuminaRose else extended.textSecondary,
                    modifier = Modifier.size(LuminaIconSize.standard)
                )
            }
        }
    }
}
