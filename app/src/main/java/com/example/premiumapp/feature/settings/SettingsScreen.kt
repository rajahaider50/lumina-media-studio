package com.example.premiumapp.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumConfirmDialog
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.components.SectionHeader
import com.example.premiumapp.design.components.SettingRow
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val prefs by viewModel.userPreferences.collectAsState()
    var showClearSearchesDialog by remember { mutableStateOf(false) }
    var showClearActivityDialog by remember { mutableStateOf(false) }
    var showResetOnboardingDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "Settings",
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
            // Appearance & Display
            SectionHeader(title = "Appearance & Display")
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                SettingRow(
                    title = "Theme & Customization",
                    subtitle = "Light, Dark, AMOLED mode, and colors",
                    icon = AppIcons.Appearance,
                    onClick = onNavigateToAppearance
                )
                SettingRow(
                    title = "Reduce Motion",
                    subtitle = "Minimize interface transitions",
                    icon = AppIcons.Adjust,
                    checked = prefs.reduceMotion,
                    onCheckedChange = { viewModel.setReduceMotion(it) }
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Storage & System
            SectionHeader(title = "System Centers")
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                SettingRow(
                    title = "Storage Center",
                    subtitle = "Manage cached files and library usage",
                    icon = AppIcons.Storage,
                    onClick = onNavigateToStorage
                )
                SettingRow(
                    title = "Permissions Center",
                    subtitle = "Inspect and grant Android device permissions",
                    icon = AppIcons.Permissions,
                    onClick = onNavigateToPermissions
                )
                SettingRow(
                    title = "Notifications Center",
                    subtitle = "Manage background task alerts",
                    icon = AppIcons.Notifications,
                    onClick = onNavigateToNotifications
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Privacy & Data
            SectionHeader(title = "Privacy & Local Data")
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                SettingRow(
                    title = "Clear Search History",
                    subtitle = "Remove all cached search terms",
                    icon = AppIcons.Search,
                    onClick = { showClearSearchesDialog = true }
                )
                SettingRow(
                    title = "Clear Activity History",
                    subtitle = "Remove the audit log of actions",
                    icon = AppIcons.Activity,
                    onClick = { showClearActivityDialog = true }
                )
                SettingRow(
                    title = "Reset Onboarding",
                    subtitle = "Show introductory tour on next launch",
                    icon = AppIcons.Refresh,
                    onClick = { showResetOnboardingDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Help & About
            SectionHeader(title = "About & Help")
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                SettingRow(
                    title = "Help & Privacy Policy",
                    subtitle = "How local storage and permissions work",
                    icon = AppIcons.Help,
                    onClick = onNavigateToHelp
                )
                SettingRow(
                    title = "About Lumina Studio",
                    subtitle = "Version, licenses, architecture",
                    icon = AppIcons.Info,
                    onClick = onNavigateToAbout
                )
            }
        }

        if (showClearSearchesDialog) {
            PremiumConfirmDialog(
                title = "Clear Search History?",
                message = "This will permanently remove all your saved recent search keywords.",
                confirmText = "Clear",
                isDestructive = true,
                onConfirm = {
                    viewModel.clearSearchHistory()
                    showClearSearchesDialog = false
                },
                onDismiss = { showClearSearchesDialog = false }
            )
        }

        if (showClearActivityDialog) {
            PremiumConfirmDialog(
                title = "Clear Activity History?",
                message = "This removes all logged events. Your photos and videos remain unaffected.",
                confirmText = "Clear",
                isDestructive = true,
                onConfirm = {
                    viewModel.clearActivityHistory()
                    showClearActivityDialog = false
                },
                onDismiss = { showClearActivityDialog = false }
            )
        }

        if (showResetOnboardingDialog) {
            PremiumConfirmDialog(
                title = "Reset Onboarding?",
                message = "The introductory walkthrough will be displayed next time the application opens.",
                confirmText = "Reset",
                onConfirm = {
                    viewModel.resetOnboarding()
                    showResetOnboardingDialog = false
                },
                onDismiss = { showResetOnboardingDialog = false }
            )
        }
    }
}
