package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ------------------------------------------------------------------
// Theme selection
// ------------------------------------------------------------------
object AppTheme {
    const val DEFAULT = "DEFAULT"
    const val BASIC = "BASIC"
    const val GOLDEN_HIVE = "GOLDEN_HIVE"
    const val SAKURA_BLOOM = "SAKURA_BLOOM"
    const val MOONLIT_PURPLE = "MOONLIT_PURPLE"
    const val HINATA = "HINATA"
}

// Default theme is set to HINATA (Obsidian & Neon Lavender)
val LocalAppTheme = compositionLocalOf { AppTheme.HINATA }

// ------------------------------------------------------------------
// Hinata Dedicated Standalone Colors (For HinataMainBalanceCard)
// ------------------------------------------------------------------
val HinataBackground = Color(0xFF040008L)       // Pure Deep Obsidian Canvas
val HinataSurface = Color(0xFF0B0216L)          // Dark Violet Card Surface
val HinataCardBorder = Color(0x40A855F7L)       // Purple Neon Glow Border
val HinataCardGlow = Color(0x33A855F7L)         // Outer Shadow Glow

// Neon Accents
val NeonMintGreen = Color(0xFF00FFB2L)          // Live Balance Neon Mint Green
val EmeraldIncome = Color(0xFF34D399L)          // Total Income Text & Icon
val RoseDeficit = Color(0xFFF43F5EL)            // Total Expenses & Deficit Text

// Soft Lavender & Sakura Accents
val HinataPurpleLight = Color(0xFFE9D5FFL)      // Text Primary
val HinataPurpleMuted = Color(0xFFC084FCL)      // Muted Purple
val HinataSakuraPink = Color(0xFFF472B6L)       // Sakura Petal Pink
val HinataPillBg = Color(0x803B0764L)           // Badge / Button Background
val HinataPillBorder = Color(0x80A855F7L)       // Badge / Button Border
val HinataButtonBg = Color(0xFF2E1065L)         // Button Purple

// ------------------------------------------------------------------
// Background / Surface colors — theme-reactive
// ------------------------------------------------------------------
val CanvasBackground: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF040008L) // Deep Obsidian
        AppTheme.MOONLIT_PURPLE -> Color(0xFF0C0714L)
        else -> Color(0xFF050505L)
    }

val SurfaceDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF0B0216L) // Dark Violet
        AppTheme.MOONLIT_PURPLE -> Color(0xFF150E20L)
        else -> Color(0xFF0A0A0DL)
    }

val SurfaceCard: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF130826L) // Obsidian Card Surface
        AppTheme.MOONLIT_PURPLE -> Color(0xFF1B1228L)
        else -> Color(0xFF121216L)
    }

val CardGlass: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF170B2CL)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF1C1226L)
        else -> Color(0xFF141418L)
    }

val CardGlassBorder: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x40A855F7L) // Neon Purple glow border
        AppTheme.GOLDEN_HIVE -> Color(0x66F2B705L)
        AppTheme.SAKURA_BLOOM -> Color(0x66FF6FA5L)
        AppTheme.MOONLIT_PURPLE -> Color(0x66B98BE0L)
        else -> Color(0x1FFFFFFFL)
    }

// ------------------------------------------------------------------
// Semantic accent colors — theme-reactive
// ------------------------------------------------------------------
val NeonGreen: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF00FFB2L) // Neon Mint Green for Live Balance
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFC72CL)
        AppTheme.SAKURA_BLOOM -> Color(0xFFFF7CAEL)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF6FF2A6L)
        else -> Color(0xFF00FF9CL)
    }

val NeonGreenGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x6600FFB2L)
        AppTheme.GOLDEN_HIVE -> Color(0x66FFC72CL)
        AppTheme.SAKURA_BLOOM -> Color(0x66FF7CAEL)
        AppTheme.MOONLIT_PURPLE -> Color(0x666FF2A6L)
        else -> Color(0x6600FF9CL)
    }

val NeonGreenDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF00B37DL)
        AppTheme.GOLDEN_HIVE -> Color(0xFFAD8619L)
        AppTheme.SAKURA_BLOOM -> Color(0xFFAD5476L)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF469C6DL)
        else -> Color(0xFF00995EL)
    }

