package com.pulseai.leaderboard.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.ui.theme.AccentIndigo
import com.pulseai.leaderboard.ui.theme.EmeraldValue
import com.pulseai.leaderboard.ui.theme.SurfaceCard
import com.pulseai.leaderboard.ui.theme.SurfaceCardBorder
import com.pulseai.leaderboard.ui.theme.SurfaceCardElevated
import com.pulseai.leaderboard.ui.theme.TextMuted
import com.pulseai.leaderboard.ui.theme.TextPrimary
import com.pulseai.leaderboard.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun TokenCostCalculator(
    model: AiModel,
    monthlyVolumeTokens: Long,
    onVolumeChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // 75% input, 25% output split
    val inputRatio = 0.75
    val outputRatio = 0.25
    val inputTokens = monthlyVolumeTokens * inputRatio
    val outputTokens = monthlyVolumeTokens * outputRatio

    val monthlySpend = (inputTokens / 1_000_000.0 * model.promptPricePerMillion) +
            (outputTokens / 1_000_000.0 * model.completionPricePerMillion)

    val formattedSpend = String.format(Locale.US, "$%.2f", monthlySpend)
    val formattedVolume = "${monthlyVolumeTokens / 1_000_000}M"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCardElevated)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Token Spend Simulator",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            AnimatedContent(
                targetState = formattedSpend,
                transitionSpec = {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                },
                label = "spendAnim"
            ) { targetSpend ->
                Text(
                    text = "$targetSpend / mo",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EmeraldValue
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Monthly Volume: $formattedVolume tokens (75% input, 25% output)",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Slider(
            value = (monthlyVolumeTokens / 1_000_000f),
            onValueChange = { onVolumeChange((it.toLong() * 1_000_000L).coerceIn(1_000_000L, 100_000_000L)) },
            valueRange = 1f..100f,
            steps = 98,
            colors = SliderDefaults.colors(
                thumbColor = AccentIndigo,
                activeTrackColor = AccentIndigo,
                inactiveTrackColor = SurfaceCard
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "1M tokens", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            Text(text = "50M tokens", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            Text(text = "100M tokens", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val presets = listOf(1L, 5L, 10L, 25L, 50L, 100L)
            presets.forEach { millions ->
                val tokens = millions * 1_000_000L
                val isSelected = (monthlyVolumeTokens / 1_000_000L) == millions
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) AccentIndigo else SurfaceCard)
                        .border(1.dp, if (isSelected) AccentIndigo else SurfaceCardBorder, RoundedCornerShape(6.dp))
                        .clickable { onVolumeChange(tokens) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${millions}M",
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
