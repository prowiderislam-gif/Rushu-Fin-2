package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ------------------------------------------------------------------
// Theme selection (4 Clean Themes)
// ------------------------------------------------------------------
object AppTheme {
    const val DEFAULT = "DEFAULT"
    const val BASIC = "BASIC"
    const val HINATA = "HINATA"
    const val KAKASHI = "KAKASHI"

    // Backwards compatibility keys
    const val GOLDEN_HIVE = "GOLDEN_HIVE"
    const val SAKURA_BLOOM = "SAKURA_BLOOM"
    const val MOONLIT_PURPLE = "MOONLIT_PURPLE"
}

val LocalAppTheme = compositionLocalOf { AppTheme.KAKASHI }

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
val HinataGold = Color(0xFFFACC15L)             // Fixed initial gold number

val HinataPurpleLight = Color(0xFFF3E8FFL)      // Crisp lavender text
val HinataPurpleMuted = Color(0xFFC084FCL)      // Muted lavender for titles/subtitles
val HinataSakuraPink = Color(0xFFF472B6L)       // Sakura Petal Pink
val HinataPillBg = Color(0x803B0764L)           // Pill / Tab / Badge Background
val HinataPillBorder = Color(0x80A855F7L)       // Pill / Tab / Badge Border
val HinataButtonBg = Color(0xFF2E1065L)         // Violet Button Container

// ------------------------------------------------------------------
// Kakashi (Chidori Lightning & Sharingan) Palette
// ------------------------------------------------------------------
val KakashiBackground = Color(0xFF020612L)      // Deep Midnight Storm Canvas
val KakashiSurface = Color(0xFF07101EL)         // Dark Lightning-Slate Card Surface
val KakashiCardBorder = Color(0x8000D2FFL)      // Electric Chidori Cyan Border
val KakashiCardGlow = Color(0x5500D2FFL)        // Electric Cyan Aura Glow
val KakashiElectricCyan = Color(0xFF00D2FFL)    // Main Live Balance & Chidori Lightning
val KakashiIceBlue = Color(0xFF38BDF8L)         // Fixed Initial & Income Accents
val KakashiSharinganRed = Color(0xFFFF3366L)    // Expense & Liability Sharingan Crimson
val KakashiPillBg = Color(0x66082F49L)          // Deep Navy Badge Container
val KakashiPillBorder = Color(0x9900D2FFL)      // Electric Cyan Pill Border
val KakashiButtonBg = Color(0xFF0C243FL)        // Deep Navy-Cyan Action Button

// ------------------------------------------------------------------
// Background / Surface colors — theme-reactive
// ------------------------------------------------------------------
val CanvasBackground: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiBackground
        AppTheme.HINATA -> Color(0xFF040008L)
        AppTheme.BASIC -> Color(0xFF0B0F19L)    // Calm, eye-safe deep slate
        else -> Color(0xFF050505L)
    }

val SurfaceDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> Color(0xFF050B17L)
        AppTheme.HINATA -> Color(0xFF080112L)
        AppTheme.BASIC -> Color(0xFF131C2EL)
        else -> Color(0xFF0A0A0DL)
    }

val SurfaceCard: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiSurface
        AppTheme.HINATA -> Color(0xFF0B0216L)
        AppTheme.BASIC -> Color(0xFF1E293BL)
        else -> Color(0xFF071710L)              // Glowing neon green glass tint for Default
    }

val CardGlass: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiSurface
        AppTheme.HINATA -> Color(0xFF0B0216L)
        AppTheme.BASIC -> Color(0xFF1E293BL)
        else -> Color(0xFF06160FL)
    }

val CardGlassBorder: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiCardBorder
        AppTheme.HINATA -> Color(0x66A855F7L)
        AppTheme.BASIC -> Color(0xFF334155L)
        else -> Color(0x4000FF9CL)
    }

// ------------------------------------------------------------------
// Semantic accent colors — theme-reactive
// ------------------------------------------------------------------
val NeonGreen: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiElectricCyan // Electric blue live balance & income
        AppTheme.HINATA -> Color(0xFF00FFB2L)
        AppTheme.BASIC -> Color(0xFF38BDF8L)
        else -> Color(0xFF00FF9CL)
    }

val NeonGreenGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x6600FF9CL)
    }

val NeonGreenDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> Color(0xFF0284C7L)
        AppTheme.HINATA -> Color(0xFF008F64L)
        AppTheme.BASIC -> Color(0xFF0284C7L)
        else -> Color(0xFF00995EL)
    }

val NeonRed: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiSharinganRed
        AppTheme.HINATA -> Color(0xFFFB7185L)
        AppTheme.BASIC -> Color(0xFFF87171L)
        else -> Color(0xFFFF3366L)
    }

val NeonRedGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> Color(0x55FF3366L)
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x66FF3366L)
    }

val NeonRedDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> Color(0xFF991B1BL)
        AppTheme.HINATA -> Color(0xFF9F1239L)
        AppTheme.BASIC -> Color(0xFFDC2626L)
        else -> Color(0xFF8F1332L)
    }

val NeonYellow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiIceBlue      // Electric Ice Blue for Fixed Initial
        AppTheme.HINATA -> Color(0xFFFACC15L)
        AppTheme.BASIC -> Color(0xFFFDE047L)
        else -> Color(0xFFFFD600L)
    }

val NeonYellowGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x66FFD600L)
    }

val NeonYellowDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> Color(0xFF0369A1L)
        AppTheme.HINATA -> Color(0xFF854D0EL)
        AppTheme.BASIC -> Color(0xFFCA8A04L)
        else -> Color(0xFF8F7800L)
    }

val NeonCyan: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiElectricCyan
        AppTheme.HINATA -> Color(0xFFC084FCL)
        AppTheme.BASIC -> Color(0xFF94A3B8L)
        else -> Color(0xFF00E5FFL)
    }

val NeonCyanGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x6600E5FFL)
    }

val NeonPurple: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiElectricCyan
        AppTheme.HINATA -> Color(0xFFA855F7L)
        AppTheme.BASIC -> Color(0xFFCBD5E1L)
        else -> Color(0xFFA855F7L)
    }

val NeonPurpleGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x66A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x66A855F7L)
    }

// ------------------------------------------------------------------
// Typography
// ------------------------------------------------------------------
val TextPrimary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> Color(0xFFF0F9FFL)  // Crisp ice-white
        AppTheme.HINATA -> Color(0xFFF3E8FFL)
        AppTheme.BASIC -> Color(0xFFF8FAFCL)
        else -> Color(0xFFFFFFFFL)
    }

val TextSecondary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> Color(0xFFBAE6FDL)  // Soft sky-tinted white
        AppTheme.HINATA -> Color(0xFFD8B4FEL)
        AppTheme.BASIC -> Color(0xFFCBD5E1L)
        else -> Color(0xFFA1A1AAL)
    }

val TextMuted: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.KAKASHI -> Color(0xFF0284C7L)  // Muted lightning cyan
        AppTheme.HINATA -> Color(0xFFA855F7L)
        AppTheme.BASIC -> Color(0xFF94A3B8L)
        else -> Color(0xFF71717AL)
    }
