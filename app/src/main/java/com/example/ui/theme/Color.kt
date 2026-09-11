package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ------------------------------------------------------------------
// Theme selection (6 Themes)
// ------------------------------------------------------------------
object AppTheme {
    const val DEFAULT = "DEFAULT"
    const val BASIC = "BASIC"
    const val HINATA = "HINATA"
    const val KAKASHI = "KAKASHI"
    const val BUMBLEBEE = "BUMBLEBEE"
    const val RUH = "RUH"

    // Backwards compatibility keys
    const val GOLDEN_HIVE = "GOLDEN_HIVE"
    const val SAKURA_BLOOM = "SAKURA_BLOOM"
    const val MOONLIT_PURPLE = "MOONLIT_PURPLE"
}

val LocalAppTheme = compositionLocalOf { AppTheme.RUH }

// ------------------------------------------------------------------
// Ruh / Itachi (Blood Red, Sharingan & Crimson Crows) Palette
// ------------------------------------------------------------------
val RuhBackground = Color(0xFF020002L)         // Pitch black midnight
val RuhSurface = Color(0xFF0D0204L)            // Dark obsidian crimson glass
val RuhCardBorder = Color(0xFFDC2626L)         // Vibrant blood red border
val RuhCardGlow = Color(0x66DC2626L)           // Sharingan crimson glow
val RuhBloodRed = Color(0xFFFF3B56L)           // Main live balance neon blood red
val RuhIncomeRed = Color(0xFFFF6B81L)          // Luminous crimson rose income
val RuhExpenseRed = Color(0xFFB91C1CL)         // Deep dark blood expense
val RuhFixedRed = Color(0xFFEF4444L)           // Fixed initial glowing crimson
val RuhPillBg = Color(0xFF260408L)             // Dark crimson pill background
val RuhPillBorder = Color(0xFF991B1BL)         // Crimson pill border
val RuhButtonBg = Color(0xFF7F1D1DL)           // Deep crimson button background

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
val BumblebeeBackground = Color(0xFF020100L)
val BumblebeeSurface = Color(0xFF0A0601L)
val BumblebeeCardBorder = Color(0xFFF59E0BL)
val BumblebeeCardGlow = Color(0x66F59E0BL)
val BumblebeeGold = Color(0xFFFFB703L)
val BumblebeeIncomeGold = Color(0xFFFBBF24L)
val BumblebeeFlameOrange = Color(0xFFF97316L)
val BumblebeeFixedGold = Color(0xFFF59E0BL)
val BumblebeePillBg = Color(0xFF261502L)
val BumblebeePillBorder = Color(0xFFD97706L)
val BumblebeeButtonBg = Color(0xFFB45309L)

// ------------------------------------------------------------------
// Background / Surface colors — theme-reactive
// ------------------------------------------------------------------
val CanvasBackground: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhBackground
        AppTheme.BUMBLEBEE -> BumblebeeBackground
        AppTheme.KAKASHI -> KakashiBackground
        AppTheme.HINATA -> HinataBackground
        AppTheme.BASIC -> Color(0xFF0B0F19L)
        else -> Color(0xFF050505L)
    }

val SurfaceDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> Color(0xFF080103L)
        AppTheme.BUMBLEBEE -> Color(0xFF070401L)
        AppTheme.KAKASHI -> Color(0xFF02070EL)
        AppTheme.HINATA -> Color(0xFF080112L)
        AppTheme.BASIC -> Color(0xFF131C2EL)
        else -> Color(0xFF0A0A0DL)
    }

val SurfaceCard: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhSurface
        AppTheme.BUMBLEBEE -> BumblebeeSurface
        AppTheme.KAKASHI -> KakashiSurface
        AppTheme.HINATA -> HinataSurface
        AppTheme.BASIC -> Color(0xFF1E293BL)
        else -> Color(0xFF071710L)
    }

val CardGlass: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhSurface
        AppTheme.BUMBLEBEE -> BumblebeeSurface
        AppTheme.KAKASHI -> KakashiSurface
        AppTheme.HINATA -> HinataSurface
        AppTheme.BASIC -> Color(0xFF1E293BL)
        else -> Color(0xFF06160FL)
    }

val CardGlassBorder: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhCardBorder
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
        AppTheme.RUH -> RuhIncomeRed
        AppTheme.BUMBLEBEE -> BumblebeeIncomeGold
        AppTheme.KAKASHI -> KakashiIncomeBlue
        AppTheme.HINATA -> Color(0xFF00FFB2L)
        AppTheme.BASIC -> Color(0xFF38BDF8L)
        else -> Color(0xFF00FF9CL)
    }

val NeonGreenGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhCardGlow
        AppTheme.BUMBLEBEE -> BumblebeeCardGlow
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x6600FF9CL)
    }

val NeonGreenDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> Color(0xFF450A0AL)
        AppTheme.BUMBLEBEE -> Color(0xFF78350FL)
        AppTheme.KAKASHI -> Color(0xFF0369A1L)
        AppTheme.HINATA -> Color(0xFF008F64L)
        AppTheme.BASIC -> Color(0xFF0284C7L)
        else -> Color(0xFF00995EL)
    }

val NeonRed: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhExpenseRed
        AppTheme.BUMBLEBEE -> BumblebeeFlameOrange
        AppTheme.KAKASHI -> KakashiSharinganRed
        AppTheme.HINATA -> Color(0xFFFB7185L)
        AppTheme.BASIC -> Color(0xFFF87171L)
        else -> Color(0xFFFF3366L)
    }

val NeonRedGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhCardGlow
        AppTheme.BUMBLEBEE -> Color(0x66F97316L)
        AppTheme.KAKASHI -> Color(0x66FF334BL)
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x66FF3366L)
    }

val NeonRedDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> Color(0xFF2B050AL)
        AppTheme.BUMBLEBEE -> Color(0xFF7C2D12L)
        AppTheme.KAKASHI -> Color(0xFF991B1BL)
        AppTheme.HINATA -> Color(0xFF9F1239L)
        AppTheme.BASIC -> Color(0xFFDC2626L)
        else -> Color(0xFF8F1332L)
    }

val NeonYellow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhFixedRed
        AppTheme.BUMBLEBEE -> BumblebeeFixedGold
        AppTheme.KAKASHI -> KakashiFixedBlue
        AppTheme.HINATA -> Color(0xFFFACC15L)
        AppTheme.BASIC -> Color(0xFFFDE047L)
        else -> Color(0xFFFFD600L)
    }

val NeonYellowGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhCardGlow
        AppTheme.BUMBLEBEE -> BumblebeeCardGlow
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x66FFD600L)
    }

val NeonCyan: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhBloodRed
        AppTheme.BUMBLEBEE -> BumblebeeGold
        AppTheme.KAKASHI -> KakashiBalanceBlue
        AppTheme.HINATA -> Color(0xFFC084FCL)
        AppTheme.BASIC -> Color(0xFF94A3B8L)
        else -> Color(0xFF00E5FFL)
    }

val NeonCyanGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhCardGlow
        AppTheme.BUMBLEBEE -> BumblebeeCardGlow
        AppTheme.KAKASHI -> KakashiCardGlow
        AppTheme.HINATA -> Color(0x40A855F7L)
        AppTheme.BASIC -> Color.Transparent
        else -> Color(0x6600E5FFL)
    }

val NeonPurple: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhBloodRed
        AppTheme.BUMBLEBEE -> BumblebeeGold
        AppTheme.KAKASHI -> KakashiBalanceBlue
        AppTheme.HINATA -> Color(0xFFA855F7L)
        AppTheme.BASIC -> Color(0xFFCBD5E1L)
        else -> Color(0xFFA855F7L)
    }

val NeonPurpleGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> RuhCardGlow
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
        AppTheme.RUH -> Color(0xFFFFFFFFL)
        AppTheme.BUMBLEBEE -> Color(0xFFFFFFFFL)
        AppTheme.KAKASHI -> Color(0xFFFFFFFFL)
        AppTheme.HINATA -> Color(0xFFF3E8FFL)
        AppTheme.BASIC -> Color(0xFFF8FAFCL)
        else -> Color(0xFFFFFFFFL)
    }

val TextSecondary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> Color(0xFFE57373L) // Soft crimson secondary
        AppTheme.BUMBLEBEE -> Color(0xFFD97706L)
        AppTheme.KAKASHI -> Color(0xFF8FA3BFL)
        AppTheme.HINATA -> Color(0xFFD8B4FEL)
        AppTheme.BASIC -> Color(0xFFCBD5E1L)
        else -> Color(0xFFA1A1AAL)
    }

val TextMuted: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.RUH -> Color(0xFF991B1BL)
        AppTheme.BUMBLEBEE -> Color(0xFF78350FL)
        AppTheme.KAKASHI -> Color(0xFF536780L)
        AppTheme.HINATA -> Color(0xFFA855F7L)
        AppTheme.BASIC -> Color(0xFF94A3B8L)
        else -> Color(0xFF71717AL)
    }