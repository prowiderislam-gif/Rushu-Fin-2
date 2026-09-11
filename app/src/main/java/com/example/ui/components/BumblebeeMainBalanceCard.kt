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
fun BumblebeeMainBalanceCard(
    liveBalance: Double,
    totalIncome: Double,
    totalExpenses: Double,
    formulaText: String = "Formula: Initial + Income - Expenses",
    isSurplus: Boolean = liveBalance >= 0,
    modifier: Modifier = Modifier
) {
    // Outer Container: Exactly identical sizing and proportions to Hinata & Kakashi cards
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // 1. Main Balance Card Box (Pitch-black obsidian surface + Amber Gold cutting-edge border)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = BumblebeeCardGlow,
                    spotColor = BumblebeeCardGlow
                )
                .background(
                    color = BumblebeeSurface,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.3.dp,
                    color = BumblebeeCardBorder,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Title: Clean "CURRENT BALANCE AUTOBOT" with no Japanese text & no surplus pill
                Text(
                    text = "CURRENT BALANCE AUTOBOT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = BumblebeeGold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Big Metallic Gold Balance Number
                Text(
                    text = "₹ ${indianNumber(liveBalance)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BumblebeeGold,
                    modifier = Modifier.fillMaxWidth(0.50f)
                )

                Text(
                    text = formulaText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFB45309),
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
                    // Total Income Tile (Midnight amber surface with golden border)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF140B02))
                            .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF331904))
                                .border(1.dp, BumblebeeIncomeGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Income",
                                tint = BumblebeeIncomeGold,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL INCOME",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFCD34D)
                            )
                            Text(
                                text = "+₹${indianNumber(totalIncome)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BumblebeeIncomeGold
                            )
                        }
                    }

                    // Spacer so the left column takes exactly 50%
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // 2. BUMBLEBEE CAR ARTWORK: Roof, hood, and fiery smoke plume flow naturally OUTSIDE the card box
        Image(
            painter = painterResource(id = R.drawable.bumblebee_corner),
            contentDescription = "Bumblebee Muscle Car",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .layout { measurable, constraints ->
                    val targetWidth = 380.dp.roundToPx()
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
                            x = -targetWidth + 18.dp.roundToPx(),
                            y = (-105).dp.roundToPx() // Car roof and smoke plume rise above the top border
                        )
                    }
                }
        )

        // 3. TOTAL EXPENSES TILE: Rendered explicitly ON TOP of the smoke / bumper
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
                    .background(Color(0xFF1E0A03)) // Solid dark flame surface
                    .border(1.dp, Color(0xFFEA580C), RoundedCornerShape(16.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3F1707))
                        .border(1.dp, BumblebeeFlameOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Expense",
                        tint = BumblebeeFlameOrange,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Column {
                    Text(
                        text = "TOTAL EXPENSES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFDBA74)
                    )
                    Text(
                        text = "-₹${indianNumber(totalExpenses)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BumblebeeFlameOrange
                    )
                }
            }
        }
    }
}