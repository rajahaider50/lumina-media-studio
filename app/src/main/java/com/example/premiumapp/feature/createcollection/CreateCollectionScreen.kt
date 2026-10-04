package com.example.premiumapp.feature.createcollection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.components.ButtonVariant
import com.example.premiumapp.design.components.PremiumButton
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun CreateCollectionScreen(
    viewModel: CreateCollectionViewModel,
    onNavigateBack: () -> Unit,
    onCollectionCreated: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "New Collection",
                navigationIcon = AppIcons.Close,
                navigationIconContentDescription = "Cancel",
                onNavigationClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(AppSpacing.xl)
        ) {
            Text(
                text = "Collection Details",
                style = PremiumTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = PremiumTheme.colors.textPrimary
            )
            Text(
                text = "Organize related photos and videos into a custom curated collection.",
                style = PremiumTheme.typography.bodyMedium,
                color = PremiumTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Collection Name *") },
                placeholder = { Text("e.g. Summer Vacation, Portfolio, Family") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it, color = PremiumTheme.colors.error) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremiumTheme.colors.primary,
                    unfocusedBorderColor = PremiumTheme.colors.border,
                    focusedTextColor = PremiumTheme.colors.textPrimary,
                    unfocusedTextColor = PremiumTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(AppSpacing.md))

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Description (Optional)") },
                placeholder = { Text("Add notes or details about this collection...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremiumTheme.colors.primary,
                    unfocusedBorderColor = PremiumTheme.colors.border,
                    focusedTextColor = PremiumTheme.colors.textPrimary,
                    unfocusedTextColor = PremiumTheme.colors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(AppSpacing.xxl))

            PremiumButton(
                text = if (state.isCreating) "Creating..." else "Create Collection",
                onClick = { viewModel.createCollection(onCollectionCreated) },
                enabled = !state.isCreating,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.PRIMARY
            )

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            PremiumButton(
                text = "Cancel",
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.TEXT
            )
        }
    }
}
