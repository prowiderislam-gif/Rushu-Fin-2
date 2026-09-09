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
    val colorScheme = darkColorScheme(
        primary = NeonCyan,
        onPrimary = CanvasBackground,
        primaryContainer = CardGlass,
        onPrimaryContainer = TextPrimary,
        secondary = NeonGreen,
        onSecondary = CanvasBackground,
        tertiary = NeonYellow,
        onTertiary = CanvasBackground,
        background = CanvasBackground,
        onBackground = TextPrimary,
        surface = SurfaceDark,
        onSurface = TextPrimary,
        surfaceVariant = SurfaceCard,
        onSurfaceVariant = TextSecondary,
        error = NeonRed,
        onError = CanvasBackground
    )

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = CanvasBackground.toArgb()
            window.navigationBarColor = CanvasBackground.toArgb()

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
