package com.pulseai.leaderboard.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.ui.components.ProviderBadge
import com.pulseai.leaderboard.ui.theme.AccentIndigo
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

@Composable
fun CompareScreen(
    viewModel: LeaderboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val modelA = uiState.compareModelA ?: uiState.filteredModels.firstOrNull()
    val modelB = uiState.compareModelB ?: uiState.filteredModels.getOrNull(1)

    var menuAExpanded by remember { mutableStateOf(false) }
    var menuBExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgObsidian)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .padding(bottom = 80.dp)
    ) {
        // Screen Header
        Text(
            text = "Arena Comparator",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Side-by-side head-to-head architectural showdown",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Model Selector Row with Swap Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Model A Selector
            Box(modifier = Modifier.weight(1f)) {
                ModelPickerCard(
                    model = modelA,
                    title = "MODEL A",
                    onClick = { menuAExpanded = true }
                )
                DropdownMenu(
                    expanded = menuAExpanded,
                    onDismissRequest = { menuAExpanded = false },
                    modifier = Modifier.background(SurfaceCardElevated)
                ) {
                    uiState.allModels.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m.name, color = TextPrimary, fontSize = 12.sp) },
                            onClick = {
                                viewModel.selectCompareModelA(m)
                                menuAExpanded = false
                            }
                        )
                    }
                }
            }

            // Swap Button
            IconButton(
                onClick = { viewModel.swapCompareModels() },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .border(1.dp, AccentIndigo.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swap",
                    tint = AccentIndigo,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Model B Selector
            Box(modifier = Modifier.weight(1f)) {
                ModelPickerCard(
                    model = modelB,
                    title = "MODEL B",
                    onClick = { menuBExpanded = true }
                )
                DropdownMenu(
                    expanded = menuBExpanded,
                    onDismissRequest = { menuBExpanded = false },
                    modifier = Modifier.background(SurfaceCardElevated)
                ) {
                    uiState.allModels.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m.name, color = TextPrimary, fontSize = 12.sp) },
                            onClick = {
                                viewModel.selectCompareModelB(m)
                                menuBExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (modelA != null && modelB != null) {
            // Metrics Battle Section
            ComparisonMetricDualBar(
                metricName = "OVERALL INTELLIGENCE",
                valueA = modelA.intelligenceScore,
                valueB = modelB.intelligenceScore,
                formattedA = String.format(Locale.US, "%.1f", modelA.intelligenceScore),
                formattedB = String.format(Locale.US, "%.1f", modelB.intelligenceScore),
                higherIsBetter = true,
                maxScale = 100.0
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComparisonMetricDualBar(
                metricName = "CODING KINGS BENCHMARK",
                valueA = modelA.codingScore,
                valueB = modelB.codingScore,
                formattedA = String.format(Locale.US, "%.1f", modelA.codingScore),
                formattedB = String.format(Locale.US, "%.1f", modelB.codingScore),
                higherIsBetter = true,
                maxScale = 100.0
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComparisonMetricDualBar(
                metricName = "REASONING & SYSTEM-2",
                valueA = modelA.reasoningScore,
                valueB = modelB.reasoningScore,
                formattedA = String.format(Locale.US, "%.1f", modelA.reasoningScore),
                formattedB = String.format(Locale.US, "%.1f", modelB.reasoningScore),
                higherIsBetter = true,
                maxScale = 100.0
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComparisonMetricDualBar(
                metricName = "AGENTIC TOOL MASTERY",
                valueA = modelA.agenticScore,
                valueB = modelB.agenticScore,
                formattedA = String.format(Locale.US, "%.1f", modelA.agenticScore),
                formattedB = String.format(Locale.US, "%.1f", modelB.agenticScore),
                higherIsBetter = true,
                maxScale = 100.0
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComparisonMetricDualBar(
                metricName = "SPEED (TOKENS / SEC)",
                valueA = modelA.speedTps,
                valueB = modelB.speedTps,
                formattedA = "${modelA.speedTps.toInt()} tps",
                formattedB = "${modelB.speedTps.toInt()} tps",
                higherIsBetter = true,
                maxScale = 220.0
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComparisonMetricDualBar(
                metricName = "CONTEXT WINDOW (TOKENS)",
                valueA = modelA.contextLength.toDouble(),
                valueB = modelB.contextLength.toDouble(),
                formattedA = modelA.formattedContext,
                formattedB = modelB.formattedContext,
                higherIsBetter = true,
                maxScale = 2_000_000.0
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComparisonMetricDualBar(
                metricName = "PROMPT PRICING ($ / 1M TOKENS)",
                valueA = modelA.promptPricePerMillion,
                valueB = modelB.promptPricePerMillion,
                formattedA = modelA.formattedPromptPrice,
                formattedB = modelB.formattedPromptPrice,
                higherIsBetter = false, // cheaper is better
                maxScale = 20.0
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComparisonMetricDualBar(
                metricName = "COMPLETION PRICING ($ / 1M TOKENS)",
                valueA = modelA.completionPricePerMillion,
                valueB = modelB.completionPricePerMillion,
                formattedA = modelA.formattedCompletionPrice,
                formattedB = modelB.formattedCompletionPrice,
                higherIsBetter = false, // cheaper is better
                maxScale = 80.0
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComparisonMetricDualBar(
                metricName = "VALUE INDEX (PERF / DOLLAR)",
                valueA = modelA.valueIndex,
                valueB = modelB.valueIndex,
                formattedA = String.format(Locale.US, "%.1f", modelA.valueIndex),
                formattedB = String.format(Locale.US, "%.1f", modelB.valueIndex),
                higherIsBetter = true,
                maxScale = 150.0
            )
        }
    }
}

@Composable
private fun ModelPickerCard(
    model: AiModel?,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = AccentIndigo,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (model != null) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${model.provider} • ${model.formattedPromptPrice}",
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(text = "Select model...", color = TextMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ComparisonMetricDualBar(
    metricName: String,
    valueA: Double,
    valueB: Double,
    formattedA: String,
    formattedB: String,
    higherIsBetter: Boolean,
    maxScale: Double,
    modifier: Modifier = Modifier
) {
    val isAWinner = if (higherIsBetter) valueA > valueB else valueA < valueB
    val isBWinner = if (higherIsBetter) valueB > valueA else valueB < valueA
    val isTie = valueA == valueB

    val colorA = if (isAWinner) EmeraldValue else TextSecondary
    val colorB = if (isBWinner) EmeraldValue else TextSecondary

    var animFactor by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(valueA, valueB) {
        animFactor = 1f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = animFactor,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "dualBarAnim"
    )

    val ratioA = ((valueA / maxScale).toFloat().coerceIn(0.04f, 1f)) * animatedProgress
    val ratioB = ((valueB / maxScale).toFloat().coerceIn(0.04f, 1f)) * animatedProgress

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = metricName,
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Values row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedA + if (isAWinner) " ★ WINNER" else "",
                    color = colorA,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isAWinner) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = (if (isBWinner) "WINNER ★ " else "") + formattedB,
                    color = colorB,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isBWinner) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dual Bars meeting in center or opposing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bar A (Right aligned, extending left)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceCardElevated),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ratioA)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isAWinner) EmeraldValue else AccentIndigo.copy(alpha = 0.5f))
                    )
                }

                // Center divider
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 12.dp)
                        .background(SurfaceCardBorder)
                )

                // Bar B (Left aligned, extending right)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceCardElevated),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ratioB)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isBWinner) EmeraldValue else AccentIndigo.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}
