package com.example.premiumapp.feature.editor

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.premiumapp.core.extensions.formatDuration
import com.example.premiumapp.design.components.ButtonVariant
import com.example.premiumapp.design.components.EmptyState
import com.example.premiumapp.design.components.PremiumButton
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppRadius
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMediaViewer: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val media = state.mediaItem
    var selectedToolTab by remember { mutableStateOf("transform") }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = if (media?.isVideo == true) "Trim Video" else "Edit Photo",
                navigationIcon = AppIcons.Close,
                navigationIconContentDescription = "Cancel",
                onNavigationClick = onNavigateBack,
                actions = {
                    PremiumButton(
                        text = "Reset",
                        onClick = { viewModel.resetEdits() },
                        variant = ButtonVariant.TEXT
                    )
                    PremiumButton(
                        text = if (state.isProcessing) "Saving..." else "Save Copy",
                        onClick = { viewModel.saveCopy(onNavigateToMediaViewer) },
                        enabled = !state.isProcessing,
                        variant = ButtonVariant.PRIMARY
                    )
                }
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
            ) {
                // Media Preview Area with Applied Transformations
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    val scaleX = if (state.imageParams.flipHorizontal) -1f else 1f
                    val scaleY = if (state.imageParams.flipVertical) -1f else 1f

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(media.localPath ?: media.uri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Edited Preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppSpacing.md)
                            .graphicsLayer(
                                rotationZ = state.imageParams.rotationDegrees,
                                scaleX = scaleX,
                                scaleY = scaleY
                            )
                    )

                    if (state.isProcessing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color.White)
                                Spacer(modifier = Modifier.height(AppSpacing.md))
                                Text("Processing media copy...", color = Color.White)
                            }
                        }
                    }
                }

                // Editor Controls Panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PremiumTheme.colors.surface)
                        .padding(AppSpacing.md)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (media.isVideo) {
                        // Video Trimmer Controls
                        val totalDuration = (media.durationMs ?: 1L).toFloat()
                        Text(
                            text = "Trim Video Range",
                            style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = PremiumTheme.colors.textPrimary
                        )
                        Text(
                            text = "Keep segment from ${state.trimStartMs.formatDuration()} to ${state.trimEndMs.formatDuration()}",
                            style = PremiumTheme.typography.bodySmall,
                            color = PremiumTheme.colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.sm))

                        val startFloat = state.trimStartMs.toFloat()
                        val endFloat = state.trimEndMs.toFloat().coerceAtLeast(startFloat + 500f)

                        RangeSlider(
                            value = startFloat..endFloat,
                            onValueChange = { range ->
                                viewModel.setTrimRange(range.start.toLong(), range.endInclusive.toLong())
                            },
                            valueRange = 0f..totalDuration.coerceAtLeast(1000f),
                            colors = SliderDefaults.colors(
                                thumbColor = PremiumTheme.colors.primary,
                                activeTrackColor = PremiumTheme.colors.primary
                            )
                        )
                    } else {
                        // Image Editing Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PremiumButton(
                                text = "Transform",
                                onClick = { selectedToolTab = "transform" },
                                variant = if (selectedToolTab == "transform") ButtonVariant.PRIMARY else ButtonVariant.TEXT
                            )
                            PremiumButton(
                                text = "Adjust Colors",
                                onClick = { selectedToolTab = "adjust" },
                                variant = if (selectedToolTab == "adjust") ButtonVariant.PRIMARY else ButtonVariant.TEXT
                            )
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.sm))

                        if (selectedToolTab == "transform") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    PremiumIconButton(
                                        icon = AppIcons.Rotate,
                                        contentDescription = "Rotate 90°",
                                        onClick = { viewModel.rotate() },
                                        backgroundColor = PremiumTheme.colors.surfaceVariant
                                    )
                                    Text("Rotate", style = PremiumTheme.typography.labelSmall)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    PremiumIconButton(
                                        icon = AppIcons.FlipHorizontal,
                                        contentDescription = "Flip Horizontal",
                                        onClick = { viewModel.flipHorizontal() },
                                        backgroundColor = PremiumTheme.colors.surfaceVariant
                                    )
                                    Text("Flip H", style = PremiumTheme.typography.labelSmall)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    PremiumIconButton(
                                        icon = AppIcons.FlipHorizontal,
                                        contentDescription = "Flip Vertical",
                                        onClick = { viewModel.flipVertical() },
                                        backgroundColor = PremiumTheme.colors.surfaceVariant
                                    )
                                    Text("Flip V", style = PremiumTheme.typography.labelSmall)
                                }
                            }
                        } else {
                            // Adjustments
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Brightness: ${state.imageParams.brightness.toInt()}",
                                    style = PremiumTheme.typography.bodySmall,
                                    color = PremiumTheme.colors.textPrimary
                                )
                                Slider(
                                    value = state.imageParams.brightness,
                                    onValueChange = { viewModel.setBrightness(it) },
                                    valueRange = -80f..80f,
                                    colors = SliderDefaults.colors(thumbColor = PremiumTheme.colors.primary, activeTrackColor = PremiumTheme.colors.primary)
                                )

                                Text(
                                    text = "Contrast: ${String.format("%.1f", state.imageParams.contrast)}x",
                                    style = PremiumTheme.typography.bodySmall,
                                    color = PremiumTheme.colors.textPrimary
                                )
                                Slider(
                                    value = state.imageParams.contrast,
                                    onValueChange = { viewModel.setContrast(it) },
                                    valueRange = 0.5f..1.8f,
                                    colors = SliderDefaults.colors(thumbColor = PremiumTheme.colors.primary, activeTrackColor = PremiumTheme.colors.primary)
                                )

                                Text(
                                    text = "Saturation: ${String.format("%.1f", state.imageParams.saturation)}x",
                                    style = PremiumTheme.typography.bodySmall,
                                    color = PremiumTheme.colors.textPrimary
                                )
                                Slider(
                                    value = state.imageParams.saturation,
                                    onValueChange = { viewModel.setSaturation(it) },
                                    valueRange = 0.0f..2.0f,
                                    colors = SliderDefaults.colors(thumbColor = PremiumTheme.colors.primary, activeTrackColor = PremiumTheme.colors.primary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
