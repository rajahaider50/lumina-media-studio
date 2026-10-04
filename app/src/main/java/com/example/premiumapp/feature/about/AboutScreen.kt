package com.example.premiumapp.feature.about

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.R
import com.example.premiumapp.design.components.PremiumCard
import com.example.premiumapp.design.components.PremiumTopBar
import com.example.premiumapp.design.components.SectionHeader
import com.example.premiumapp.design.components.SettingRow
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.AppShapes
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val packageInfo = try {
        context.packageManager.getPackageInfo(context.packageName, 0)
    } catch (e: Exception) {
        null
    }
    val versionName = packageInfo?.versionName ?: "1.0.0"
    val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        packageInfo?.longVersionCode?.toString() ?: "1"
    } else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toString() ?: "1"
    }

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        topBar = {
            PremiumTopBar(
                title = "About",
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
                .padding(bottom = AppSpacing.huge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.lg))

            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(AppSpacing.md))

            Text(
                text = stringResource(id = R.string.app_name),
                style = PremiumTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = PremiumTheme.colors.textPrimary
            )

            Text(
                text = "Version $versionName (Build $versionCode)",
                style = PremiumTheme.typography.bodySmall,
                color = PremiumTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            // Tech Specs Card
            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface
            ) {
                Text(
                    text = "Application Specifications",
                    style = PremiumTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = PremiumTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                AboutRow("Architecture", "Clean Architecture + MVVM")
                AboutRow("UI Framework", "100% Native Jetpack Compose")
                AboutRow("Design System", "Material 3 + Custom Tokens")
                AboutRow("Persistence", "Room Database + DataStore")
                AboutRow("Media Engine", "Media3 ExoPlayer + Android MediaStore")
                AboutRow("Image Loader", "Coil 2.6 Lifecycle-Aware")
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Open Source Licenses Card
            SectionHeader(title = "Open Source Acknowledgments")

            PremiumCard(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                backgroundColor = PremiumTheme.colors.surface,
                contentPadding = 0.dp
            ) {
                SettingRow(
                    title = "Android Jetpack & Compose",
                    subtitle = "Apache License 2.0 • Google",
                    icon = AppIcons.Check,
                    showChevron = false
                )
                SettingRow(
                    title = "Kotlin Coroutines & Flow",
                    subtitle = "Apache License 2.0 • JetBrains",
                    icon = AppIcons.Check,
                    showChevron = false
                )
                SettingRow(
                    title = "Coil Image Loading",
                    subtitle = "Apache License 2.0 • Coil Contributors",
                    icon = AppIcons.Check,
                    showChevron = false
                )
                SettingRow(
                    title = "AndroidX Media3 ExoPlayer",
                    subtitle = "Apache License 2.0 • Google",
                    icon = AppIcons.Check,
                    showChevron = false
                )
            }
        }
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = PremiumTheme.typography.bodyMedium, color = PremiumTheme.colors.textSecondary)
        Text(text = value, style = PremiumTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = PremiumTheme.colors.textPrimary)
    }
}
