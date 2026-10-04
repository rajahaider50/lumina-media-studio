package com.example.premiumapp.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.dimensions.AppRadius
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    isDestructive: Boolean = false
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        PremiumCard(
            shape = AppShapes.extraLarge,
            backgroundColor = PremiumTheme.colors.surface,
            borderColor = PremiumTheme.colors.border,
            elevation = 6.dp,
            contentPadding = AppSpacing.xl
        ) {
            Text(
                text = title,
                style = PremiumTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = PremiumTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                text = message,
                style = PremiumTheme.typography.bodyMedium,
                color = PremiumTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(AppSpacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumButton(
                    text = cancelText,
                    onClick = onDismiss,
                    variant = ButtonVariant.TEXT
                )
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                PremiumButton(
                    text = confirmText,
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    variant = if (isDestructive) ButtonVariant.DESTRUCTIVE else ButtonVariant.PRIMARY
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumInputDialog(
    title: String,
    initialValue: String = "",
    label: String = "Name",
    placeholder: String = "",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Save",
    cancelText: String = "Cancel"
) {
    var textValue by remember { mutableStateOf(initialValue) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        PremiumCard(
            shape = AppShapes.extraLarge,
            backgroundColor = PremiumTheme.colors.surface,
            borderColor = PremiumTheme.colors.border,
            elevation = 6.dp,
            contentPadding = AppSpacing.xl
        ) {
            Text(
                text = title,
                style = PremiumTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = PremiumTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(AppSpacing.md))
            OutlinedTextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    if (it.isNotBlank()) errorMessage = null
                },
                label = { Text(label) },
                placeholder = { Text(placeholder) },
                isError = errorMessage != null,
                supportingText = errorMessage?.let { { Text(it, color = PremiumTheme.colors.error) } },
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
            Spacer(modifier = Modifier.height(AppSpacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumButton(
                    text = cancelText,
                    onClick = onDismiss,
                    variant = ButtonVariant.TEXT
                )
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                PremiumButton(
                    text = confirmText,
                    onClick = {
                        if (textValue.trim().isEmpty()) {
                            errorMessage = "Cannot be empty"
                        } else {
                            onConfirm(textValue.trim())
                            onDismiss()
                        }
                    },
                    variant = ButtonVariant.PRIMARY
                )
            }
        }
    }
}
