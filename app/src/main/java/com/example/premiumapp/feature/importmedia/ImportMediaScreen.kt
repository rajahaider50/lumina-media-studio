package com.example.premiumapp.feature.importmedia

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.components.ButtonVariant
import com.example.premiumapp.design.components.MediaListItem
import com.example.premiumapp.design.components.PremiumButton
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun ImportMediaScreen(
    viewModel: ImportMediaViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMediaViewer: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Android Photo Picker Launcher (Modern Android 13+)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.importUris(uris)
        }
    }

    // Storage Access Framework (SAF) Documents Launcher
    val safLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.importUris(uris)
        }
    }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Import Media",
                navigationIcon = AppIcons.Back,
                navigationIconContentDescription = "Go Back",
                onNavigationClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(AppSpacing.md)
        ) {
            // Import Options Cards
            if (!state.isImporting) {
                Text(
                    text = "Select Import Source",
                    style = PremiumTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = PremiumTheme.colors.textPrimary
                )
                Text(
                    text = "Import photos and videos into your private local vault safely.",
                    style = PremiumTheme.typography.bodyMedium,
                    color = PremiumTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(AppSpacing.md))

                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large,
                    backgroundColor = PremiumTheme.colors.surface,
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = AppIcons.ImagePlaceholder,
                            contentDescription = null,
                            tint = PremiumTheme.colors.primary,
                            modifier = Modifier.size(AppIconSize.large)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Photo & Video Picker",
                                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = PremiumTheme.colors.textPrimary
                            )
                            Text(
                                text = "Recommended: Select directly from system photo album with zero storage permissions required.",
                                style = PremiumTheme.typography.bodySmall,
                                color = PremiumTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.md))

                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large,
                    backgroundColor = PremiumTheme.colors.surface,
                    onClick = {
                        safLauncher.launch(arrayOf("image/*", "video/*"))
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = AppIcons.Folder,
                            contentDescription = null,
                            tint = PremiumTheme.colors.primary,
                            modifier = Modifier.size(AppIconSize.large)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Browse Files (Storage Access Framework)",
                                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = PremiumTheme.colors.textPrimary
                            )
                            Text(
                                text = "Select photos or videos from Downloads, SD card, or any specific folder on your device.",
                                style = PremiumTheme.typography.bodySmall,
                                color = PremiumTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.md))

                val context = LocalContext.current
                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large,
                    backgroundColor = PremiumTheme.colors.surface,
                    onClick = {
                        viewModel.seedDemoAssets(context)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = AppIcons.Sparkles,
                            contentDescription = null,
                            tint = PremiumTheme.colors.secondary,
                            modifier = Modifier.size(AppIconSize.large)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Load Demo Studio Assets",
                                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = PremiumTheme.colors.textPrimary
                            )
                            Text(
                                text = "Instantly generate curated high-res photography artwork to test library features, viewer, and editor.",
                                style = PremiumTheme.typography.bodySmall,
                                color = PremiumTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            // Progress Banner
            if (state.isImporting) {
                Spacer(modifier = Modifier.height(AppSpacing.xl))
                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large,
                    backgroundColor = PremiumTheme.colors.surface
                ) {
                    Text(
                        text = "Importing Media...",
                        style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = PremiumTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Processing ${state.currentItemIndex} of ${state.totalItems}",
                        style = PremiumTheme.typography.bodySmall,
                        color = PremiumTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.md))

                    val progress = if (state.totalItems > 0) {
                        state.currentItemIndex.toFloat() / state.totalItems.toFloat()
                    } else 0f

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = PremiumTheme.colors.primary,
                        trackColor = PremiumTheme.colors.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            // Completed Summary
            if (state.successfulImports.isNotEmpty() || state.failedCount > 0) {
                Spacer(modifier = Modifier.height(AppSpacing.xl))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Import Results (${state.successfulImports.size} added${if (state.failedCount > 0) ", ${state.failedCount} failed" else ""})",
                        style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = PremiumTheme.colors.textPrimary
                    )
                    PremiumButton(
                        text = "Clear Results",
                        onClick = { viewModel.resetState() },
                        variant = ButtonVariant.TEXT
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppSpacing.xl)
                ) {
                    items(state.successfulImports, key = { it.id }) { item ->
                        MediaListItem(
                            item = item,
                            onClick = { onNavigateToMediaViewer(item.id) }
                        )
                    }
                }
            }
        }
    }
}
