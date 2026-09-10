package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.util.indianNumber

@Composable
fun HinataMainBalanceCard(
    liveBalance: Double,
    totalIncome: Double,
    totalExpenses: Double,
    formulaText: String = "Formula: Initial + Income - Expenses",
    isSurplus: Boolean = liveBalance >= 0,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val isHinataTheme = theme == AppTheme.HINATA
    val isDefaultTheme = theme == AppTheme.DEFAULT

    // Outer Container
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Main Balance Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isHinataTheme) 18.dp else 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = if (isHinataTheme) HinataCardGlow else (if (isSurplus) NeonGreenGlow else NeonRedGlow),
                    spotColor = if (isHinataTheme) HinataCardGlow else (if (isSurplus) NeonGreenGlow else NeonRedGlow)
                )
                .background(
                    color = when {
                        isHinataTheme -> HinataSurface
                        isDefaultTheme -> Color(0xFF041910) // Glowing Emerald Green Glassmorphism for Default
                        else -> CardGlass
                    },
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = if (isDefaultTheme) 1.5.dp else 1.dp,
                    color = when {
                        isHinataTheme -> HinataCardBorder
                        isDefaultTheme -> if (isSurplus) NeonGreen.copy(alpha = 0.7f) else NeonRed.copy(alpha = 0.7f)
                        else -> CardGlassBorder
                    },
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // 1. GIANT HINATA: Placed behind the Bottom "Total Expenses" card, but on top of card canvas
            // Using layout(0, 0) reports ZERO height to the parent Box so there is ZERO extra bottom gap!
            if (isHinataTheme) {
                Image(
                    painter = painterResource(id = R.drawable.hinata_corner),
                    contentDescription = "Hinata Hyuga",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .layout { measurable, constraints ->
                            val targetWidth = 385.dp.roundToPx()
                            val targetHeight = 515.dp.roundToPx()
                            val placeable = measurable.measure(
                                constraints.copy(
                                    minWidth = targetWidth,
                                    maxWidth = targetWidth,
                                    minHeight = targetHeight,
                                    maxHeight = targetHeight
                                )
                            )
                            layout(0, 0) {
                                placeable.placeRelative(
                                    x = -targetWidth + 24.dp.roundToPx(),
                                    y = (-118).dp.roundToPx() // Reaches right up under the TIER 2 button
                                )
                            }
                        }
                )
            }

            // 2. Card Content (Rendered in front of Hinata so "Total Expenses" sits ON TOP of her body)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row (Constrained width in Hinata theme so text stays cleanly on the left)
                Row(
                    modifier = if (isHinataTheme) Modifier.fillMaxWidth(0.48f) else Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CURRENT BALANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isHinataTheme) HinataPurpleLight else TextSecondary
                    )

                    // Surplus / Deficit Pill Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isHinataTheme) {
                                    if (isSurplus) HinataPillBg else Color(0x804C0519)
                                } else {
                                    (if (isSurplus) NeonGreen else NeonRed).copy(alpha = 0.2f)
                                }
                            )
                            .border(
                                1.dp,
                                if (isHinataTheme) {
                                    if (isSurplus) HinataPillBorder else Color(0x80FB7185)
                                } else {
                                    (if (isSurplus) NeonGreen else NeonRed).copy(alpha = 0.6f)
                                },
                                CircleShape
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isSurplus) "SURPLUS" else "DEFICIT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHinataTheme) {
                                if (isSurplus) HinataPurpleLight else Color(0xFFFECDD3)
                            } else {
                                if (isSurplus) NeonGreen else NeonRed
                            }
                        )
                    }
                }

                if (isHinataTheme) {
                    Text(
                        text = "愛しいシュヘラ",
                        fontSize = 10.sp,
                        color = HinataPurpleMuted.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Live Balance Number
                Text(
                    text = "₹ ${indianNumber(liveBalance)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isHinataTheme) NeonMintGreen else (if (isSurplus) NeonGreen else NeonRed),
                    modifier = if (isHinataTheme) Modifier.fillMaxWidth(0.48f) else Modifier.fillMaxWidth()
                )

                Text(
                    text = formulaText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (isHinataTheme) HinataPurpleLight.copy(alpha = 0.65f) else TextMuted,
                    maxLines = 2,
                    modifier = (if (isHinataTheme) Modifier.fillMaxWidth(0.48f) else Modifier.fillMaxWidth())
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))
                ThemedDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Income & Expenses Row — Rendered ON TOP of Hinata's jacket!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Income Tile
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isHinataTheme -> Color(0xFF1B0733) // Solid violet so it sits crisply on top
                                    isDefaultTheme -> Color(0xFF092418)
                                    else -> Color(0xFF131C2E)
                                }
                            )
                            .border(
                                1.dp,
                                when {
                                    isHinataTheme -> Color(0x66A855F7)
                                    isDefaultTheme -> NeonGreen.copy(alpha = 0.4f)
                                    else -> CardGlassBorder
                                },
                                RoundedCornerShape(16.dp)
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NeonGreen.copy(alpha = 0.2f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Income",
                                tint = NeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL INCOME",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isHinataTheme) HinataPurpleLight.copy(alpha = 0.6f) else TextMuted
                            )
                            Text(
                                text = "+₹${indianNumber(totalIncome)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        }
                    }

                    // Total Expenses Tile (Sits ON TOP of Hinata)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isHinataTheme -> Color(0xFF1B0733) // Solid violet so her jacket flows cleanly behind
                                    isDefaultTheme -> Color(0xFF260A13)
                                    else -> Color(0xFF131C2E)
                                }
                            )
                            .border(
                                1.dp,
                                when {
                                    isHinataTheme -> Color(0x66A855F7)
                                    isDefaultTheme -> NeonRed.copy(alpha = 0.4f)
                                    else -> CardGlassBorder
                                },
                                RoundedCornerShape(16.dp)
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NeonRed.copy(alpha = 0.2f))
                                .border(1.dp, NeonRed.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Expense",
                                tint = NeonRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL EXPENSES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isHinataTheme) HinataPurpleLight.copy(alpha = 0.6f) else TextMuted
                            )
                            Text(
                                text = "-₹${indianNumber(totalExpenses)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonRed
                            )
                        }
                    }
                }
            }
        }
    }
}
