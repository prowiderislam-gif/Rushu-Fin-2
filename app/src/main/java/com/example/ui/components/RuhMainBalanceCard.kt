package com.example.ui.components

import android.annotation.SuppressLint
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.util.indianNumber

@SuppressLint("DiscouragedApi")
@Composable
fun RuhMainBalanceCard(
    liveBalance: Double,
    totalIncome: Double,
    totalExpenses: Double,
    formulaText: String = "Formula: Initial + Income - Expenses",
    isSurplus: Boolean = liveBalance >= 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Safe lookup: prevents any compile/build error even if ruh_corner.png isn't committed yet
    val ruhResId = remember(context) {
        val resId = context.resources.getIdentifier("ruh_corner", "drawable", context.packageName)
        if (resId != 0) resId else R.drawable.rushu_fin_logo
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // 1. Pitch-black obsidian glass surface with vibrant Blood-Red border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 22.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = RuhCardGlow,
                    spotColor = RuhCardGlow
                )
                .background(
                    color = RuhSurface,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.3.dp,
                    color = RuhCardBorder,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row: Clean "MAIN LIVE BALANCE" + exact "Ꮢᴜʜ᭓Ꮢɪᴅɛʀ" badge in place of Surplus!
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(0.60f)
                ) {
                    Text(
                        text = "MAIN LIVE BALANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.1.sp,
                        color = Color.White
                    )

                    // Stylized Ꮢᴜʜ᭓Ꮢɪᴅɛʀ badge in place of Surplus
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF260408),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626))
                    ) {
                        Text(
                            text = "Ꮢᴜʜ᭓Ꮢɪᴅɛʀ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RuhBloodRed,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                // Custom Japanese text: 彼女を手に入れたい。
                Text(
                    text = "彼女を手に入れたい。",
                    fontSize = 11.sp,
                    color = Color(0xFFE57373),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Big Neon Blood Red Balance Number
                Text(
                    text = "₹ ${indianNumber(liveBalance)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RuhBloodRed,
                    modifier = Modifier.fillMaxWidth(0.50f)
                )

                Text(
                    text = formulaText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFB91C1C),
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
                    // Total Income Tile
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E0407))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B070E))
                                .border(1.dp, RuhIncomeRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Income",
                                tint = RuhIncomeRed,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TOTAL INCOME",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFCA5A5)
                            )
                            Text(
                                text = "+₹${indianNumber(totalIncome)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RuhIncomeRed
                            )
                        }
                    }

                    // Spacer so left column takes exactly 50%
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // 2. RUH / ITACHI ARTWORK: Crow wings, high collar & smoke extend freely outside
        Image(
            painter = painterResource(id = ruhResId),
            contentDescription = "Itachi Crows",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .layout { measurable, constraints ->
                    val targetWidth = 380.dp.roundToPx()
                    val targetHeight = 500.dp.roundToPx()
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
                            y = (-110).dp.roundToPx()
                        )
                    }
                }
        )

        // 3. TOTAL EXPENSES TILE: Sits on top of the lower crows and smoke
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
                    .background(Color(0xFF260408))
                    .border(1.dp, Color(0xFFDC2626), RoundedCornerShape(16.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF450A0A))
                        .border(1.dp, RuhExpenseRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Expense",
                        tint = RuhExpenseRed,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Column {
                    Text(
                        text = "TOTAL EXPENSES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF87171)
                    )
                    Text(
                        text = "-₹${indianNumber(totalExpenses)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RuhExpenseRed
                    )
                }
            }
        }
    }
}