val NeonRed: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFF43F5EL) // Rose Red for expenses/deficit
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFB300L)
        AppTheme.SAKURA_BLOOM -> Color(0xFFFF4C86L)
        AppTheme.MOONLIT_PURPLE -> Color(0xFFE85FA0L)
        else -> Color(0xFFFF3366L)
    }

val NeonRedGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x66F43F5EL)
        AppTheme.GOLDEN_HIVE -> Color(0x66FFB300L)
        AppTheme.SAKURA_BLOOM -> Color(0x66FF4C86L)
        AppTheme.MOONLIT_PURPLE -> Color(0x66E85FA0L)
        else -> Color(0x66FF3366L)
    }

val NeonRedDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFF9F1239L)
        AppTheme.GOLDEN_HIVE -> Color(0xFFA87700L)
        AppTheme.SAKURA_BLOOM -> Color(0xFFA8335AL)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF9C3F6BL)
        else -> Color(0xFF8F1332L)
    }

val NeonYellow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFFACC15L) // Fixed initial gold
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFD54AL)
        AppTheme.SAKURA_BLOOM -> Color(0xFFFFA6C9L)
        AppTheme.MOONLIT_PURPLE -> Color(0xFFE8C55AL)
        else -> Color(0xFFFFD600L)
    }

val NeonYellowGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x66FACC15L)
        AppTheme.GOLDEN_HIVE -> Color(0x66FFD54AL)
        AppTheme.SAKURA_BLOOM -> Color(0x66FFA6C9L)
        AppTheme.MOONLIT_PURPLE -> Color(0x66E8C55AL)
        else -> Color(0x66FFD600L)
    }

val NeonYellowDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFA16207L)
        AppTheme.GOLDEN_HIVE -> Color(0xFFAD9333L)
        AppTheme.SAKURA_BLOOM -> Color(0xFFAD7489L)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF9C853DL)
        else -> Color(0xFF8F7800L)
    }

val NeonCyan: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFC084FCL) // Lavender accent
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFECB3L)
        AppTheme.SAKURA_BLOOM -> Color(0xFFFFC1D9L)
        AppTheme.MOONLIT_PURPLE -> Color(0xFFC9A6F5L)
        else -> Color(0xFF00E5FFL)
    }

val NeonCyanGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x66C084FCL)
        AppTheme.GOLDEN_HIVE -> Color(0x66FFECB3L)
        AppTheme.SAKURA_BLOOM -> Color(0x66FFC1D9L)
        AppTheme.MOONLIT_PURPLE -> Color(0x66C9A6F5L)
        else -> Color(0x6600E5FFL)
    }

val NeonPurple: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFA855F7L) // Primary Hinata Neon Purple
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFD54AL)
        AppTheme.SAKURA_BLOOM -> Color(0xFFFF9CC7L)
        AppTheme.MOONLIT_PURPLE -> Color(0xFFB98BE0L)
        else -> Color(0xFFA855F7L)
    }

val NeonPurpleGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0x66A855F7L)
        AppTheme.GOLDEN_HIVE -> Color(0x66FFD54AL)
        AppTheme.SAKURA_BLOOM -> Color(0x66FF9CC7L)
        AppTheme.MOONLIT_PURPLE -> Color(0x66B98BE0L)
        else -> Color(0x66A855F7L)
    }

// ------------------------------------------------------------------
// Typography
// ------------------------------------------------------------------
val TextPrimary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFF3E8FFL) // High contrast pale lavender
        AppTheme.MOONLIT_PURPLE -> Color(0xFFF6F0FBL)
        else -> Color(0xFFFFFFFFL)
    }

val TextSecondary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFD8B4FEL) // Medium lavender
        AppTheme.GOLDEN_HIVE -> Color(0xFFC9B77AL)
        AppTheme.SAKURA_BLOOM -> Color(0xFFCB9BAEL)
        AppTheme.MOONLIT_PURPLE -> Color(0xFFB8A6C9L)
        else -> Color(0xFFA1A1AAL)
    }

val TextMuted: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.HINATA -> Color(0xFFA855F7L) // Subdued purple
        AppTheme.GOLDEN_HIVE -> Color(0xFF9C8F5CL)
        AppTheme.SAKURA_BLOOM -> Color(0xFF9C7A8AL)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF8A7A9CL)
        else -> Color(0xFF71717AL)
    }
