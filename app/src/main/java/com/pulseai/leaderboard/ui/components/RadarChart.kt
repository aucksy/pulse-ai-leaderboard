package com.pulseai.leaderboard.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.ui.theme.AccentIndigo
import com.pulseai.leaderboard.ui.theme.EmeraldValue
import com.pulseai.leaderboard.ui.theme.SilverRank
import com.pulseai.leaderboard.ui.theme.SurfaceCardBorder
import com.pulseai.leaderboard.ui.theme.TextMuted
import com.pulseai.leaderboard.ui.theme.TextSecondary
import com.pulseai.leaderboard.ui.viewmodel.CategoryAverages
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarSpiderChart(
    model: AiModel,
    categoryAverages: CategoryAverages,
    modifier: Modifier = Modifier
) {
    var animProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(model.id) {
        animProgress = 1f
    }
    val animatedFactor by animateFloatAsState(
        targetValue = animProgress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "radarAnim"
    )

    // Normalize metrics to 0..100
    val modelMetrics = listOf(
        model.codingScore.toFloat(),
        model.reasoningScore.toFloat(),
        model.agenticScore.toFloat(),
        (model.speedTps / 2.0).toFloat().coerceIn(0f, 100f),
        (model.valueIndex * 3.0).toFloat().coerceIn(0f, 100f)
    )

    val avgMetrics = listOf(
        categoryAverages.avgCoding.toFloat(),
        categoryAverages.avgReasoning.toFloat(),
        categoryAverages.avgAgentic.toFloat(),
        (categoryAverages.avgSpeed / 2.0).toFloat().coerceIn(0f, 100f),
        ((categoryAverages.avgIntelligence / (categoryAverages.avgPrice + 0.1)) * 3.0).toFloat().coerceIn(0f, 100f)
    )

    val labels = listOf("Coding", "Reasoning", "Agentic", "Speed", "Value")

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.width / 2f - 20f
                val sides = 5
                val angleStep = (2 * PI / sides).toFloat()

                // Draw concentric pentagons (25%, 50%, 75%, 100%)
                val gridLevels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                gridLevels.forEach { level ->
                    val r = maxRadius * level
                    val gridPath = Path()
                    for (i in 0 until sides) {
                        val angle = -PI.toFloat() / 2f + i * angleStep
                        val x = center.x + r * cos(angle)
                        val y = center.y + r * sin(angle)
                        if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
                    }
                    gridPath.close()
                    drawPath(
                        path = gridPath,
                        color = SurfaceCardBorder,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Draw axis spokes and text labels
                for (i in 0 until sides) {
                    val angle = -PI.toFloat() / 2f + i * angleStep
                    val endX = center.x + maxRadius * cos(angle)
                    val endY = center.y + maxRadius * sin(angle)
                    drawLine(
                        color = SurfaceCardBorder.copy(alpha = 0.6f),
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Text labels
                    val textRadius = maxRadius + 14.dp.toPx()
                    val textX = center.x + textRadius * cos(angle)
                    val textY = center.y + textRadius * sin(angle)
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#94A3B8")
                        textSize = 9.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        labels[i],
                        textX,
                        textY + 3.dp.toPx(),
                        paint
                    )
                }

                // Draw Average polygon
                val avgPath = Path()
                for (i in 0 until sides) {
                    val angle = -PI.toFloat() / 2f + i * angleStep
                    val r = (avgMetrics[i] / 100f).coerceIn(0.1f, 1f) * maxRadius * animatedFactor
                    val x = center.x + r * cos(angle)
                    val y = center.y + r * sin(angle)
                    if (i == 0) avgPath.moveTo(x, y) else avgPath.lineTo(x, y)
                }
                avgPath.close()
                drawPath(path = avgPath, color = SilverRank.copy(alpha = 0.15f), style = Fill)
                drawPath(path = avgPath, color = SilverRank.copy(alpha = 0.5f), style = Stroke(width = 1.dp.toPx()))

                // Draw Model polygon
                val modelPath = Path()
                for (i in 0 until sides) {
                    val angle = -PI.toFloat() / 2f + i * angleStep
                    val r = (modelMetrics[i] / 100f).coerceIn(0.1f, 1f) * maxRadius * animatedFactor
                    val x = center.x + r * cos(angle)
                    val y = center.y + r * sin(angle)
                    if (i == 0) modelPath.moveTo(x, y) else modelPath.lineTo(x, y)
                }
                modelPath.close()
                drawPath(path = modelPath, color = AccentIndigo.copy(alpha = 0.35f), style = Fill)
                drawPath(path = modelPath, color = AccentIndigo, style = Stroke(width = 2.dp.toPx()))

                // Model vertex dots
                for (i in 0 until sides) {
                    val angle = -PI.toFloat() / 2f + i * angleStep
                    val r = (modelMetrics[i] / 100f).coerceIn(0.1f, 1f) * maxRadius * animatedFactor
                    val x = center.x + r * cos(angle)
                    val y = center.y + r * sin(angle)
                    drawCircle(color = EmeraldValue, radius = 3.dp.toPx(), center = Offset(x, y))
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .androidx.compose.foundation.background(AccentIndigo)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = model.name, fontSize = 11.sp, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .androidx.compose.foundation.background(SilverRank)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Category Average", fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}
