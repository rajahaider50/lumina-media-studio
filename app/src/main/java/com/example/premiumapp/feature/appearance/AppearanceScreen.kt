package com.example.premiumapp.feature.appearance

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.components.SectionHeader
import com.example.premiumapp.design.components.SettingRow
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme
import com.example.premiumapp.design.theme.ThemeMode
import com.example.premiumapp.domain.model.GridDensity
import com.example.premiumapp.domain.model.ThumbnailQuality

@Composable
fun AppearanceScreen(
    viewModel: AppearanceViewModel,
    onNavigateBack: () -> Unit
) {
    val prefs by viewModel.userPreferences.collectAsState()

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Appearance",
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
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.md)
                .padding(bottom = AppSpacing.huge)
        ) {
            // Theme Mode Selection
            SectionHeader(title = "Color Theme")
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                ThemeOptionRow(
                    title = "System Default",
                    subtitle = "Match device dark/light setting",
                    isSelected = prefs.themeMode == ThemeMode.SYSTEM,
                    onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
                )
                ThemeOptionRow(
                    title = "Light Theme",
                    subtitle = "Clean, bright surfaces with subtle contrast",
                    isSelected = prefs.themeMode == ThemeMode.LIGHT,
                    onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
                )
                ThemeOptionRow(
                    title = "Dark Theme",
                    subtitle = "Deep graphite surfaces for low light",
                    isSelected = prefs.themeMode == ThemeMode.DARK,
                    onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
                )
                ThemeOptionRow(
                    title = "AMOLED Dark",
                    subtitle = "Pure black backgrounds for OLED battery saving",
                    isSelected = prefs.themeMode == ThemeMode.AMOLED,
                    onClick = { viewModel.setThemeMode(ThemeMode.AMOLED) }
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Dynamic Color & Motion
            SectionHeader(title = "Visual Effects")
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SettingRow(
                        title = "Material You Dynamic Color",
                        subtitle = "Sample palette accents from system wallpaper",
                        icon = AppIcons.Appearance,
                        checked = prefs.dynamicColor,
                        onCheckedChange = { viewModel.setDynamicColor(it) }
                    )
                }
                SettingRow(
                    title = "Reduce Motion",
                    subtitle = "Disable non-essential animations across the app",
                    icon = AppIcons.Adjust,
                    checked = prefs.reduceMotion,
                    onCheckedChange = { viewModel.setReduceMotion(it) }
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Grid Density
            SectionHeader(title = "Library Grid Density")
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                GridDensityRow(
                    title = "Compact (4 Columns)",
                    isSelected = prefs.gridDensity == GridDensity.COMPACT,
                    onClick = { viewModel.setGridDensity(GridDensity.COMPACT) }
                )
                GridDensityRow(
                    title = "Standard (3 Columns)",
                    isSelected = prefs.gridDensity == GridDensity.STANDARD,
                    onClick = { viewModel.setGridDensity(GridDensity.STANDARD) }
                )
                GridDensityRow(
                    title = "Large (2 Columns)",
                    isSelected = prefs.gridDensity == GridDensity.LARGE,
                    onClick = { viewModel.setGridDensity(GridDensity.LARGE) }
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Thumbnail Quality
            SectionHeader(title = "Thumbnail Quality")
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                ThumbnailQualityRow(
                    title = "High Quality (Sharpest details)",
                    isSelected = prefs.thumbnailQuality == ThumbnailQuality.HIGH,
                    onClick = { viewModel.setThumbnailQuality(ThumbnailQuality.HIGH) }
                )
                ThumbnailQualityRow(
                    title = "Medium Quality (Balanced)",
                    isSelected = prefs.thumbnailQuality == ThumbnailQuality.MEDIUM,
                    onClick = { viewModel.setThumbnailQuality(ThumbnailQuality.MEDIUM) }
                )
                ThumbnailQualityRow(
                    title = "Low Quality (Fastest rendering & lowest RAM)",
                    isSelected = prefs.thumbnailQuality == ThumbnailQuality.LOW,
                    onClick = { viewModel.setThumbnailQuality(ThumbnailQuality.LOW) }
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = PremiumTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                style = PremiumTheme.typography.bodySmall,
                color = PremiumTheme.colors.textSecondary
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = PremiumTheme.colors.primary,
                unselectedColor = PremiumTheme.colors.textTertiary
            )
        )
    }
}

@Composable
private fun GridDensityRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = PremiumTheme.typography.bodyLarge,
            color = PremiumTheme.colors.textPrimary
        )
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = PremiumTheme.colors.primary,
                unselectedColor = PremiumTheme.colors.textTertiary
            )
        )
    }
}

@Composable
private fun ThumbnailQualityRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = PremiumTheme.typography.bodyLarge,
            color = PremiumTheme.colors.textPrimary
        )
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = PremiumTheme.colors.primary,
                unselectedColor = PremiumTheme.colors.textTertiary
            )
        )
    }
}
