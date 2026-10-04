package com.example.premiumapp.feature.errors

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.components.ErrorState
import com.example.premiumapp.design.components.FilterChipRow
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme

enum class ErrorHubType(
    val title: String,
    val heading: String,
    val message: String,
    val retryText: String,
    val secondaryText: String?
) {
    OFFLINE(
        "Offline Ready",
        "Full Offline Capability",
        "Lumina Media is operating completely locally on your device. All your media, search, and collections work flawlessly without internet.",
        "Refresh",
        null
    ),
    PERMISSION_DENIED(
        "Permission Denied",
        "Photo Access Required",
        "Lumina Media requires access to read images or videos from your device. You can grant access anytime in Permissions Center.",
        "Grant Permission",
        "Cancel"
    ),
    MEDIA_UNAVAILABLE(
        "Media Unavailable",
        "Source File Missing",
        "The original media file could not be located in local storage. It may have been cleaned by an external app or storage manager.",
        "Remove Record",
        "Go Back"
    ),
    STORAGE_FULL(
        "Storage Full",
        "Insufficient Storage Space",
        "Your device has less than 100MB of free disk space remaining. Clear your cache or delete unused files to continue.",
        "Open Storage Center",
        "Cancel"
    ),
    IMPORT_FAILED(
        "Import Error",
        "Media Import Interrupted",
        "The selected image or video file could not be read. Please verify the file is not damaged and try again.",
        "Choose Another File",
        "Dismiss"
    )
}

@Composable
fun ErrorHubScreen(
    initialType: String = "OFFLINE",
    onNavigateBack: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onNavigateToImport: () -> Unit
) {
    var selectedType by remember {
        mutableStateOf(
            try {
                ErrorHubType.valueOf(initialType.uppercase())
            } catch (e: Exception) {
                ErrorHubType.OFFLINE
            }
        )
    }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "State Hub",
                subtitle = "Error & Offline Modes",
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
        ) {
            FilterChipRow(
                filters = ErrorHubType.values().map { it.title },
                selectedFilter = selectedType.title,
                onFilterSelected = { title ->
                    selectedType = ErrorHubType.values().first { it.title == title }
                }
            )

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            when (selectedType) {
                ErrorHubType.OFFLINE -> {
                    ErrorState(
                        title = selectedType.heading,
                        message = selectedType.message,
                        icon = AppIcons.Offline,
                        retryText = selectedType.retryText,
                        onRetry = onNavigateBack
                    )
                }
                ErrorHubType.PERMISSION_DENIED -> {
                    ErrorState(
                        title = selectedType.heading,
                        message = selectedType.message,
                        icon = AppIcons.Permissions,
                        retryText = selectedType.retryText,
                        onRetry = onNavigateToPermissions,
                        secondaryText = selectedType.secondaryText,
                        onSecondaryAction = onNavigateBack
                    )
                }
                ErrorHubType.MEDIA_UNAVAILABLE -> {
                    ErrorState(
                        title = selectedType.heading,
                        message = selectedType.message,
                        icon = AppIcons.Error,
                        retryText = selectedType.retryText,
                        onRetry = onNavigateBack,
                        secondaryText = selectedType.secondaryText,
                        onSecondaryAction = onNavigateBack
                    )
                }
                ErrorHubType.STORAGE_FULL -> {
                    ErrorState(
                        title = selectedType.heading,
                        message = selectedType.message,
                        icon = AppIcons.Storage,
                        retryText = selectedType.retryText,
                        onRetry = onNavigateToStorage,
                        secondaryText = selectedType.secondaryText,
                        onSecondaryAction = onNavigateBack
                    )
                }
                ErrorHubType.IMPORT_FAILED -> {
                    ErrorState(
                        title = selectedType.heading,
                        message = selectedType.message,
                        icon = AppIcons.Import,
                        retryText = selectedType.retryText,
                        onRetry = onNavigateToImport,
                        secondaryText = selectedType.secondaryText,
                        onSecondaryAction = onNavigateBack
                    )
                }
            }
        }
    }
}
