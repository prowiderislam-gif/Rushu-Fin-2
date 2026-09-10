package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppTheme
import com.example.ui.theme.CardGlass
import com.example.ui.theme.CardGlassBorder
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedGlow
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.NeonYellowGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

/**
 * Custom glassmorphic container with frosted translucent finish and dynamic inner glow.
 */
@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    glowColor: Color = Color.Transparent,
    glowIntensity: Float = 0.45f,
    borderColor: Color = CardGlassBorder,
    backgroundColor: Color = CardGlass,
    content: @Composable BoxScope.() -> Unit
) {
    val theme = LocalAppTheme.current
    val basicMode = theme == AppTheme.BASIC
    val effectiveShape = shape

    val animatedGlowColor by animateColorAsState(
        targetValue = if (basicMode) Color.Transparent else glowColor,
        animationSpec = tween(durationMillis = 400),
        label = "glowColor"
    )

    Box(
        modifier = modifier
            .clip(effectiveShape)
            .background(
                brush = if (basicMode) {
                    Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0.95f),
                            backgroundColor.copy(alpha = 0.95f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0.70f),
                            backgroundColor.copy(alpha = 0.40f)
                        )
                    )
                }
            )
            .drawBehind {
                if (animatedGlowColor != Color.Transparent) {
                    // Inner glow edge simulation
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                animatedGlowColor.copy(alpha = glowIntensity * 0.35f),
                                animatedGlowColor.copy(alpha = glowIntensity * 0.10f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width.coerceAtLeast(size.height) * 0.7f
                        )
                    )
                    // Top highlight line
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                animatedGlowColor.copy(alpha = glowIntensity * 0.8f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(size.width * 0.15f, 1f),
                        end = Offset(size.width * 0.85f, 1f),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }
            .border(
                width = if (basicMode) 1.5.dp else 1.dp,
                brush = if (basicMode) {
                    Brush.linearGradient(colors = listOf(borderColor.copy(alpha = 0.9f), borderColor.copy(alpha = 0.9f)))
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            if (animatedGlowColor != Color.Transparent) animatedGlowColor.copy(alpha = 0.5f) else borderColor.copy(alpha = 0.6f),
                            borderColor.copy(alpha = 0.2f),
                            if (animatedGlowColor != Color.Transparent) animatedGlowColor.copy(alpha = 0.3f) else borderColor.copy(alpha = 0.35f)
                        )
                    )
                },
                shape = effectiveShape
            )
    ) {
        content()

        // Decorative cherry-blossom corner accents for the sakura-style themes —
        // purely visual, low alpha so they never compete with real content.
        val showCornerFlowers = theme == AppTheme.SAKURA_BLOOM || theme == AppTheme.MOONLIT_PURPLE
        if (showCornerFlowers) {
            Text(
                text = "🌸",
                fontSize = 15.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .alpha(0.5f)
            )
            Text(
                text = "🌸",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .alpha(0.55f)
            )
        }
    }
}

/**
 * Text style with neon glow shadow — automatically drops the glow/blur
 * in Basic theme (crisper, easier to read), and softens slightly in
 * Kitty theme to match its pastel palette.
 */
@Composable
fun neonTextStyle(
    color: Color,
    fontSize: TextUnit = 20.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    glowRadius: Float = 16f
): TextStyle {
    val theme = LocalAppTheme.current
    val basicMode = theme == AppTheme.BASIC
    val softGlowMode = theme == AppTheme.MOONLIT_PURPLE || theme == AppTheme.SAKURA_BLOOM
    val effectiveGlowRadius = if (softGlowMode) glowRadius * 0.6f else glowRadius

    return TextStyle(
        color = color,
        fontSize = fontSize,
        fontWeight = if (basicMode) FontWeight.Bold else fontWeight,
        fontFamily = FontFamily.SansSerif,
        shadow = if (basicMode) null else Shadow(
            color = color.copy(alpha = 0.85f),
            offset = Offset(0f, 0f),
            blurRadius = effectiveGlowRadius
        )
    )
}

/**
 * Interactive glowing liability payoff progress bar.
 * Renders high-tech glowing Neon Red line when liabilities are high,
 * smoothly shrinks as debt decreases, and transforms into glowing Neon Green when debt = 0.
 */
