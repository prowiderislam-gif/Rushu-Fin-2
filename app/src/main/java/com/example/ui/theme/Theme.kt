package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val backgroundColor = CanvasBackground
    val primaryColor = NeonCyan
    val cardGlassColor = CardGlass
    val primaryTextColor = TextPrimary
    val secondaryColor = NeonGreen
    val tertiaryColor = NeonYellow
    val surfaceDarkColor = SurfaceDark
    val surfaceCardColor = SurfaceCard
    val secondaryTextColor = TextSecondary
    val errorColor = NeonRed

    val colorScheme = darkColorScheme(
        primary = primaryColor,
        onPrimary = backgroundColor,
        primaryContainer = cardGlassColor,
        onPrimaryContainer = primaryTextColor,

        secondary = secondaryColor,
        onSecondary = backgroundColor,

        tertiary = tertiaryColor,
        onTertiary = backgroundColor,

        background = backgroundColor,
        onBackground = primaryTextColor,

        surface = surfaceDarkColor,
        onSurface = primaryTextColor,

        surfaceVariant = surfaceCardColor,
        onSurfaceVariant = secondaryTextColor,

        error = errorColor,
        onError = backgroundColor
    )

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = backgroundColor.toArgb()
            window.navigationBarColor = backgroundColor.toArgb()

            WindowCompat.getInsetsController(
                window,
                view
            ).isAppearanceLightStatusBars = false

            WindowCompat.getInsetsController(
                window,
                view
            ).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
