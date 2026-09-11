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
import java.text.DecimalFormat

@SuppressLint("DiscouragedApi")
@Composable
fun BumblebeeMainBalanceCard(
    liveBalance: Double,
    totalIncome: Double,
    totalExpenses: Double,
    formulaText: String = "Formula: Initial + Income - Expenses",
    isSurplus: Boolean = liveBalance >= 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bumblebeeResId = remember(context) {
        val resId = context.resources.getIdentifier("bumblebee_corner", "drawable", context.packageName)
        if (resId != 0) resId else R.drawable.rushu_fin_logo
    }

    // Format balance: Standard Indian system up to 10 Lakhs, then uses 'k' format after 10 Lakhs
    val formattedBalance = if (kotlin.math.abs(liveBalance) >= 1_000_000.0) {
        val thousands = liveBalance / 1_000.0
        val kFormat = DecimalFormat("##,##,##,##0.#")
        "${kFormat.format(thousands)}k"
    } else {
        indianNumber(liveBalance)
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // 1. Amber metallic pitch-black card background surface
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
        )

        // 2. BUMBLEBEE CAR ARTWORK (Rendered BEFORE buttons/texts so buttons and numbers are ON TOP)
        Image(
            painter = painterResource(id = bumblebeeResId),
            contentDescription = "Bumblebee Autobot",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .layout { measurable, constraints ->
                    val targetWidth = 350.dp.roundToPx()
                    val targetHeight = 440.dp.roundToPx()
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
                            x = -targetWidth + 30.dp.roundToPx(),
                            y = (-68).dp.roundToPx()
                        )
                    }
                }
        )

        // 3. FOREGROUND CONTENT: All texts, calculations, Total Income & Expenses (STRICTLY ON TOP)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "CURRENT BALANCE AUTOBOT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.1.sp,
                    color = BumblebeeGold
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BumblebeePillBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Text(
                        text = if (isSurplus) "SURPLUS" else "DEFICIT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSurplus) BumblebeeGold else BumblebeeFlameOrange,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Balance Amount (Single line extending across over the car)
            Text(
                text = "₹ $formattedBalance",
                fontSize = if (formattedBalance.length > 12) 28.sp else 34.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                softWrap = false,
                color = BumblebeeGold,
                modifier = Modifier.fillMaxWidth()
            )

            // High Contrast Formula Text (On Top)
            Text(
                text = formulaText,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFDE68A),
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth(0.60f)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))
            ThemedDivider()
            Spacer(modifier = Modifier.height(14.dp))

            // Income and Expenses Buttons Row (Both completely ON TOP of car artwork and smoke)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Income Button (On Top)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF221404))
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3D2405))
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
                            color = Color(0xFFFDE68A)
                        )
                        Text(
                            text = "+₹${indianNumber(totalIncome)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BumblebeeIncomeGold
                        )
                    }
                }

                // Total Expenses Button (On Top)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF271302))
                        .border(1.dp, Color(0xFFEA580C), RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF451A03))
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
}