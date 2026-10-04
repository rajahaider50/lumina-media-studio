package com.example.premiumapp.design.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalReduceMotion = compositionLocalOf { false }

object AppAnimation {
    const val DURATION_FAST = 150
    const val DURATION_STANDARD = 250
    const val DURATION_MEDIUM = 350
    const val DURATION_SLOW = 450

    @Composable
    fun <T> standard(reduceMotion: Boolean = LocalReduceMotion.current): AnimationSpec<T> {
        return if (reduceMotion) {
            tween(durationMillis = 0)
        } else {
            tween(durationMillis = DURATION_STANDARD, easing = FastOutSlowInEasing)
        }
    }

    @Composable
    fun <T> fast(reduceMotion: Boolean = LocalReduceMotion.current): AnimationSpec<T> {
        return if (reduceMotion) {
            tween(durationMillis = 0)
        } else {
            tween(durationMillis = DURATION_FAST, easing = LinearOutSlowInEasing)
        }
    }

    @Composable
    fun <T> medium(reduceMotion: Boolean = LocalReduceMotion.current): AnimationSpec<T> {
        return if (reduceMotion) {
            tween(durationMillis = 0)
        } else {
            tween(durationMillis = DURATION_MEDIUM, easing = FastOutSlowInEasing)
        }
    }

    @Composable
    fun <T> springy(reduceMotion: Boolean = LocalReduceMotion.current): AnimationSpec<T> {
        return if (reduceMotion) {
            tween(durationMillis = 0)
        } else {
            spring(dampingRatio = 0.8f, stiffness = 400f)
        }
    }
}
