package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ------------------------------------------------------------------
// Theme selection
// ------------------------------------------------------------------
// Mutually exclusive display themes. The chosen theme is provided at
// the root of the screen and read by the color properties below, so
// every existing call site in the app (which just references e.g.
// `NeonGreen` as a bare value) automatically re-themes without needing
// to be individually edited.
object AppTheme {
    const val DEFAULT = "DEFAULT"
    const val BASIC = "BASIC"
    const val GOLDEN_HIVE = "GOLDEN_HIVE"
    const val SAKURA_BLOOM = "SAKURA_BLOOM"
    const val MOONLIT_PURPLE = "MOONLIT_PURPLE"
}

val LocalAppTheme = compositionLocalOf { AppTheme.DEFAULT }

// ------------------------------------------------------------------
// Background / Surface colors — theme-reactive
// ------------------------------------------------------------------
val CanvasBackground: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.MOONLIT_PURPLE -> Color(0xFF0C0714) // near-black with a violet undertone
        else -> Color(0xFF050505)
    }

val SurfaceDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.MOONLIT_PURPLE -> Color(0xFF150E20)
        else -> Color(0xFF0A0A0D)
    }

val SurfaceCard: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.MOONLIT_PURPLE -> Color(0xFF1B1228)
        else -> Color(0xFF121216)
    }

val CardGlass: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.MOONLIT_PURPLE -> Color(0xFF1C1226)
        else -> Color(0xFF141418)
    }

val CardGlassBorder: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0x66F2B705) // amber-gold border
        AppTheme.SAKURA_BLOOM -> Color(0x66FF6FA5) // rose-pink border
        AppTheme.MOONLIT_PURPLE -> Color(0x66B98BE0) // lavender border
        else -> Color(0x1FFFFFFF)
    }

// ------------------------------------------------------------------
// Semantic accent colors — theme-reactive.
//
// Golden Hive and Sakura Bloom are single-accent "monochrome" themes
// (every figure — balance, initial, liability — uses the same family
// of gold / pink, exactly like the reference images), while Moonlit
// Purple keeps the original green/gold/pink separation (live balance
// green, fixed initial gold, liability pink) with an overall violet
// chrome, matching its reference image.
// ------------------------------------------------------------------
val NeonGreen: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFC72C)
        AppTheme.SAKURA_BLOOM -> Color(0xFFFF7CAE)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF6FF2A6) // soft minty green, matches reference
        else -> Color(0xFF00FF9C)
    }
val NeonGreenGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0x66FFC72C)
        AppTheme.SAKURA_BLOOM -> Color(0x66FF7CAE)
        AppTheme.MOONLIT_PURPLE -> Color(0x666FF2A6)
        else -> Color(0x6600FF9C)
    }
val NeonGreenDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFFAD8619)
        AppTheme.SAKURA_BLOOM -> Color(0xFFAD5476)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF469C6D)
        else -> Color(0xFF00995E)
    }

val NeonRed: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFB300) // deeper amber, still gold family
        AppTheme.SAKURA_BLOOM -> Color(0xFFFF4C86) // stronger rose-pink
        AppTheme.MOONLIT_PURPLE -> Color(0xFFE85FA0) // magenta-pink, matches reference liability color
        else -> Color(0xFFFF3366)
    }
val NeonRedGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0x66FFB300)
        AppTheme.SAKURA_BLOOM -> Color(0x66FF4C86)
        AppTheme.MOONLIT_PURPLE -> Color(0x66E85FA0)
        else -> Color(0x66FF3366)
    }
val NeonRedDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFFA87700)
        AppTheme.SAKURA_BLOOM -> Color(0xFFA8335A)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF9C3F6B)
        else -> Color(0xFF8F1332)
    }

val NeonYellow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFD54A) // bright honey gold
        AppTheme.SAKURA_BLOOM -> Color(0xFFFFA6C9)
        AppTheme.MOONLIT_PURPLE -> Color(0xFFE8C55A) // warm gold, matches reference fixed-initial color
        else -> Color(0xFFFFD600)
    }
val NeonYellowGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0x66FFD54A)
        AppTheme.SAKURA_BLOOM -> Color(0x66FFA6C9)
        AppTheme.MOONLIT_PURPLE -> Color(0x66E8C55A)
        else -> Color(0x66FFD600)
    }
val NeonYellowDark: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFFAD9333)
        AppTheme.SAKURA_BLOOM -> Color(0xFFAD7489)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF9C853D)
        else -> Color(0xFF8F7800)
    }

val NeonCyan: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFECB3) // pale honey cream, general accent
        AppTheme.SAKURA_BLOOM -> Color(0xFFFFC1D9) // signature soft pink accent
        AppTheme.MOONLIT_PURPLE -> Color(0xFFC9A6F5) // lavender, general accent (title, borders, buttons)
        else -> Color(0xFF00E5FF)
    }
val NeonCyanGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0x66FFECB3)
        AppTheme.SAKURA_BLOOM -> Color(0x66FFC1D9)
        AppTheme.MOONLIT_PURPLE -> Color(0x66C9A6F5)
        else -> Color(0x6600E5FF)
    }

val NeonPurple: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFFFFD54A)
        AppTheme.SAKURA_BLOOM -> Color(0xFFFF9CC7)
        AppTheme.MOONLIT_PURPLE -> Color(0xFFB98BE0)
        else -> Color(0xFFA855F7)
    }
val NeonPurpleGlow: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0x66FFD54A)
        AppTheme.SAKURA_BLOOM -> Color(0x66FF9CC7)
        AppTheme.MOONLIT_PURPLE -> Color(0x66B98BE0)
        else -> Color(0x66A855F7)
    }

// ------------------------------------------------------------------
// Typography — kept high-contrast across all themes for reliable
// readability; only a very slight tint shift for the darker themes.
// ------------------------------------------------------------------
val TextPrimary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.MOONLIT_PURPLE -> Color(0xFFF6F0FB)
        else -> Color(0xFFFFFFFF)
    }
val TextSecondary: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFFC9B77A)
        AppTheme.SAKURA_BLOOM -> Color(0xFFCB9BAE)
        AppTheme.MOONLIT_PURPLE -> Color(0xFFB8A6C9)
        else -> Color(0xFFA1A1AA)
    }
val TextMuted: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.GOLDEN_HIVE -> Color(0xFF9C8F5C)
        AppTheme.SAKURA_BLOOM -> Color(0xFF9C7A8A)
        AppTheme.MOONLIT_PURPLE -> Color(0xFF8A7A9C)
        else -> Color(0xFF71717A)
        package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Obsidian & Hinata Dark Theme Palette
val HinataBackground = Color(0xFF040008)       // Deep Obsidian Canvas
val HinataSurface = Color(0xFF0B0216)          // Dark Violet Card Surface
val HinataCardBorder = Color(0x40A855F7)       // Purple Neon Glow Border
val HinataCardGlow = Color(0x33A855F7)         // Outer Shadow Glow

// Neon & Metric Accents
val NeonMintGreen = Color(0xFF00FFB2)          // Live Balance Neon Mint
val EmeraldIncome = Color(0xFF34D399)          // Total Income Text & Icon
val RoseDeficit = Color(0xFFF43F5E)            // Total Expenses & Deficit Text

// Soft Lavender & Sakura Accents
val HinataPurpleLight = Color(0xFFE9D5FF)      // Text Primary
val HinataPurpleMuted = Color(0xFFC084FC)      // Muted Purple
val HinataSakuraPink = Color(0xFFF472B6)       // Sakura Petal Pink
val HinataPillBg = Color(0x803B0764)           // Badge / Button Background
val HinataPillBorder = Color(0x80A855F7)       // Badge / Button Border
val HinataButtonBg = Color(0xFF2E1065)         // Button Purple
    }
