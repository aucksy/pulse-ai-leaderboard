package com.pulseai.leaderboard.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.ui.components.PillBadge
import com.pulseai.leaderboard.ui.components.ProviderBadge
import com.pulseai.leaderboard.ui.theme.AccentIndigo
import com.pulseai.leaderboard.ui.theme.AmberCrown
import com.pulseai.leaderboard.ui.theme.BgObsidian
import com.pulseai.leaderboard.ui.theme.EmeraldValue
import com.pulseai.leaderboard.ui.theme.SurfaceCard
import com.pulseai.leaderboard.ui.theme.SurfaceCardBorder
import com.pulseai.leaderboard.ui.theme.SurfaceCardElevated
import com.pulseai.leaderboard.ui.theme.TextMuted
import com.pulseai.leaderboard.ui.theme.TextPrimary
import com.pulseai.leaderboard.ui.theme.TextSecondary
import com.pulseai.leaderboard.ui.viewmodel.LeaderboardViewModel
import java.util.Locale
import kotlin.math.sqrt

@Composable
fun FrontierScreen(
    viewModel: LeaderboardViewModel,
    onModelClick: (AiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val models = uiState.allModels

    var selectedModel by remember(models) {
        mutableStateOf(models.firstOrNull())
    }

    // Compute Pareto frontier: model is pareto-optimal if no other model has higher score and lower/equal price
    val paretoModels = remember(models) {
        val sortedByPrice = models.sortedBy { it.promptPricePerMillion }
        val frontier = mutableListOf<AiModel>()
        var maxScoreSeen = -1.0
        for (m in sortedByPrice) {
            if (m.intelligenceScore > maxScoreSeen) {
                frontier.add(m)
                maxScoreSeen = m.intelligenceScore
            }
        }
        frontier
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgObsidian)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Text(
            text = "Value Frontier Curve",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Intelligence Score vs. Cost ($/1M tokens) with Pareto boundary",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Legend row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentIndigo))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Open-Weights", fontSize = 11.sp, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldValue))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Proprietary", fontSize = 11.sp, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).border(1.dp, AmberCrown, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Pareto Frontier", fontSize = 11.sp, color = AmberCrown)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Interactive Canvas Scatter Plot
        val maxPrice = 16.0 // $/1M tokens max on x-axis
        val minScore = 70.0
        val maxScore = 100.0

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceCard)
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(models) {
                        detectTapGestures { tapOffset ->
                            val padLeft = 40f
                            val padBottom = 30f
                            val plotWidth = size.width - padLeft - 20f
                            val plotHeight = size.height - padBottom - 20f

                            var closestModel: AiModel? = null
                            var minDistance = Float.MAX_VALUE

                            models.forEach { m ->
                                val priceClamped = m.promptPricePerMillion.coerceIn(0.0, maxPrice)
                                val x = padLeft + (priceClamped / maxPrice * plotWidth).toFloat()
                                val scoreNorm = ((m.intelligenceScore - minScore) / (maxScore - minScore)).coerceIn(0.0, 1.0)
                                val y = (size.height - padBottom) - (scoreNorm * plotHeight).toFloat()

                                val dx = tapOffset.x - x
                                val dy = tapOffset.y - y
                                val dist = sqrt(dx * dx + dy * dy)
                                if (dist < 40f && dist < minDistance) {
                                    minDistance = dist
                                    closestModel = m
                                }
                            }

                            if (closestModel != null) {
                                selectedModel = closestModel
                            }
                        }
                    }
            ) {
                val padLeft = 40f
                val padBottom = 30f
                val plotWidth = size.width - padLeft - 20f
                val plotHeight = size.height - padBottom - 20f

                // Draw Horizontal Grid lines (Score 70, 80, 90, 100)
                val scoreTicks = listOf(70, 80, 90, 100)
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#64748B")
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.RIGHT
                    isAntiAlias = true
                }

                scoreTicks.forEach { score ->
                    val norm = (score - minScore) / (maxScore - minScore)
                    val y = (size.height - padBottom) - (norm * plotHeight).toFloat()

                    drawLine(
                        color = SurfaceCardBorder.copy(alpha = 0.5f),
                        start = Offset(padLeft, y),
                        end = Offset(size.width - 20f, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawContext.canvas.nativeCanvas.drawText("$score", padLeft - 8f, y + 4.dp.toPx(), textPaint)
                }

                // Draw Vertical Price Grid lines ($0, $5, $10, $15)
                val priceTicks = listOf(0, 5, 10, 15)
                val xTextPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#64748B")
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                priceTicks.forEach { price ->
                    val norm = price / maxPrice
                    val x = padLeft + (norm * plotWidth).toFloat()

                    drawLine(
                        color = SurfaceCardBorder.copy(alpha = 0.5f),
                        start = Offset(x, 20f),
                        end = Offset(x, size.height - padBottom),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawContext.canvas.nativeCanvas.drawText("$$price", x, size.height - 8f, xTextPaint)
                }

                // Draw Pareto Frontier Curve
                if (paretoModels.size >= 2) {
                    val frontierPath = Path()
                    paretoModels.forEachIndexed { i, m ->
                        val priceClamped = m.promptPricePerMillion.coerceIn(0.0, maxPrice)
                        val x = padLeft + (priceClamped / maxPrice * plotWidth).toFloat()
                        val scoreNorm = ((m.intelligenceScore - minScore) / (maxScore - minScore)).coerceIn(0.0, 1.0)
                        val y = (size.height - padBottom) - (scoreNorm * plotHeight).toFloat()

                        if (i == 0) frontierPath.moveTo(x, y) else frontierPath.lineTo(x, y)
                    }
                    drawPath(
                        path = frontierPath,
                        color = AmberCrown.copy(alpha = 0.6f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Plot Model Scatter points
                models.forEach { m ->
                    val priceClamped = m.promptPricePerMillion.coerceIn(0.0, maxPrice)
                    val x = padLeft + (priceClamped / maxPrice * plotWidth).toFloat()
                    val scoreNorm = ((m.intelligenceScore - minScore) / (maxScore - minScore)).coerceIn(0.0, 1.0)
                    val y = (size.height - padBottom) - (scoreNorm * plotHeight).toFloat()

                    val isSelected = m.id == selectedModel?.id
                    val isPareto = paretoModels.any { it.id == m.id }
                    val dotColor = if (m.isOpenWeight) AccentIndigo else EmeraldValue

                    // Outer halo if selected or pareto
                    if (isSelected) {
                        drawCircle(
                            color = dotColor.copy(alpha = 0.35f),
                            radius = 12.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }

                    if (isPareto) {
                        drawCircle(
                            color = AmberCrown,
                            radius = 6.5.dp.toPx(),
                            center = Offset(x, y),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }

                    // Main Dot
                    drawCircle(
                        color = dotColor,
                        radius = if (isSelected) 5.dp.toPx() else 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Model Card
        if (selectedModel != null) {
            val sm = selectedModel!!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceCardElevated)
                    .border(1.dp, AccentIndigo.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        ProviderBadge(provider = sm.provider)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = sm.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${sm.formattedPromptPrice} • Score: ${String.format(Locale.US, "%.1f", sm.intelligenceScore)} • ${sm.formattedSpeed}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = { onModelClick(sm) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
