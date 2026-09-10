package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ------------------------------------------------------------------
// Theme selection (3 Clean Themes)
// ------------------------------------------------------------------
object AppTheme {
    const val DEFAULT = "DEFAULT"
    const val BASIC = "BASIC"
    const val HINATA = "HINATA"
    
    // Fallback constants for backwards compatibility
    const val GOLDEN_HIVE = "GOLDEN_HIVE"
    const val SAKURA_BLOOM = "SAKURA_BLOOM"
    const val MOONLIT_PURPLE = "MOONLIT_PURPLE"
}

val LocalAppTheme = compositionLocalOf { AppTheme.HINATA }

// ------------------------------------------------------------------
// Hinata Dedicated Standalone Palette
// ------------------------------------------------------------------
val HinataBackground = Color(0xFF040008L)       // Deep Obsidian Canvas
val HinataSurface = Color(0xFF0B0216L)          // Dark Violet Card Surface
val HinataCardBorder = Color(0x66A855F7L)       // Purple Neon Glow Border
val HinataCardGlow = Color(0x40A855F7L)         // Outer Shadow Glow

// Strict Number Accents for Hinata Theme
val NeonMintGreen = Color(0xFF00FFB2L)          // Live Balance Mint Neon Green
val EmeraldIncome = Color(0xFF00FFB2L)          // Income number
val RoseDeficit = Color(0xFFFB7185L)            // Soft Sakura Rose for Expense & Liability numbers
val HinataGold = Color(0xFFFACC15L)               // Fixed initial gold number

// Cohesive Violet & Lavender Accents for all Cards/Buttons
val HinataPurpleLight = Color(0xFFF3E8FFL)      // Crisp lavender text
val HinataPurpleMuted = Color(0xFFC084FCL)      // Muted lavender for titles/subtitles
val HinataSakuraPink = Color(0xFFF472B6L)       // Sakura Petal Pink
val HinataPillBg = Color(0x803B0764L)           // Pill / Tab / Badge Background
val HinataPillBorder = Color(0x80A855F7L)       // Pill / Tab / Badge Border
val HinataButtonBg = Color(0xFF2E1065L)         // Violet Button Container

// ------------------------------------------------------------------
// Background / Surface colors — theme-reactive
// ------------------------------------------------------------------
val CanvasBackground: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF040008L)
        else -> Color(0xFF050505L)
    }

val SurfaceDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF080112L)
        else -> Color(0xFF0A0A0DL)
    }

val SurfaceCard: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF0B0216L)
        else -> Color(0xFF121216L)
    }

val CardGlass: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF0B0216L)
        else -> Color(0xFF141418L)
    }

val CardGlassBorder: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x66A855F7L) // Purple Neon Glow Border
        else -> Color(0x1FFFFFFFL)
    }

// ------------------------------------------------------------------
// Semantic accent colors — theme-reactive
// ------------------------------------------------------------------
val NeonGreen: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF00FFB2L)
        else -> Color(0xFF00FF9CL) // Original Neon Green
    }

val NeonGreenGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x40A855F7L) // In Hinata theme, glow is always purple!
        else -> Color(0x6600FF9CL)             // Original Neon Green Glow
    }

val NeonGreenDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF008F64L)
        else -> Color(0xFF00995EL)
    }

val NeonRed: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFFB7185L) // Sakura Rose Pink for numbers
        else -> Color(0xFFFF3366L)
    }

val NeonRedGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x40A855F7L) // In Hinata theme, glow is always purple!
        else -> Color(0x66FF3366L)
    }

val NeonRedDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF9F1239L)
        else -> Color(0xFF8F1332L)
    }

val NeonYellow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFFACC15L) // Warm Golden Amber for initial number
        else -> Color(0xFFFFD600L)
    }

val NeonYellowGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x40A855F7L) // In Hinata theme, glow is always purple!
        else -> Color(0x66FFD600L)
    }

val NeonYellowDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF854D0EL)
        else -> Color(0xFF8F7800L)
    }

val NeonCyan: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFC084FCL) // Lavender Violet for Headers & Labels
        else -> Color(0xFF00E5FFL)
    }

val NeonCyanGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x40A855F7L)
        else -> Color(0x6600E5FFL)
    }

val NeonPurple: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFA855F7L)
        else -> Color(0xFFA855F7L)
    }

val NeonPurpleGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x66A855F7L)
        else -> Color(0x66A855F7L)
    }

// ------------------------------------------------------------------
// Typography
// ------------------------------------------------------------------
val TextPrimary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFF3E8FFL) // High-contrast pale lavender
        else -> Color(0xFFFFFFFFL)
    }

val TextSecondary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFD8B4FEL) // Medium lavender
        else -> Color(0xFFA1A1AAL)
    }

val TextMuted: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFA855F7L) // Lavender muted
        else -> Color(0xFF71717AL)
    }
