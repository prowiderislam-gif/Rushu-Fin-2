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
fun KakashiMainBalanceCard(
    liveBalance: Double,
    totalIncome: Double,
    totalExpenses: Double,
    formulaText: String = "Formula: Initial + Income - Expenses",
    isSurplus: Boolean = liveBalance >= 0,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Main Balance Card Container with Electric Cyan Border & Glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = KakashiCardGlow,
                    spotColor = KakashiCardGlow
                )
                .background(
                    color = KakashiSurface,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.2.dp,
                    color = KakashiCardBorder,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // 1. KAKASHI ARTWORK: Placed behind the "Total Expenses" card, on top of card surface
            // Using layout(0, 0) reports ZERO height to the parent Box so there is ZERO extra bottom gap!
            Image(
                painter = painterResource(id = R.drawable.kakashi_corner),
                contentDescription = "Kakashi Hatake",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .layout { measurable, constraints ->
                        val targetWidth = 390.dp.roundToPx()
                        val targetHeight = 520.dp.roundToPx()
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
                                y = (-118).dp.roundToPx() // Head and silver hair align right under TIER 2 button
                            )
                        }
                    }
            )

            // 2. Card Content: Constrained to 48% width so text never collides with Kakashi
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(0.48f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CURRENT BALANCE JUTSU",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = KakashiElectricCyan
                    )

                    // Surplus / Deficit Pill Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSurplus) KakashiPillBg else Color(0x66450A0A))
                            .border(
                                1.dp,
                                if (isSurplus) KakashiPillBorder else Color(0x80FF3366),
                                CircleShape
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isSurplus) "SURPLUS" else "DEFICIT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSurplus) KakashiElectricCyan else KakashiSharinganRed
                        )
                    }
                }

                // Japanese Subtitle for Kakashi: "写輪眼のカカシ" (Kakashi of the Sharingan)
                Text(
                    text = "写輪眼のカカシ",
                    fontSize = 10.sp,
                    color = KakashiIceBlue.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Big Chidori Cyan Live Balance Number
                Text(
                    text = "₹ ${indianNumber(liveBalance)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KakashiElectricCyan,
                    modifier = Modifier.fillMaxWidth(0.48f)
                )

                Text(
                    text = formulaText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = KakashiIceBlue.copy(alpha = 0.65f),
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth(0.48f)
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))
                ThemedDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Income & Expenses Row — Rendered ON TOP of Kakashi's flak jacket
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Income Tile
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF091A2E)) // Solid midnight navy
                            .border(1.dp, Color(0x6600D2FF), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(KakashiElectricCyan.copy(alpha = 0.2f))
                                .border(1.dp, KakashiElectricCyan.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Income",
                                tint = KakashiElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL INCOME",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = KakashiIceBlue.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "+₹${indianNumber(totalIncome)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = KakashiElectricCyan
                            )
                        }
                    }

                    // Total Expenses Tile: Sits cleanly ON TOP of Kakashi's jacket
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF180A12)) // Solid sharingan dark red
                            .border(1.dp, Color(0x80FF3366), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(KakashiSharinganRed.copy(alpha = 0.2f))
                                .border(1.dp, KakashiSharinganRed.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Expense",
                                tint = KakashiSharinganRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL EXPENSES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = KakashiSharinganRed.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "-₹${indianNumber(totalExpenses)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = KakashiSharinganRed
                            )
                        }
                    }
                }
            }
        }
    }
}
