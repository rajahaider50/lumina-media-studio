package com.example.premiumapp.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.premiumapp.R
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val navTarget by viewModel.navigationTarget.collectAsState()

    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        scale.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(navTarget) {
        when (navTarget) {
            SplashNavigationTarget.Home -> onNavigateToHome()
            SplashNavigationTarget.Onboarding -> onNavigateToOnboarding()
            SplashNavigationTarget.Loading -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(AppIconSize.splashLogo)
            )

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            Text(
                text = stringResource(id = R.string.app_name),
                style = PremiumTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = PremiumTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(AppSpacing.xs))

            Text(
                text = stringResource(id = R.string.app_tagline),
                style = PremiumTheme.typography.bodyMedium,
                color = PremiumTheme.colors.textSecondary
            )
        }
    }
}
