package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ------------------------------------------------------------------
// Theme selection (5 Clean Themes)
// ------------------------------------------------------------------
object AppTheme {
    const val DEFAULT = "DEFAULT"
    const val BASIC = "BASIC"
    const val HINATA = "HINATA"
    const val KAKASHI = "KAKASHI"
    const val BUMBLEBEE = "BUMBLEBEE"

    // Backwards compatibility keys
    const val GOLDEN_HIVE = "GOLDEN_HIVE"
    const val SAKURA_BLOOM = "SAKURA_BLOOM"
    const val MOONLIT_PURPLE = "MOONLIT_PURPLE"
}

val LocalAppTheme = compositionLocalOf { AppTheme.BUMBLEBEE }

// ------------------------------------------------------------------
// Hinata Dedicated Standalone Palette
// ------------------------------------------------------------------
val HinataBackground = Color(0xFF040008L)
val HinataSurface = Color(0xFF0B0216L)
val HinataCardBorder = Color(0x66A855F7L)
val HinataCardGlow = Color(0x40A855F7L)

val NeonMintGreen = Color(0xFF00FFB2L)
val EmeraldIncome = Color(0xFF00FFB2L)
val RoseDeficit = Color(0xFFFB7185L)
val HinataGold = Color(0xFFFACC15L)

val HinataPurpleLight = Color(0xFFF3E8FFL)
val HinataPurpleMuted = Color(0xFFC084FCL)
val HinataSakuraPink = Color(0xFFF472B6L)
val HinataPillBg = Color(0x803B0764L)
val HinataPillBorder = Color(0x80A855F7L)
val HinataButtonBg = Color(0xFF2E1065L)

// ------------------------------------------------------------------
// Kakashi (True Pitch Black & Electric Chidori Blue) Palette
// ------------------------------------------------------------------
val KakashiBackground = Color(0xFF010307L)
val KakashiSurface = Color(0xFF030A14L)
val KakashiCardBorder = Color(0xFF0284C7L)
val KakashiCardGlow = Color(0x660284C7L)
val KakashiElectricCyan = Color(0xFF14B8A6L)
val KakashiBalanceBlue = Color(0xFF18C0F5L)
val KakashiIncomeBlue = Color(0xFF20D0E8L)
val KakashiSharinganRed = Color(0xFFFF334BL)
val KakashiFixedBlue = Color(0xFF29B6F6L)
val KakashiPillBg = Color(0xFF051829L)
val KakashiPillBorder = Color(0xFF0EA5E9L)
val KakashiButtonBg = Color(0xFF04192DL)

// ------------------------------------------------------------------
// Bumblebee (Glossy Metallic Amber Gold & Pitch Black) Palette
// ------------------------------------------------------------------
val BumblebeeBackground = Color(0xFF020100L)     // Pitch Black Canvas
val BumblebeeSurface = Color(0xFF0A0601L)        // Deep Obsidian Gold-Tinted Surface
val BumblebeeCardBorder = Color(0xFFF59E0BL)     // Metallic Amber Gold Edge
val BumblebeeCardGlow = Color(0x66F59E0BL)       // Amber Fire Glow
val BumblebeeGold = Color(0xFFFFB703L)           // Main Live Balance Metallic Gold
val BumblebeeIncomeGold = Color(0xFFFBBF24L)     // +ive Income Bright Gold
val BumblebeeFlameOrange = Color(0xFFF97316L)    // -ive Expense Flame Orange
val BumblebeeFixedGold = Color(0xFFF59E0BL)      // Fixed Initial Deep Amber Gold
val BumblebeePillBg = Color(0xFF261502L)         // Dark Honey-Amber Pill Background
val BumblebeePillBorder = Color(0xFFD97706L)     // Sharp Honey-Gold Pill Border
val BumblebeeButtonBg = Color(0xFFB45309L)       // Shiny Gold Action Button
val BumblebeeButtonText = Color(0xFF000000L)     // High-contrast black on gold

// ------------------------------------------------------------------
// Background / Surface colors — theme-reactive
// ------------------------------------------------------------------
val CanvasBackground: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeBackground
        AppTheme.KAKASHI -> KakashiBackground
        AppTheme.HINATA -> HinataBackground
        AppTheme.BASIC -> Color(0xFF0B0F19L)
        else -> Color(0xFF050505L)
    }

val SurfaceDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> Color(0xFF070401L)
        AppTheme.KAKASHI -> Color(0xFF02070EL)
        AppTheme.HINATA -> Color(0xFF080112L)
        AppTheme.BASIC -> Color(0xFF131C2EL)
        else -> Color(0xFF0A0A0DL)
    }

