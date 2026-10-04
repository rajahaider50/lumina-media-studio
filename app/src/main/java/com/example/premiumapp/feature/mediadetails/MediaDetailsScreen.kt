package com.example.premiumapp.feature.mediadetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.premiumapp.core.extensions.formatDate
import com.example.premiumapp.core.extensions.formatDateTime
import com.example.premiumapp.core.extensions.formatDuration
import com.example.premiumapp.core.extensions.formatFileSize
import com.example.premiumapp.core.extensions.formatDimensions
import com.example.premiumapp.design.components.ButtonVariant
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.LoadingState
import com.example.premiumapp.design.components.PremiumButton
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumConfirmDialog
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppRadius
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsScreen(
    viewModel: MediaDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToViewer: (Long) -> Unit,
    onNavigateToEditor: (Long) -> Unit,
    onNavigateToExport: (Long) -> Unit,
    onNavigateToShare: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val media = state.mediaItem

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddToCollectionSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Media Details",
                navigationIcon = AppIcons.Back,
                navigationIconContentDescription = "Go Back",
                onNavigationClick = onNavigateBack,
                actions = {
                    if (media != null) {
                        PremiumIconButton(
                            icon = if (media.isFavorite) AppIcons.FavoritesFilled else AppIcons.FavoritesOutlined,
                            contentDescription = if (media.isFavorite) "Remove Favorite" else "Add Favorite",
                            onClick = { viewModel.toggleFavorite() },
                            tint = if (media.isFavorite) PremiumTheme.colors.error else PremiumTheme.colors.textPrimary
                        )
                        PremiumIconButton(
                            icon = AppIcons.Share,
                            contentDescription = "Share",
                            onClick = { onNavigateToShare(media.id) }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingState(modifier = Modifier.padding(innerPadding))
        } else if (media == null) {
            EmptyState(
                title = "Media Not Found",
                description = "This media item may have been moved or deleted.",
                icon = AppIcons.Error,
                actionText = "Go Back",
                onActionClick = onNavigateBack,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(AppSpacing.md)
            ) {
                // Media Preview Image/Thumbnail
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                        .clip(RoundedCornerShape(AppRadius.large))
                        .background(PremiumTheme.colors.surfaceVariant)
                        .clickable { onNavigateToViewer(media.id) },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(media.thumbnailUri ?: media.localPath ?: media.uri)
                            .crossfade(true)
                            .build(),
                        contentDescription = media.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (media.isVideo) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = AppIcons.Play,
                                contentDescription = "Play Video",
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(AppIconSize.large)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.md))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    PremiumButton(
                        text = "Open",
                        onClick = { onNavigateToViewer(media.id) },
                        modifier = Modifier.weight(1f),
                        variant = ButtonVariant.PRIMARY
                    )
                    PremiumButton(
                        text = "Edit",
                        onClick = { onNavigateToEditor(media.id) },
                        modifier = Modifier.weight(1f),
                        variant = ButtonVariant.SECONDARY
                    )
                    PremiumButton(
                        text = "Export",
                        onClick = { onNavigateToExport(media.id) },
                        modifier = Modifier.weight(1f),
                        variant = ButtonVariant.SECONDARY
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    PremiumButton(
                        text = "Add to Collection",
                        onClick = { showAddToCollectionSheet = true },
                        modifier = Modifier.weight(1f),
                        variant = ButtonVariant.SECONDARY
                    )
                    PremiumButton(
                        text = "Delete",
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        variant = ButtonVariant.DESTRUCTIVE
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Metadata Card
                Text(
                    text = "File Information",
                    style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = PremiumTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(AppSpacing.xs))

                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large,
                    backgroundColor = PremiumTheme.colors.surface
                ) {
                    MetadataRow("File Name", media.displayName)
                    MetadataRow("Type", if (media.isVideo) "Video" else "Image")
                    MetadataRow("MIME Type", media.mimeType)
                    MetadataRow("File Size", media.sizeBytes.formatFileSize())
                    MetadataRow("Dimensions", formatDimensions(media.width, media.height))
                    if (media.isVideo && media.durationMs != null) {
                        MetadataRow("Duration", media.durationMs.formatDuration())
                    }
                    MetadataRow("Date Added", media.dateAdded.formatDateTime())
                    MetadataRow("Date Modified", media.dateModified.formatDateTime())
                    if (media.localPath != null) {
                        MetadataRow("Location", media.localPath)
                    }
                }
            }
        }

        // Add to Collection Sheet
        if (showAddToCollectionSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddToCollectionSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = PremiumTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.lg)
                ) {
                    Text(
                        text = "Select Collection",
                        style = PremiumTheme.typography.titleLarge,
                        color = PremiumTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.md))

                    if (state.availableCollections.isEmpty()) {
                        Text(
                            text = "No collections created yet. Create a collection first from the Collections tab.",
                            style = PremiumTheme.typography.bodyMedium,
                            color = PremiumTheme.colors.textSecondary
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            items(state.availableCollections, key = { it.id }) { col ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.addToCollection(col.id)
                                            showAddToCollectionSheet = false
                                        }
                                        .padding(vertical = AppSpacing.sm),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = AppIcons.Folder,
                                        contentDescription = null,
                                        tint = PremiumTheme.colors.primary,
                                        modifier = Modifier.size(AppIconSize.standard)
                                    )
                                    Spacer(modifier = Modifier.size(AppSpacing.md))
                                    Column {
                                        Text(
                                            text = col.name,
                                            style = PremiumTheme.typography.titleMedium,
                                            color = PremiumTheme.colors.textPrimary
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
        }

        // Delete Dialog
        if (showDeleteDialog && media != null) {
            PremiumConfirmDialog(
                title = "Delete Media?",
                message = "Are you sure you want to permanently delete \"${media.displayName}\"? This action cannot be undone.",
                confirmText = "Delete",
                isDestructive = true,
                onConfirm = {
                    viewModel.deleteMedia(onNavigateBack)
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = PremiumTheme.typography.bodyMedium,
            color = PremiumTheme.colors.textSecondary,
            modifier = Modifier.padding(end = AppSpacing.md)
        )
        Text(
            text = value,
            style = PremiumTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = PremiumTheme.colors.textPrimary,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}
