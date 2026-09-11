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
    // Outer Container: Exactly identical sizing and proportions to Hinata's card
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // 1. The Main Balance Card Box (Pitch-black midnight surface + Razor-sharp Chidori Cyan glow)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = KakashiCardGlow,
                    spotColor = KakashiCardGlow
                )
                .background(
                    color = KakashiSurface,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.3.dp,
                    color = KakashiCardBorder,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Title: Thematic "CURRENT BALANCE JUTSU" (Surplus completely removed!)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "CURRENT BALANCE JUTSU",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp,
                        color = KakashiBalanceBlue
                    )
                    Text(
                        text = "⚡",
                        fontSize = 12.sp
                    )
                }

                // Subtitle: "愛しいシュヘラ" in soft ice-cyan
                Text(
                    text = "写輪眼のカカシ",
                    fontSize = 11.sp,
                    color = Color(0xFF648DAF),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Big Electric Azure Blue Balance Number
                Text(
                    text = "₹ ${indianNumber(liveBalance)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KakashiBalanceBlue,
                    modifier = Modifier.fillMaxWidth(0.50f)
                )

                Text(
                    text = formulaText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF7E97B5),
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth(0.50f)
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))
                ThemedDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Income & Expenses Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Income Tile (Midnight dark navy surface with electric blue border)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF041221))
                            .border(1.dp, Color(0xFF0284C7), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF032644))
                                .border(1.dp, KakashiIncomeBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Income",
                                tint = KakashiIncomeBlue,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL INCOME",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF88A2C0)
                            )
                            Text(
                                text = "+₹${indianNumber(totalIncome)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = KakashiIncomeBlue
                            )
                        }
                    }

                    // Spacer so the left column takes exactly 50%
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // 2. KAKASHI ARTWORK: Spiky hair naturally extends OUTSIDE the card box without cropping
        Image(
            painter = painterResource(id = R.drawable.kakashi_corner),
            contentDescription = "Kakashi Hatake",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .layout { measurable, constraints ->
                    val targetWidth = 370.dp.roundToPx()
                    val targetHeight = 490.dp.roundToPx()
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
                            x = -targetWidth + 14.dp.roundToPx(),
                            y = (-110).dp.roundToPx() // Spiky hair extends gracefully above top edge
                        )
                    }
                }
        )

        // 3. TOTAL EXPENSES TILE: Rendered explicitly ON TOP of Kakashi's flak jacket
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth(0.50f)
                .padding(end = 20.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A050A)) // Solid dark Sharingan Crimson surface
                    .border(1.dp, Color(0xFFE11D48), RoundedCornerShape(16.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF330913))
                        .border(1.dp, KakashiSharinganRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Expense",
                        tint = KakashiSharinganRed,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Column {
                    Text(
                        text = "TOTAL EXPENSES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE08B9B)
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
