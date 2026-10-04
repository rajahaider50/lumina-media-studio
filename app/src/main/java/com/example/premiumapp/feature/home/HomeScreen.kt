package com.example.premiumapp.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.premiumapp.core.extensions.formatFileSize
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.MediaThumbnail
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.components.SectionHeader
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppRadius
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToMediaViewer: (Long) -> Unit,
    onNavigateToImport: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCreateCollection: () -> Unit,
    onNavigateToCollectionDetails: (Long) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Lumina Studio",
                subtitle = "Welcome back, ${state.userName}",
                actions = {
                    PremiumIconButton(
                        icon = AppIcons.Search,
                        contentDescription = "Search",
                        onClick = onNavigateToSearch
                    )
                    PremiumIconButton(
                        icon = AppIcons.Settings,
                        contentDescription = "Settings",
                        onClick = onNavigateToSettings
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = AppSpacing.huge)
        ) {
            // Storage Overview Card
            val totalBytes = state.storageStats.totalStorageBytes.coerceAtLeast(1L)
            val usedBytes = state.storageStats.usedStorageBytes
            val progress = (usedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)

            PremiumCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                shape = AppShapes.extraLarge,
                backgroundColor = PremiumTheme.colors.surface,
                onClick = onNavigateToStorage
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Device Storage",
                            style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = PremiumTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.storageStats.freeStorageBytes.formatFileSize()} free of ${state.storageStats.totalStorageBytes.formatFileSize()}",
                            style = PremiumTheme.typography.bodySmall,
                            color = PremiumTheme.colors.textSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PremiumTheme.colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = AppIcons.Storage,
                            contentDescription = null,
                            tint = PremiumTheme.colors.primary,
                            modifier = Modifier.size(AppIconSize.standard)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.md))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (progress > 0.9f) PremiumTheme.colors.error else PremiumTheme.colors.primary,
                    trackColor = PremiumTheme.colors.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${state.totalMediaCount} Library Items",
                        style = PremiumTheme.typography.labelSmall,
                        color = PremiumTheme.colors.textSecondary
                    )
                    Text(
                        text = "App media: ${state.storageStats.mediaStorageBytes.formatFileSize()}",
                        style = PremiumTheme.typography.labelSmall,
                        color = PremiumTheme.colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Quick Actions Row
            Text(
                text = "Quick Actions",
                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = PremiumTheme.colors.textPrimary,
                modifier = Modifier.padding(horizontal = AppSpacing.md)
            )

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = AppSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                HomeQuickActionChip(
                    icon = AppIcons.Import,
                    title = "Import Media",
                    onClick = onNavigateToImport
                )
                HomeQuickActionChip(
                    icon = AppIcons.Add,
                    title = "New Collection",
                    onClick = onNavigateToCreateCollection
                )
                HomeQuickActionChip(
                    icon = AppIcons.FavoritesFilled,
                    title = "Favorites",
                    onClick = onNavigateToFavorites
                )
                HomeQuickActionChip(
                    icon = AppIcons.ExploreFilled,
                    title = "Explore All",
                    onClick = onNavigateToExplore
                )
                HomeQuickActionChip(
                    icon = AppIcons.Storage,
                    title = "Storage",
                    onClick = onNavigateToStorage
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            // Recent Media Section
            SectionHeader(
                title = "Recent Media",
                actionText = if (state.recentMedia.isNotEmpty()) "See All" else null,
                onActionClick = onNavigateToExplore
            )

            if (state.recentMedia.isEmpty()) {
                EmptyState(
                    title = "No Media Yet",
                    description = "Import your photos and videos to start creating your personal library.",
                    icon = AppIcons.ImagePlaceholder,
                    actionText = "Import Media",
                    onActionClick = onNavigateToImport,
                    modifier = Modifier.padding(vertical = AppSpacing.md)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = AppSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    state.recentMedia.forEach { item ->
                        Box(modifier = Modifier.size(130.dp)) {
                            MediaThumbnail(
                                item = item,
                                onClick = { onNavigateToMediaViewer(item.id) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            // Favorites Section
            if (state.favoriteMedia.isNotEmpty()) {
                SectionHeader(
                    title = "Favorites",
                    actionText = "See All",
                    onActionClick = onNavigateToFavorites
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = AppSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    state.favoriteMedia.forEach { item ->
                        Box(modifier = Modifier.size(130.dp)) {
                            MediaThumbnail(
                                item = item,
                                onClick = { onNavigateToMediaViewer(item.id) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(AppSpacing.lg))
            }

            // Collections Preview
            if (state.collections.isNotEmpty()) {
                SectionHeader(
                    title = "Collections",
                    actionText = "See All",
                    onActionClick = onNavigateToExplore
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = AppSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    state.collections.forEach { col ->
                        PremiumCard(
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { onNavigateToCollectionDetails(col.id) },
                            shape = AppShapes.medium,
                            backgroundColor = PremiumTheme.colors.surface,
                            contentPadding = AppSpacing.sm
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(AppRadius.small))
                                    .background(PremiumTheme.colors.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = AppIcons.Folder,
                                    contentDescription = null,
                                    tint = PremiumTheme.colors.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = col.name,
                                style = PremiumTheme.typography.titleSmall,
                                color = PremiumTheme.colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${col.mediaCount} items",
                                style = PremiumTheme.typography.bodySmall,
                                color = PremiumTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeQuickActionChip(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(AppRadius.medium))
            .clickable(onClick = onClick)
            .padding(AppSpacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(PremiumTheme.colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PremiumTheme.colors.primary,
                modifier = Modifier.size(AppIconSize.standard)
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = title,
            style = PremiumTheme.typography.labelSmall,
            color = PremiumTheme.colors.textPrimary,
            maxLines = 1
        )
    }
}