val SurfaceCard: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeSurface
        AppTheme.KAKASHI -> KakashiSurface
        AppTheme.HINATA -> HinataSurface
        AppTheme.BASIC -> Color(0xFF1E293BL)
        else -> Color(0xFF071710L)
    }

val CardGlass: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeSurface
        AppTheme.KAKASHI -> KakashiSurface
        AppTheme.HINATA -> HinataSurface
        AppTheme.BASIC -> Color(0xFF1E293BL)
        else -> Color(0xFF06160FL)
    }

val CardGlassBorder: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeCardBorder
        AppTheme.KAKASHI -> KakashiCardBorder
        AppTheme.HINATA -> HinataCardBorder
        AppTheme.BASIC -> Color(0xFF334155L)
        else -> Color(0x4000FF9CL)
    }

// ------------------------------------------------------------------
// Semantic accent colors — theme-reactive
// ------------------------------------------------------------------
val NeonGreen: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeIncomeGold
        AppTheme.KAKASHI -> KakashiIncomeBlue
        AppTheme.HINATA -> Color(0xFF00FFB2L)
        AppTheme.BASIC -> Color(0xFF38BDF8L)
        else -> Color(0xFF00FF9CL)
    }

val NeonGreenGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeCardGlow
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x6600FF9CL)
    }

val NeonGreenDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> Color(0xFF78350FL)
        AppTheme.KAKASHI -> Color(0xFF0369A1L)
        AppTheme.HINATA -> Color(0xFF008F64L)
        AppTheme.BASIC -> Color(0xFF0284C7L)
        else -> Color(0xFF00995EL)
    }

val NeonRed: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeFlameOrange
        AppTheme.KAKASHI -> KakashiSharinganRed
        AppTheme.HINATA -> Color(0xFFFB7185L)
        AppTheme.BASIC -> Color(0xFFF87171L)
        else -> Color(0xFFFF3366L)
    }

val NeonRedGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> Color(0x66F97316L)
        AppTheme.KAKASHI -> Color(0x66FF334BL)
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x66FF3366L)
    }

val NeonRedDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> Color(0xFF7C2D12L)
        AppTheme.KAKASHI -> Color(0xFF991B1BL)
        AppTheme.HINATA -> Color(0xFF9F1239L)
        AppTheme.BASIC -> Color(0xFFDC2626L)
        else -> Color(0xFF8F1332L)
    }

val NeonYellow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeFixedGold
        AppTheme.KAKASHI -> KakashiFixedBlue
        AppTheme.HINATA -> Color(0xFFFACC15L)
        AppTheme.BASIC -> Color(0xFFFDE047L)
        else -> Color(0xFFFFD600L)
    }

val NeonYellowGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeCardGlow
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x66FFD600L)
    }

val NeonYellowDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> Color(0xFF92400EL)
        AppTheme.KAKASHI -> Color(0xFF0284C7L)
        AppTheme.HINATA -> Color(0xFF854D0EL)
        AppTheme.BASIC -> Color(0xFFCA8A04L)
        else -> Color(0xFF8F7800L)
    }

val NeonCyan: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeGold
        AppTheme.KAKASHI -> KakashiBalanceBlue
        AppTheme.HINATA -> Color(0xFFC084FCL)
        AppTheme.BASIC -> Color(0xFF94A3B8L)
        else -> Color(0xFF00E5FFL)
    }

val NeonCyanGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeCardGlow
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x6600E5FFL)
    }

val NeonPurple: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeGold
        AppTheme.KAKASHI -> KakashiBalanceBlue
        AppTheme.HINATA -> Color(0xFFA855F7L)
        AppTheme.BASIC -> Color(0xFFCBD5E1L)
        else -> Color(0xFFA855F7L)
    }

val NeonPurpleGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> BumblebeeCardGlow
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
        AppTheme.BUMBLEBEE -> Color(0xFFFFFFFFL)
        AppTheme.KAKASHI -> Color(0xFFFFFFFFL)
        AppTheme.HINATA -> Color(0xFFF3E8FFL)
        AppTheme.BASIC -> Color(0xFFF8FAFCL)
        else -> Color(0xFFFFFFFFL)
    }

val TextSecondary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> Color(0xFFD97706L) // Warm gold secondary
        AppTheme.KAKASHI -> Color(0xFF8FA3BFL)
        AppTheme.HINATA -> Color(0xFFD8B4FEL)
        AppTheme.BASIC -> Color(0xFFCBD5E1L)
        else -> Color(0xFFA1A1AAL)
    }

val TextMuted: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.BUMBLEBEE -> Color(0xFF78350FL)
        AppTheme.KAKASHI -> Color(0xFF536780L)
        AppTheme.HINATA -> Color(0xFFA855F7L)
        AppTheme.BASIC -> Color(0xFF94A3B8L)
        else -> Color(0xFF71717AL)
    }