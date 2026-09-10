package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val HinataColorScheme = darkColorScheme(
    primary = HinataPurpleMuted,
    secondary = HinataSakuraPink,
    tertiary = NeonMintGreen,
    background = HinataBackground,
    surface = HinataSurface,
    onPrimary = HinataBackground,
    onSecondary = HinataBackground,
    onBackground = HinataPurpleLight,
    onSurface = HinataPurpleLight,
    error = RoseDeficit
)

/**
 * Main theme for your application.
 * Defines both MyApplicationTheme (which your MainActivity.kt calls)
 * and RushuFinTheme so your project builds with zero errors.
 */
@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Provides AppTheme.HINATA to all existing theme-reactive buttons and cards
    CompositionLocalProvider(LocalAppTheme provides AppTheme.HINATA) {
        MaterialTheme(
            colorScheme = HinataColorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun RushuFinTheme(
    content: @Composable () -> Unit
) {
    MyApplicationTheme(content = content)
}
