package com.example.premiumapp.design.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.premiumapp.domain.model.ThemeMode

typealias ThemeMode = com.example.premiumapp.domain.model.ThemeMode

@Immutable
data class ExtendedColors(
    val cardBackground: Color,
    val borderColor: Color,
    val borderSubtle: Color,
    val overlay: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        cardBackground = LightCard,
        borderColor = LightBorder,
        borderSubtle = LightBorderSubtle,
        overlay = LightOverlay,
        success = StatusSuccess,
        warning = StatusWarning,
        info = StatusInfo,
        textPrimary = LightTextPrimary,
        textSecondary = LightTextSecondary,
        textTertiary = LightTextTertiary
    )
}

val LocalReducedMotion = compositionLocalOf { false }

private val LightColorScheme = lightColorScheme(
    primary = LuminaIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = LuminaIndigoDark,
    secondary = LuminaCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF0E7490),
    tertiary = LuminaViolet,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = StatusError,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = LuminaIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = LuminaCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = LuminaViolet,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    error = StatusError,
    onError = Color.White
)

private val AmoledColorScheme = darkColorScheme(
    primary = LuminaIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E1B4B),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = LuminaCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF083344),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = LuminaViolet,
    background = AmoledBackground,
    onBackground = AmoledTextPrimary,
    surface = AmoledSurface,
    onSurface = AmoledTextPrimary,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = AmoledTextSecondary,
    error = StatusError,
    onError = Color.White
)

@Composable
fun LuminaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }
    val isAmoled = themeMode == ThemeMode.AMOLED

    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        isAmoled -> AmoledColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) {
                dynamicDarkColorScheme(context).copy(
                    background = DarkBackground,
                    surface = DarkSurface,
                    surfaceVariant = DarkSurfaceVariant
                )
            } else {
                dynamicLightColorScheme(context).copy(
                    background = LightBackground,
                    surface = LightSurface,
                    surfaceVariant = LightSurfaceVariant
                )
            }
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = when {
        isAmoled -> ExtendedColors(
            cardBackground = AmoledCard,
            borderColor = AmoledBorder,
            borderSubtle = AmoledBorderSubtle,
            overlay = AmoledOverlay,
            success = StatusSuccess,
            warning = StatusWarning,
            info = StatusInfo,
            textPrimary = AmoledTextPrimary,
            textSecondary = AmoledTextSecondary,
            textTertiary = AmoledTextTertiary
        )
        isDark -> ExtendedColors(
            cardBackground = DarkCard,
            borderColor = DarkBorder,
            borderSubtle = DarkBorderSubtle,
            overlay = DarkOverlay,
            success = StatusSuccess,
            warning = StatusWarning,
            info = StatusInfo,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            textTertiary = DarkTextTertiary
        )
        else -> ExtendedColors(
            cardBackground = LightCard,
            borderColor = LightBorder,
            borderSubtle = LightBorderSubtle,
            overlay = LightOverlay,
            success = StatusSuccess,
            warning = StatusWarning,
            info = StatusInfo,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textTertiary = LightTextTertiary
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors,
        LocalReducedMotion provides reduceMotion
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LuminaTypography,
            content = content
        )
    }
}

object LuminaThemeTokens {
    val extended: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current

    val reduceMotion: Boolean
        @Composable
        get() = LocalReducedMotion.current
}

data class ExtendedColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val secondary: Color,
    val error: Color,
    val card: Color,
    val border: Color,
    val borderSubtle: Color,
    val overlay: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color
)

object PremiumTheme {
    val colors: ExtendedColorScheme
        @Composable
        get() = ExtendedColorScheme(
            background = MaterialTheme.colorScheme.background,
            surface = MaterialTheme.colorScheme.surface,
            surfaceVariant = MaterialTheme.colorScheme.surfaceVariant,
            primary = MaterialTheme.colorScheme.primary,
            secondary = MaterialTheme.colorScheme.secondary,
            error = MaterialTheme.colorScheme.error,
            card = LocalExtendedColors.current.cardBackground,
            border = LocalExtendedColors.current.borderColor,
            borderSubtle = LocalExtendedColors.current.borderSubtle,
            overlay = LocalExtendedColors.current.overlay,
            success = LocalExtendedColors.current.success,
            warning = LocalExtendedColors.current.warning,
            info = LocalExtendedColors.current.info,
            textPrimary = LocalExtendedColors.current.textPrimary,
            textSecondary = LocalExtendedColors.current.textSecondary,
            textTertiary = LocalExtendedColors.current.textTertiary
        )

    val typography: androidx.compose.material3.Typography
        @Composable
        get() = MaterialTheme.typography

    val reduceMotion: Boolean
        @Composable
        get() = LocalReducedMotion.current
}