@Composable
fun GlowingPayoffProgressBar(
    currentLiability: Double,
    peakLiability: Double,
    currencySymbol: String = "₹",
    modifier: Modifier = Modifier
) {
    val isDebtFree = currentLiability <= 0.001
    val effectivePeak = if (peakLiability <= 0.0) currentLiability.coerceAtLeast(1.0) else peakLiability.coerceAtLeast(currentLiability)
    
    // Remaining debt ratio (1.0 = 100% unpaid, 0.0 = completely paid off)
    val remainingDebtRatio = if (isDebtFree) 0.0f else (currentLiability / effectivePeak).toFloat().coerceIn(0.01f, 1.0f)
    val paidOffPercent = ((1.0f - (if (isDebtFree) 0f else remainingDebtRatio)) * 100f).coerceIn(0f, 100f)

    val animatedWidthFraction by animateFloatAsState(
        targetValue = if (isDebtFree) 1.0f else remainingDebtRatio,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "progressWidth"
    )

    val activeColor = if (isDebtFree) NeonGreen else NeonRed
    val activeGlow = if (isDebtFree) NeonGreenGlow else NeonRedGlow

    // Subtle pulse for celebration or high-tech feel
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        glowColor = if (isDebtFree) NeonGreen else NeonRed.copy(alpha = 0.35f),
        glowIntensity = if (isDebtFree) 0.5f else 0.3f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val themeAccentEmoji = when (LocalAppTheme.current) {
                        AppTheme.GOLDEN_HIVE -> "\uD83D\uDC1D"
                        AppTheme.SAKURA_BLOOM, AppTheme.MOONLIT_PURPLE -> "\uD83C\uDF38"
                        else -> null
                    }
                    if (themeAccentEmoji != null) {
                        Text(text = themeAccentEmoji, fontSize = 15.sp)
                    } else {
                        Icon(
                            imageVector = if (isDebtFree) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = activeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = if (isDebtFree) "LIABILITY PAYOFF: 100% PAID" else "LIABILITY PAYOFF PROGRESS",
                        style = neonTextStyle(activeColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, glowRadius = 10f)
                    )
                }

                Text(
                    text = if (isDebtFree) "DEBT-FREE" else String.format(Locale.getDefault(), "%.1f%% Paid", paidOffPercent),
                    style = neonTextStyle(activeColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, glowRadius = 8f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // High-Tech glowing track and progress line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF101014))
                    .border(1.dp, CardGlassBorder, CircleShape)
            ) {
                // Animated neon fill bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedWidthFraction)
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                if (isDebtFree) {
                                    listOf(NeonGreen.copy(alpha = 0.7f), NeonGreen, NeonGreen)
                                } else {
                                    listOf(NeonYellow.copy(alpha = 0.8f), NeonRed, NeonRed)
                                }
                            )
                        )
                        .drawBehind {
                            // High intensity outer line glow
                            drawCircle(
                                color = activeGlow.copy(alpha = pulseAlpha * 0.9f),
                                radius = size.height * 1.8f,
                                center = Offset(size.width, size.height / 2)
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isDebtFree) {
                    Text(
                        text = "Total Financial Freedom • Zero Debt Balance",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = String.format(Locale.getDefault(), "Current Debt: %s%.2f", currencySymbol, currentLiability),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "Peak Debt: %s%.2f", currencySymbol, effectivePeak),
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

/**
 * Decorative floating cherry-blossom petals drifting down the screen on a
 * loop — purely visual (no touch handling, so it never blocks scrolling or
 * taps on whatever it's layered over). Used for the sakura-style themes.
 */
@Composable
fun FloatingPetalsOverlay(
    modifier: Modifier = Modifier,
    petalColor: Color = Color(0xFFFF9CC7),
    petalCount: Int = 14
) {
    val infiniteTransition = rememberInfiniteTransition(label = "petals")
    val petals = remember(petalCount) {
        List(petalCount) {
            PetalSpec(
                startXFraction = (it * 37 % 100) / 100f,
                driftAmplitude = 14f + (it % 5) * 6f,
                sizeDp = 6f + (it % 4) * 3f,
                durationMillis = 9000 + (it % 6) * 1400,
                delayFraction = (it * 53 % 100) / 100f,
                rotationSpeed = if (it % 2 == 0) 1f else -1f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        petals.forEach { petal ->
            val progress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = petal.durationMillis, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "petalFall"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val effectiveProgress = (progress + petal.delayFraction) % 1f
                val yPos = effectiveProgress * size.height
                val xDrift = kotlin.math.sin(effectiveProgress * 2 * Math.PI.toFloat()) * petal.driftAmplitude
                val xPos = petal.startXFraction * size.width + xDrift
                val fadeAlpha = when {
                    effectiveProgress < 0.08f -> effectiveProgress / 0.08f
                    effectiveProgress > 0.92f -> (1f - effectiveProgress) / 0.08f
                    else -> 1f
                }
                val rotation = effectiveProgress * 360f * petal.rotationSpeed

                rotate(degrees = rotation, pivot = Offset(xPos, yPos)) {
                    drawOval(
                        color = petalColor.copy(alpha = 0.35f * fadeAlpha),
                        topLeft = Offset(xPos - petal.sizeDp, yPos - petal.sizeDp / 2),
                        size = androidx.compose.ui.geometry.Size(petal.sizeDp * 2, petal.sizeDp)
                    )
                }
            }
        }
    }
}

private data class PetalSpec(
    val startXFraction: Float,
    val driftAmplitude: Float,
    val sizeDp: Float,
    val durationMillis: Int,
    val delayFraction: Float,
    val rotationSpeed: Float
)

/**
 * A themed divider — a plain line for most themes, or a line with a small
 * flower centered in it for the sakura-style themes.
 */
@Composable
fun ThemedDivider(modifier: Modifier = Modifier) {
    val theme = LocalAppTheme.current
    val showFlower = theme == AppTheme.SAKURA_BLOOM || theme == AppTheme.MOONLIT_PURPLE
    if (showFlower) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = CardGlassBorder, thickness = 1.dp)
            Text(text = "🌸", fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f), color = CardGlassBorder, thickness = 1.dp)
        }
    } else {
        HorizontalDivider(modifier = modifier, color = CardGlassBorder)
    }
}
