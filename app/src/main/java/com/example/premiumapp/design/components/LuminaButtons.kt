package com.example.premiumapp.design.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.dimensions.LuminaIconSize
import com.example.premiumapp.design.dimensions.LuminaRadius
import com.example.premiumapp.design.dimensions.LuminaSpacing
import com.example.premiumapp.design.dimensions.LuminaTouchTarget
import com.example.premiumapp.design.theme.LuminaThemeTokens
import com.example.premiumapp.design.theme.StatusError

@Composable
fun LuminaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(LuminaRadius.medium),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = LuminaSpacing.xLarge, vertical = LuminaSpacing.small),
        modifier = modifier
            .defaultMinSize(minHeight = LuminaTouchTarget.minimum)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(LuminaIconSize.standard)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(LuminaSpacing.xSmall))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LuminaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val extended = LuminaThemeTokens.extended

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(LuminaRadius.medium),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(horizontal = LuminaSpacing.xLarge, vertical = LuminaSpacing.small),
        modifier = modifier
            .defaultMinSize(minHeight = LuminaTouchTarget.minimum)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(LuminaIconSize.standard)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(LuminaSpacing.xSmall))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LuminaDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(LuminaRadius.medium),
        colors = ButtonDefaults.buttonColors(
            containerColor = StatusError,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = LuminaSpacing.xLarge, vertical = LuminaSpacing.small),
        modifier = modifier
            .defaultMinSize(minHeight = LuminaTouchTarget.minimum)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(LuminaIconSize.standard)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(LuminaSpacing.xSmall))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LuminaOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val extended = LuminaThemeTokens.extended

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(LuminaRadius.medium),
        border = BorderStroke(1.dp, extended.borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = LuminaSpacing.xLarge, vertical = LuminaSpacing.small),
        modifier = modifier
            .defaultMinSize(minHeight = LuminaTouchTarget.minimum)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(LuminaIconSize.standard)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(LuminaSpacing.xSmall))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LuminaIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(LuminaTouchTarget.minimum)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(LuminaIconSize.standard)
        )
    }
}
