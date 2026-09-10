package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.random.Random

data class LightningSpark(
    val startXRatio: Float,
    val startYRatio: Float,
    val lengthRatio: Float,
    val angle: Float,
    val phaseOffset: Float
)

@Composable
fun LightningOverlay(
    modifier: Modifier = Modifier,
    lightningColor: Color = Color(0xFF00D2FF),
    sparkCount: Int = 12
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lightning_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val flicker by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flicker"
    )

    val sparks = remember {
        List(sparkCount) { i ->
            LightningSpark(
                startXRatio = Random.nextFloat(),
                startYRatio = Random.nextFloat(),
                lengthRatio = 0.04f + Random.nextFloat() * 0.08f,
                angle = Random.nextFloat() * 360f,
                phaseOffset = Random.nextFloat()
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        sparks.forEachIndexed { index, spark ->
            val sparkTime = (flicker + spark.phaseOffset) % 1f
            // Soft intermittent electric discharge
            val alpha = if (sparkTime in 0.15f..0.35f || sparkTime in 0.7f..0.85f) {
                ((pulse * 0.45f) + 0.15f).coerceIn(0.1f, 0.65f)
            } else {
                0.05f
            }

            val startX = spark.startXRatio * width
            val startY = spark.startYRatio * height
            val segLen = spark.lengthRatio * height

            val path = Path()
            path.moveTo(startX, startY)

            // Draw a zig-zag lightning arc
            var currX = startX
            var currY = startY
            val segments = 4
            for (s in 1..segments) {
                val stepY = (segLen / segments)
                val jitterX = ((s % 2 * 2 - 1) * 14f) * (0.8f + pulse * 0.4f)
                currX += jitterX
                currY += stepY
                path.lineTo(currX, currY)
            }

            // Glow Stroke
            drawPath(
                path = path,
                color = lightningColor.copy(alpha = alpha * 0.35f),
                style = Stroke(width = 4.5f)
            )
            // Core Bright Stroke
            drawPath(
                path = path,
                color = Color.White.copy(alpha = alpha),
                style = Stroke(width = 1.5f)
            )
        }
    }
}