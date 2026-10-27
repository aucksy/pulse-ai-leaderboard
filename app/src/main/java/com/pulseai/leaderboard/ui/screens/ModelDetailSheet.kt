package com.pulseai.leaderboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.ui.components.PillBadge
import com.pulseai.leaderboard.ui.components.ProviderBadge
import com.pulseai.leaderboard.ui.components.RadarSpiderChart
import com.pulseai.leaderboard.ui.components.TokenCostCalculator
import com.pulseai.leaderboard.ui.theme.AccentIndigo
import com.pulseai.leaderboard.ui.theme.AmberCrown
import com.pulseai.leaderboard.ui.theme.CyanSpeed
import com.pulseai.leaderboard.ui.theme.EmeraldValue
import com.pulseai.leaderboard.ui.theme.PurpleReasoning
import com.pulseai.leaderboard.ui.theme.SurfaceCard
import com.pulseai.leaderboard.ui.theme.SurfaceCardBorder
import com.pulseai.leaderboard.ui.theme.SurfaceCardElevated
import com.pulseai.leaderboard.ui.theme.TextMuted
import com.pulseai.leaderboard.ui.theme.TextPrimary
import com.pulseai.leaderboard.ui.theme.TextSecondary
import com.pulseai.leaderboard.ui.viewmodel.LeaderboardUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ModelDetailSheet(
    model: AiModel,
    uiState: LeaderboardUiState,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onVolumeChange: (Long) -> Unit,
    onCompareClick: (AiModel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCard,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SurfaceCardBorder)
            )
        },
        modifier = modifier
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header with Model Title and Dismiss
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProviderBadge(provider = model.provider, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = model.cleanModelName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${model.provider} • ID: ${model.id}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceCardElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Description
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stat Cards Grid (Context, Pricing, Speed, Value Index)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricMiniCard(
                    title = "CONTEXT",
                    value = model.formattedContext,
                    subtitle = "tokens max",
                    accentColor = CyanSpeed,
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "SPEED",
                    value = "${model.speedTps.toInt()} tps",
                    subtitle = "throughput",
                    accentColor = EmeraldValue,
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "VALUE INDEX",
                    value = String.format(Locale.US, "%.1f", model.valueIndex),
                    subtitle = "perf / dollar",
                    accentColor = AmberCrown,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing Detail Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardElevated)
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "INPUT PRICING", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = model.formattedPromptPrice,
                        color = EmeraldValue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = 30.dp)
                        .background(SurfaceCardBorder)
                )
                Column {
                    Text(text = "OUTPUT PRICING", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = model.formattedCompletionPrice,
                        color = EmeraldValue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = 30.dp)
                        .background(SurfaceCardBorder)
                )
                Column {
                    Text(text = "WEIGHTS", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = if (model.isOpenWeight) "OPEN" else "PROPRIETARY",
                        color = if (model.isOpenWeight) AccentIndigo else AmberCrown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Capabilities Chips
            Text(
                text = "SUPPORTED CAPABILITIES",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CapabilityChip(title = "Tool Calling", icon = Icons.Default.Bolt, isSupported = model.supportsTools)
                CapabilityChip(title = "Reasoning Engine", icon = Icons.Default.Psychology, isSupported = model.supportsReasoning)
                CapabilityChip(title = "Vision Modality", icon = Icons.Default.Visibility, isSupported = model.supportsVision)
                CapabilityChip(title = "Code Synthesis", icon = Icons.Default.Code, isSupported = model.codingScore >= 85.0)
                CapabilityChip(title = "Autonomous Agentic", icon = Icons.Default.AutoAwesome, isSupported = model.agenticScore >= 90.0)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Benchmark Radar / Spider Chart
            Text(
                text = "CAPABILITY RADAR (VS CATEGORY AVG)",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            RadarSpiderChart(
                model = model,
                categoryAverages = uiState.categoryAverages
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Token Cost Simulator Slider
            TokenCostCalculator(
                model = model,
                monthlyVolumeTokens = uiState.monthlyVolumeTokens,
                onVolumeChange = onVolumeChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onCompareClick(model) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Compare in Arena Showdown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCardElevated)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = title, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(text = subtitle, color = TextMuted, fontSize = 9.sp)
        }
    }
}

@Composable
private fun CapabilityChip(
    title: String,
    icon: ImageVector,
    isSupported: Boolean,
    modifier: Modifier = Modifier
) {
    val chipColor = if (isSupported) AccentIndigo else TextMuted
    val bgColor = if (isSupported) AccentIndigo.copy(alpha = 0.12f) else SurfaceCardElevated

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, if (isSupported) AccentIndigo.copy(alpha = 0.4f) else SurfaceCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSupported) icon else Icons.Default.Close,
            contentDescription = null,
            tint = chipColor,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            color = if (isSupported) TextPrimary else TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
