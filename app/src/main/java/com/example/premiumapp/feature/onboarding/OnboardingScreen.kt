package com.example.premiumapp.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.premiumapp.design.components.ButtonVariant
import com.example.premiumapp.design.components.PremiumButton
import com.example.premiumapp.design.dimensions.AppIconSize
import com.example.premiumapp.design.dimensions.AppSpacing
import com.example.premiumapp.design.icons.AppIcons
import com.example.premiumapp.design.theme.PremiumTheme

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToHome: () -> Unit
) {
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages = viewModel.pages.size
    val isLastPage = currentPage == totalPages - 1
    val isFirstPage = currentPage == 0

    Scaffold(
        containerColor = PremiumTheme.colors.background,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.xl)
            ) {
                // Page Indicator Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalPages) { index ->
                        val isSelected = index == currentPage
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) PremiumTheme.colors.primary
                                    else PremiumTheme.colors.surfaceVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isFirstPage) {
                        PremiumButton(
                            text = "Back",
                            onClick = { viewModel.onPreviousPage() },
                            variant = ButtonVariant.TEXT
                        )
                    } else {
                        PremiumButton(
                            text = "Skip",
                            onClick = { viewModel.completeOnboarding(onNavigateToHome) },
                            variant = ButtonVariant.TEXT
                        )
                    }

                    if (isLastPage) {
                        PremiumButton(
                            text = "Get Started",
                            onClick = { viewModel.completeOnboarding(onNavigateToHome) },
                            variant = ButtonVariant.PRIMARY
                        )
                    } else {
                        PremiumButton(
                            text = "Next",
                            onClick = { viewModel.onNextPage() },
                            variant = ButtonVariant.PRIMARY
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = AppSpacing.xl),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "OnboardingPageAnimation"
            ) { targetPageIndex ->
                val pageData = viewModel.pages[targetPageIndex]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(PremiumTheme.colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (pageData.iconName) {
                                "Security" -> AppIcons.Permissions
                                "Tune" -> AppIcons.Adjust
                                else -> AppIcons.CollectionsFilled
                            },
                            contentDescription = null,
                            modifier = Modifier.size(AppIconSize.hero),
                            tint = PremiumTheme.colors.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.xxl))

                    Text(
                        text = pageData.title,
                        style = PremiumTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = PremiumTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.md))

                    Text(
                        text = pageData.description,
                        style = PremiumTheme.typography.bodyLarge,
                        color = PremiumTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
