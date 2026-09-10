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
    // These color values are theme-reactive (they read the currently selected
    // App Theme via LocalAppTheme), so they must be read here inside a
    // @Composable body — not in a top-level val or inside a SideEffect lambda.
    val primary = NeonCyan
    val onPrimary = CanvasBackground
    val primaryContainer = CardGlass
    val onPrimaryContainer = TextPrimary
    val secondary = NeonGreen
    val tertiary = NeonYellow
    val background = CanvasBackground
    val onBackground = TextPrimary
    val surface = SurfaceDark
    val onSurface = TextPrimary
    val surfaceVariant = SurfaceCard
    val onSurfaceVariant = TextSecondary
    val error = NeonRed

    val darkColorScheme = darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onPrimary,
        tertiary = tertiary,
        onTertiary = onPrimary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        error = error,
        onError = onPrimary
    )

    // Captured as a plain value above, so it's safe to reference inside the
    // non-composable SideEffect lambda below.
    val statusBarColorArgb = background.toArgb()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = statusBarColorArgb
            window.navigationBarColor = statusBarColorArgb
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme,
        typography = Typography,
        content = content
    )
}
