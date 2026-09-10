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
    val isHinataTheme = LocalAppTheme.current == AppTheme.HINATA

    // When Hinata theme is active, add top space for her hair and head to overflow naturally
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = if (isHinataTheme) 34.dp else 0.dp)
    ) {
        // Main Card Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isHinataTheme) 16.dp else 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = if (isHinataTheme) HinataCardGlow else Color.Transparent,
                    spotColor = if (isHinataTheme) HinataCardGlow else Color.Transparent
                )
                .border(
                    width = 1.dp,
                    color = if (isHinataTheme) HinataCardBorder else CardGlassBorder,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isHinataTheme) HinataSurface else CardGlass
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row (Constrained to 52% width in Hinata theme so text never touches her)
                Row(
                    modifier = if (isHinataTheme) Modifier.fillMaxWidth(0.52f) else Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MAIN LIVE BALANCE",
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
                                    (if (isSurplus) NeonGreen else NeonRed).copy(alpha = 0.15f)
                                }
                            )
                            .border(
                                1.dp,
                                if (isHinataTheme) {
                                    if (isSurplus) HinataPillBorder else Color(0x80FB7185)
                                } else {
                                    (if (isSurplus) NeonGreen else NeonRed).copy(alpha = 0.4f)
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
                        text = "現在の残高",
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
                    modifier = if (isHinataTheme) Modifier.fillMaxWidth(0.52f) else Modifier.fillMaxWidth()
                )

                Text(
                    text = formulaText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (isHinataTheme) HinataPurpleLight.copy(alpha = 0.65f) else TextMuted,
                    maxLines = 2,
                    modifier = (if (isHinataTheme) Modifier.fillMaxWidth(0.52f) else Modifier.fillMaxWidth())
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
                    // Total Income Tile
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isHinataTheme) Color(0x551E0638) else Color(0xFF0E0E12))
                            .border(
                                1.dp,
                                if (isHinataTheme) Color(0x33A855F7) else CardGlassBorder,
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
                                .background(NeonGreen.copy(alpha = 0.15f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.4f), CircleShape),
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

                    // Total Expenses Tile
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isHinataTheme) Color(0x551E0638) else Color(0xFF0E0E12))
                            .border(
                                1.dp,
                                if (isHinataTheme) Color(0x33A855F7) else CardGlassBorder,
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
                                .background(NeonRed.copy(alpha = 0.15f))
                                .border(1.dp, NeonRed.copy(alpha = 0.4f), CircleShape),
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

        // Hinata Artwork: ONLY rendered in Hinata Theme, positioned big & majestic matching reference image
        if (isHinataTheme) {
            Image(
                painter = painterResource(id = R.drawable.hinata_corner),
                contentDescription = "Hinata Hyuga",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-32).dp)
                    .width(245.dp)
                    .height(275.dp)
            )
        }
    }
}
