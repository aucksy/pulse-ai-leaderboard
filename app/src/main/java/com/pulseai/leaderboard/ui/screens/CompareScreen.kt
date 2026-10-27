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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    viewModel: LeaderboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val modelA = uiState.compareModelA ?: uiState.filteredModels.firstOrNull()
    val modelB = uiState.compareModelB ?: uiState.filteredModels.getOrNull(1)

    var pickingForSlot by remember { mutableStateOf<String?>(null) }
    val pickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

        Spacer(modifier = Modifier.height(16.dp))

        // Model Selector Row with Swap Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Model A Selector
            ModelPickerCard(
                model = modelA,
                title = "MODEL A",
                onClick = { pickingForSlot = "A" },
                modifier = Modifier.weight(1f)
            )

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
            ModelPickerCard(
                model = modelB,
                title = "MODEL B",
                onClick = { pickingForSlot = "B" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (modelA != null && modelB != null) {
            // Calculate Showdown Winner Tally
            val metrics = listOf(
                Pair(modelA.intelligenceScore, modelB.intelligenceScore), // higher
                Pair(modelA.codingScore, modelB.codingScore),             // higher
                Pair(modelA.reasoningScore, modelB.reasoningScore),       // higher
                Pair(modelA.agenticScore, modelB.agenticScore),           // higher
                Pair(modelA.speedTps, modelB.speedTps),                   // higher
                Pair(modelA.contextLength.toDouble(), modelB.contextLength.toDouble()), // higher
                Pair(modelB.promptPricePerMillion, modelA.promptPricePerMillion),       // lower is better
                Pair(modelB.completionPricePerMillion, modelA.completionPricePerMillion), // lower is better
                Pair(modelA.valueIndex, modelB.valueIndex)                // higher
            )
            val winsA = metrics.count { it.first > it.second }
            val winsB = metrics.count { it.second > it.first }

            // Showdown Tally Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardElevated)
                    .border(1.dp, if (winsA != winsB) EmeraldValue.copy(alpha = 0.4f) else SurfaceCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = if (winsA != winsB) AmberCrown else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        val winnerText = when {
                            winsA > winsB -> "${modelA.cleanModelName} leads $winsA to $winsB"
                            winsB > winsA -> "${modelB.cleanModelName} leads $winsB to $winsA"
                            else -> "Dead Heat ($winsA - $winsB tie)"
                        }
                        Text(
                            text = winnerText,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Evaluated across 9 intelligence, speed, and cost metrics",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricDualBar(
                metricName = "CODING KINGS BENCHMARK",
                valueA = modelA.codingScore,
                valueB = modelB.codingScore,
                formattedA = String.format(Locale.US, "%.1f", modelA.codingScore),
                formattedB = String.format(Locale.US, "%.1f", modelB.codingScore),
                higherIsBetter = true,
                maxScale = 100.0
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricDualBar(
                metricName = "REASONING & SYSTEM-2",
                valueA = modelA.reasoningScore,
                valueB = modelB.reasoningScore,
                formattedA = String.format(Locale.US, "%.1f", modelA.reasoningScore),
                formattedB = String.format(Locale.US, "%.1f", modelB.reasoningScore),
                higherIsBetter = true,
                maxScale = 100.0
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricDualBar(
                metricName = "AGENTIC TOOL MASTERY",
                valueA = modelA.agenticScore,
                valueB = modelB.agenticScore,
                formattedA = String.format(Locale.US, "%.1f", modelA.agenticScore),
                formattedB = String.format(Locale.US, "%.1f", modelB.agenticScore),
                higherIsBetter = true,
                maxScale = 100.0
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricDualBar(
                metricName = "SPEED (TOKENS / SEC)",
                valueA = modelA.speedTps,
                valueB = modelB.speedTps,
                formattedA = "${modelA.speedTps.toInt()} tps",
                formattedB = "${modelB.speedTps.toInt()} tps",
                higherIsBetter = true,
                maxScale = 220.0
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricDualBar(
                metricName = "CONTEXT WINDOW (TOKENS)",
                valueA = modelA.contextLength.toDouble(),
                valueB = modelB.contextLength.toDouble(),
                formattedA = modelA.formattedContext,
                formattedB = modelB.formattedContext,
                higherIsBetter = true,
                maxScale = 2_000_000.0
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricDualBar(
                metricName = "PROMPT PRICING ($ / 1M TOKENS)",
                valueA = modelA.promptPricePerMillion,
                valueB = modelB.promptPricePerMillion,
                formattedA = modelA.formattedPromptPrice,
                formattedB = modelB.formattedPromptPrice,
                higherIsBetter = false, // cheaper is better
                maxScale = 20.0
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricDualBar(
                metricName = "COMPLETION PRICING ($ / 1M TOKENS)",
                valueA = modelA.completionPricePerMillion,
                valueB = modelB.completionPricePerMillion,
                formattedA = modelA.formattedCompletionPrice,
                formattedB = modelB.formattedCompletionPrice,
                higherIsBetter = false, // cheaper is better
                maxScale = 80.0
            )

            Spacer(modifier = Modifier.height(12.dp))

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

        // Searchable Model Picker Bottom Sheet
        if (pickingForSlot != null) {
            SearchableModelPickerSheet(
                slotTitle = if (pickingForSlot == "A") "Select Model A" else "Select Model B",
                models = uiState.allModels,
                onDismiss = { pickingForSlot = null },
                onSelectModel = { selected ->
                    if (pickingForSlot == "A") {
                        viewModel.selectCompareModelA(selected)
                    } else {
                        viewModel.selectCompareModelB(selected)
                    }
                    pickingForSlot = null
                }
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
                    text = model.cleanModelName,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchableModelPickerSheet(
    slotTitle: String,
    models: List<AiModel>,
    onDismiss: () -> Unit,
    onSelectModel: (AiModel) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(models, searchQuery) {
        if (searchQuery.isBlank()) models
        else {
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            models.filter {
                it.cleanModelName.lowercase(Locale.ROOT).contains(q) ||
                it.name.lowercase(Locale.ROOT).contains(q) ||
                it.provider.lowercase(Locale.ROOT).contains(q)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SurfaceCardBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = slotTitle,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by model name or provider...", color = TextMuted, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCardElevated,
                    unfocusedContainerColor = SurfaceCardElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentIndigo,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // List of models
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filtered, key = { it.id }) { m ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceCardElevated)
                            .clickable { onSelectModel(m) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProviderBadge(provider = m.provider)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = m.cleanModelName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${m.provider} • Score: ${String.format(Locale.US, "%.1f", m.intelligenceScore)} • ${m.formattedPromptPrice}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
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

            // Dual Bars meeting in center
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
