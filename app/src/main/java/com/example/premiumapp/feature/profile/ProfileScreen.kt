package com.example.premiumapp.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.core.extensions.formatFileSize
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumIconButton
import com.example.premiumapp.design.components.PremiumInputDialog
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.components.SectionHeader
import com.example.premiumapp.design.components.SettingRow
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showEditNameDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Profile",
                actions = {
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
                .padding(AppSpacing.md)
                .padding(bottom = AppSpacing.huge)
        ) {
            // User Header Card
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.extraLarge,
                backgroundColor = PremiumTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PremiumTheme.colors.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.ProfileFilled,
                            contentDescription = null,
                            tint = PremiumTheme.colors.primary,
                            modifier = Modifier.size(AppIconSize.hero)
                        )
                    }

                    Spacer(modifier = Modifier.size(AppSpacing.md))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.userName,
                            style = PremiumTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = PremiumTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Local Offline Vault",
                            style = PremiumTheme.typography.bodySmall,
                            color = PremiumTheme.colors.textSecondary
                        )
                    }

                    PremiumIconButton(
                        icon = AppIcons.Edit,
                        contentDescription = "Edit Display Name",
                        onClick = { showEditNameDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Library Stats 2x2 Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                StatCard(
                    title = "Total Media",
                    value = "${state.mediaCount}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Collections",
                    value = "${state.collectionCount}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                StatCard(
                    title = "Favorites",
                    value = "${state.favoritesCount}",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Storage Used",
                    value = state.storageUsedBytes.formatFileSize(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            // Settings & Centers
            SectionHeader(title = "App Management")

            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                SettingRow(
                    title = "Appearance",
                    subtitle = "Theme, AMOLED mode, dynamic colors",
                    icon = AppIcons.Appearance,
                    onClick = onNavigateToAppearance
                )
                SettingRow(
                    title = "Permissions Center",
                    subtitle = "Manage camera and storage permissions",
                    icon = AppIcons.Permissions,
                    onClick = onNavigateToPermissions
                )
                SettingRow(
                    title = "Storage Center",
                    subtitle = "Usage breakdown and cache cleaner",
                    icon = AppIcons.Storage,
                    onClick = onNavigateToStorage
                )
                SettingRow(
                    title = "Notifications Center",
                    subtitle = "Task alerts and export notifications",
                    icon = AppIcons.Notifications,
                    onClick = onNavigateToNotifications
                )
                SettingRow(
                    title = "Recent Activity",
                    subtitle = "Audit log of imports and edits",
                    icon = AppIcons.Activity,
                    onClick = onNavigateToActivity
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            SectionHeader(title = "Information & Support")

            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                SettingRow(
                    title = "Help & Privacy",
                    subtitle = "Data guarantees and FAQs",
                    icon = AppIcons.Help,
                    onClick = onNavigateToHelp
                )
                SettingRow(
                    title = "About",
                    subtitle = "Version, licenses, architecture",
                    icon = AppIcons.Info,
                    onClick = onNavigateToAbout
                )
            }
        }

        if (showEditNameDialog) {
            PremiumInputDialog(
                title = "Edit Profile Name",
                initialValue = state.userName,
                label = "Display Name",
                onConfirm = { newName ->
                    viewModel.updateUserName(newName)
                    showEditNameDialog = false
                },
                onDismiss = { showEditNameDialog = false }
            )
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    PremiumCard(
        modifier = modifier,
        shape = AppShapes.large,
        backgroundColor = PremiumTheme.colors.surface,
        contentPadding = AppSpacing.md
    ) {
        Text(
            text = value,
            style = PremiumTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = PremiumTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = PremiumTheme.typography.bodySmall,
            color = PremiumTheme.colors.textSecondary
        )
    }
}
