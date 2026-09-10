package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun RushuFinTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HinataColorScheme,
        typography = Typography,
        content = content
    )
}
