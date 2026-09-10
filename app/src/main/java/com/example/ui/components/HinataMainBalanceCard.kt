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

@Composable
fun HinataMainBalanceCard(
    liveBalance: Double,
    totalIncome: Double,
    totalExpenses: Double,
    formulaText: String = "Formula: Initial + Income - Expenses",
    isSurplus: Boolean = liveBalance >= 0,
    modifier: Modifier = Modifier
) {
    // Outer Box allows Hinata to extend above the top boundary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 28.dp)
    ) {
        // Main Card Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = HinataCardGlow,
                    spotColor = HinataCardGlow
                )
                .border(
                    width = 1.dp,
                    color = HinataCardBorder,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HinataSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row (Constrained to 58% width to guarantee ZERO overlap)
                Row(
                    modifier = Modifier.fillMaxWidth(0.58f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "MAIN LIVE BALANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = 0.95f)
                    )

                    // Surplus / Deficit Pill Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSurplus) HinataPillBg else Color(0x804C0519))
                            .border(
                                1.dp,
                                if (isSurplus) HinataPillBorder else Color(0x80FB7185),
                                CircleShape
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isSurplus) "SURPLUS" else "DEFICIT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSurplus) HinataPurpleLight else Color(0xFFFECDD3)
                        )
                    }
                }

                Text(
                    text = "現在の残高",
                    fontSize = 10.sp,
                    color = HinataPurpleMuted.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Big Live Balance Number
                Text(
                    text = "₹ %.2f".format(liveBalance),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonMintGreen,
                    modifier = Modifier.fillMaxWidth(0.58f)
                )

                Text(
                    text = formulaText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = HinataPurpleLight.copy(alpha = 0.65f),
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth(0.58f)
                        .padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))
                Divider(color = Color(0x33A855F7), thickness = 1.dp)
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
                            .background(Color(0x551E0638))
                            .border(1.dp, Color(0x26A855F7), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x3310B981))
                                .border(1.dp, Color(0x6610B981), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Income",
                                tint = EmeraldIncome,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL INCOME",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HinataPurpleLight.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "+₹ %.2f".format(totalIncome),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldIncome
                            )
                        }
                    }

                    // Total Expenses Tile
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x551E0638))
                            .border(1.dp, Color(0x26A855F7), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x33F43F5E))
                                .border(1.dp, Color(0x66F43F5E), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Expense",
                                tint = RoseDeficit,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL EXPENSES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HinataPurpleLight.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "-₹ %.2f".format(totalExpenses),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoseDeficit
                            )
                        }
                    }
                }
            }
        }

        // Hinata Corner Artwork: Aligned to TopEnd, extending slightly outside
        Image(
            painter = painterResource(id = R.drawable.hinata_corner),
            contentDescription = "Hinata Hyuga",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 10.dp, y = (-26).dp)
                .width(185.dp)
        )
    }
}
