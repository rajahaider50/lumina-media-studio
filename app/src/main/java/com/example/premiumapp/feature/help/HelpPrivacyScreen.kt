package com.example.premiumapp.feature.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.components.SectionHeader
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun HelpPrivacyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Help & Privacy",
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
            // Privacy Guarantee Hero Card
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.extraLarge,
                backgroundColor = PremiumTheme.colors.primary.copy(alpha = 0.08f),
                borderColor = PremiumTheme.colors.primary.copy(alpha = 0.3f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.Permissions,
                        contentDescription = null,
                        tint = PremiumTheme.colors.primary,
                        modifier = Modifier.size(AppIconSize.large)
                    )
                    Spacer(modifier = Modifier.size(AppSpacing.md))
                    Column {
                        Text(
                            text = "100% Offline & Private",
                            style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PremiumTheme.colors.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Lumina Media does not contain any remote analytics, advertising trackers, or cloud upload services. Your media never leaves your physical phone.",
                            style = PremiumTheme.typography.bodySmall,
                            color = PremiumTheme.colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            SectionHeader(title = "Core Architecture Guides")

            ExpandableHelpCard(
                title = "How Media Import Works",
                icon = AppIcons.Import,
                content = "When you import an image or video, Lumina Media reads the source file metadata (dimensions, duration, size), makes a private local copy inside the app's protected sandbox, generates an optimized memory-conscious thumbnail, and indexes it in Room Database. This prevents broken references if temporary Photo Picker permissions expire."
            )

            Spacer(modifier = Modifier.height(AppSpacing.xs))

            ExpandableHelpCard(
                title = "How Local Storage Works",
                icon = AppIcons.Storage,
                content = "Imported media resides in your device's internal app-private storage. You can check exact disk usage anytime in Storage Center. The cache cleaner safely purges temporary thumbnails and preview buffers while never touching your original photos or videos."
            )

            Spacer(modifier = Modifier.height(AppSpacing.xs))

            ExpandableHelpCard(
                title = "How Collections Work",
                icon = AppIcons.CollectionsFilled,
                content = "Collections allow you to organize media into customized albums using an internal SQLite relation table. Deleting a collection only deletes the album grouping—the underlying photos and videos remain safely in your library."
            )

            Spacer(modifier = Modifier.height(AppSpacing.xs))

            ExpandableHelpCard(
                title = "Non-Destructive Media Editing",
                icon = AppIcons.Edit,
                content = "All image rotations, color adjustments, and video trimming are strictly non-destructive. When you edit and tap 'Save Copy', Lumina Media generates a new distinct media file copy, preserving your original file untouched."
            )

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            SectionHeader(title = "Frequently Asked Questions")

            FaqItem(
                question = "Does this application use my mobile data?",
                answer = "No. Lumina Media runs completely offline without requiring any internet connection. No cellular or Wi-Fi data is ever used."
            )
            FaqItem(
                question = "How do I export media back to my gallery?",
                answer = "Open any photo or video, tap 'Export', and choose 'Save to Device Photos / Gallery'. Lumina Media writes it directly to Android MediaStore Pictures or Movies."
            )
            FaqItem(
                question = "What happens when I delete a photo or video?",
                answer = "Lumina Media asks for explicit confirmation. Once confirmed, the file is securely deleted from the local application storage and its index is removed."
            )
        }
    }
}

@Composable
private fun ExpandableHelpCard(
    title: String,
    icon: ImageVector,
    content: String
) {
    var expanded by remember { mutableStateOf(false) }

    PremiumCard(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.large,
        backgroundColor = PremiumTheme.colors.surface,
        onClick = { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PremiumTheme.colors.primary,
                    modifier = Modifier.size(AppIconSize.standard)
                )
                Spacer(modifier = Modifier.size(AppSpacing.md))
                Text(
                    text = title,
                    style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = PremiumTheme.colors.textPrimary
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = PremiumTheme.colors.textTertiary
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = AppSpacing.sm)) {
                Text(
                    text = content,
                    style = PremiumTheme.typography.bodyMedium,
                    color = PremiumTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = AppShapes.medium,
        backgroundColor = PremiumTheme.colors.surface,
        onClick = { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                style = PremiumTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = PremiumTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = PremiumTheme.colors.textTertiary
            )
        }

        AnimatedVisibility(visible = expanded) {
            Text(
                text = answer,
                style = PremiumTheme.typography.bodySmall,
                color = PremiumTheme.colors.textSecondary,
                modifier = Modifier.padding(top = AppSpacing.xs)
            )
        }
    }
}
