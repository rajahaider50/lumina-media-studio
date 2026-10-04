package com.example.premiumapp.feature.share

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.core.extensions.formatFileSize
import com.example.premiumapp.design.components.ButtonVariant
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.MediaThumbnail
import com.example.premiumapp.design.components.PremiumButton
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.components.SectionHeader
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun ShareExportScreen(
    viewModel: ShareExportViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToViewer: ((Long) -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsState()
    val media = state.mediaItem
    val context = LocalContext.current

    // SAF Create Document launcher for custom folder export
    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(media?.mimeType ?: "application/octet-stream")
    ) { destinationUri ->
        if (destinationUri != null) {
            viewModel.exportToDestinationUri(destinationUri)
        }
    }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Share & Export",
                navigationIcon = AppIcons.Back,
                navigationIconContentDescription = "Go Back",
                onNavigationClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        if (media == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PremiumTheme.colors.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(AppSpacing.md)
            ) {
                // Media Summary Card
                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large,
                    backgroundColor = PremiumTheme.colors.surface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(72.dp)) {
                            MediaThumbnail(
                                item = media,
                                onClick = { onNavigateToViewer?.invoke(media.id) }
                            )
                        }
                        Spacer(modifier = Modifier.size(AppSpacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = media.displayName,
                                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = PremiumTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${if (media.isVideo) "Video" else "Image"} • ${media.sizeBytes.formatFileSize()}",
                                style = PremiumTheme.typography.bodySmall,
                                color = PremiumTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Feedback Banner
                if (state.exportSuccessMessage != null) {
                    PremiumCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.medium,
                        backgroundColor = PremiumTheme.colors.success.copy(alpha = 0.15f),
                        borderColor = PremiumTheme.colors.success
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = AppIcons.Success, contentDescription = null, tint = PremiumTheme.colors.success)
                            Spacer(modifier = Modifier.size(AppSpacing.sm))
                            Text(
                                text = state.exportSuccessMessage!!,
                                style = PremiumTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = PremiumTheme.colors.success
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                }

                if (state.errorMessage != null) {
                    PremiumCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.medium,
                        backgroundColor = PremiumTheme.colors.error.copy(alpha = 0.15f),
                        borderColor = PremiumTheme.colors.error
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = AppIcons.Error, contentDescription = null, tint = PremiumTheme.colors.error)
                            Spacer(modifier = Modifier.size(AppSpacing.sm))
                            Text(
                                text = state.errorMessage!!,
                                style = PremiumTheme.typography.bodyMedium,
                                color = PremiumTheme.colors.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                }

                SectionHeader(title = "Choose Destination")

                // Option 1: Android System Sharesheet
                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large,
                    backgroundColor = PremiumTheme.colors.surface,
                    onClick = {
                        val shareIntent = viewModel.getShareIntent()
                        if (shareIntent != null) {
                            context.startActivity(Intent.createChooser(shareIntent, "Share with"))
                        }
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = AppIcons.Share,
                            contentDescription = null,
                            tint = PremiumTheme.colors.primary,
                            modifier = Modifier.size(AppIconSize.large)
                        )
                        Spacer(modifier = Modifier.size(AppSpacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "System Sharesheet",
                                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = PremiumTheme.colors.textPrimary
                            )
                            Text(
                                text = "Send to WhatsApp, Drive, Gmail, or nearby devices using secure FileProvider URI.",
                                style = PremiumTheme.typography.bodySmall,
                                color = PremiumTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                // Option 2: Save to Device Gallery
                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large,
                    backgroundColor = PremiumTheme.colors.surface,
                    onClick = { viewModel.exportToGallery() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = AppIcons.Export,
                            contentDescription = null,
                            tint = PremiumTheme.colors.primary,
                            modifier = Modifier.size(AppIconSize.large)
                        )
                        Spacer(modifier = Modifier.size(AppSpacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Save to Device Photos / Gallery",
                                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = PremiumTheme.colors.textPrimary
                            )
                            Text(
                                text = "Export into Android MediaStore Pictures/Movies album to make it visible across other apps.",
                                style = PremiumTheme.typography.bodySmall,
                                color = PremiumTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                // Option 3: Save to Custom File Location (SAF)
                PremiumCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.large,
                    backgroundColor = PremiumTheme.colors.surface,
                    onClick = { createDocLauncher.launch(media.displayName) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = AppIcons.Folder,
                            contentDescription = null,
                            tint = PremiumTheme.colors.primary,
                            modifier = Modifier.size(AppIconSize.large)
                        )
                        Spacer(modifier = Modifier.size(AppSpacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Save to Files / Custom Folder",
                                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = PremiumTheme.colors.textPrimary
                            )
                            Text(
                                text = "Pick a specific folder or external SD card directory to save a copy.",
                                style = PremiumTheme.typography.bodySmall,
                                color = PremiumTheme.colors.textSecondary
                            )
                        }
                    }
                }

                if (state.isExporting) {
                    Spacer(modifier = Modifier.height(AppSpacing.xl))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PremiumTheme.colors.primary)
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                        Text("Exporting file...", style = PremiumTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
