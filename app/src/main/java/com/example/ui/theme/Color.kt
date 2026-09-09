package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ------------------------------------------------------------------
// Theme selection
// ------------------------------------------------------------------
// Three mutually exclusive display themes:
//   "DEFAULT" — the original glowing neon-on-black look
//   "BASIC"   — bigger text, no glow/blur, flatter high-contrast panels
//               (for users who find the default look hard to read)
//   "KITTY"   — a soft, cute pastel-on-charcoal theme with rounder
//               "paw-like" card corners
// The chosen theme is provided at the root of the screen and read by
// the color properties below, so every existing call site in the app
// (which just references e.g. `NeonGreen` as a bare value) automatically
// re-themes without needing to be individually edited.
object AppTheme {
    const val DEFAULT = "DEFAULT"
    const val BASIC = "BASIC"
    const val KITTY = "KITTY"
}

val LocalAppTheme = compositionLocalOf { AppTheme.DEFAULT }

// Convenience read-only helpers
val isBasicTheme: Boolean
    @Composable get() = LocalAppTheme.current == AppTheme.BASIC

val isKittyTheme: Boolean
    @Composable get() = LocalAppTheme.current == AppTheme.KITTY

// ------------------------------------------------------------------
// Background / Surface colors — theme-reactive
// ------------------------------------------------------------------
val CanvasBackground: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFF1B1620) // warm charcoal with a hint of plum
        else -> Color(0xFF050505)
    }

val SurfaceDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFF241D29)
        else -> Color(0xFF0A0A0D)
    }

val SurfaceCard: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFF2A222F)
        else -> Color(0xFF121216)
    }

val CardGlass: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFF2D242F)
        else -> Color(0xFF141418)
    }

val CardGlassBorder: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0x33FFC1D9) // soft pink border
        else -> Color(0x1FFFFFFF)
    }

// ------------------------------------------------------------------
// Semantic accent colors — theme-reactive. Names and roles stay the
// same everywhere (Green = live/positive, Red = liability/negative,
// Yellow = fixed initial balance, Cyan = general accent/links),
// only the exact shade shifts per theme.
// ------------------------------------------------------------------
val NeonGreen: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFF8FE3B0) // soft mint green
        else -> Color(0xFF00FF9C)
    }
val NeonGreenGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0x668FE3B0)
        else -> Color(0x6600FF9C)
    }
val NeonGreenDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFF4FA377)
        else -> Color(0xFF00995E)
    }

val NeonRed: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFFE8748F) // soft raspberry-rose red
        else -> Color(0xFFFF3366)
    }
val NeonRedGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0x66E8748F)
        else -> Color(0x66FF3366)
    }
val NeonRedDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFF9C4A5C)
        else -> Color(0xFF8F1332)
    }

val NeonYellow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFFF4D58D) // warm buttery gold
        else -> Color(0xFFFFD600)
    }
val NeonYellowGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0x66F4D58D)
        else -> Color(0x66FFD600)
    }
val NeonYellowDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFFA88F5C)
        else -> Color(0xFF8F7800)
    }

val NeonCyan: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFFFFC1D9) // signature soft pink accent
        else -> Color(0xFF00E5FF)
    }
val NeonCyanGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0x66FFC1D9)
        else -> Color(0x6600E5FF)
    }

val NeonPurple: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFFCBA6E8)
        else -> Color(0xFFA855F7)
    }
val NeonPurpleGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0x66CBA6E8)
        else -> Color(0x66A855F7)
    }

// ------------------------------------------------------------------
// Typography — kept mostly stable across themes for reliable contrast;
// only a very slight warm tint shift for Kitty so it doesn't look stark
// against the charcoal-plum background.
// ------------------------------------------------------------------
val TextPrimary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFFFDF6FA)
        else -> Color(0xFFFFFFFF)
    }
val TextSecondary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFFCBB8C9)
        else -> Color(0xFFA1A1AA)
    }
val TextMuted: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KITTY -> Color(0xFF9C8A9A)
        else -> Color(0xFF71717A)
    }
