package com.pulseai.leaderboard.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.ui.theme.AccentIndigo
import com.pulseai.leaderboard.ui.theme.AmberCrown
import com.pulseai.leaderboard.ui.theme.BronzeRank
import com.pulseai.leaderboard.ui.theme.CyanSpeed
import com.pulseai.leaderboard.ui.theme.EmeraldValue
import com.pulseai.leaderboard.ui.theme.SilverRank
import com.pulseai.leaderboard.ui.theme.SurfaceCard
import com.pulseai.leaderboard.ui.theme.SurfaceCardBorder
import com.pulseai.leaderboard.ui.theme.SurfaceCardElevated
import com.pulseai.leaderboard.ui.theme.TextMuted
import com.pulseai.leaderboard.ui.theme.TextPrimary
import com.pulseai.leaderboard.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun LeaderboardRowItem(
    model: AiModel,
    rank: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var targetProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(model.intelligenceScore) {
        targetProgress = (model.intelligenceScore / 100.0).toFloat().coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "scoreProgress"
    )

    val rankColor = when (rank) {
        1 -> AmberCrown
        2 -> SilverRank
        3 -> BronzeRank
        else -> TextMuted
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(
            1.dp,
            if (rank == 1) AmberCrown.copy(alpha = 0.5f) else SurfaceCardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated Rank Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (rank <= 3) rankColor.copy(alpha = 0.15f) else SurfaceCardElevated
                        )
                        .border(
                            1.dp,
                            if (rank <= 3) rankColor.copy(alpha = 0.5f) else SurfaceCardBorder,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = rank,
                        transitionSpec = {
                            slideInVertically { height -> height } + fadeIn() togetherWith
                                    slideOutVertically { height -> -height } + fadeOut()
                        },
                        label = "rankAnim"
                    ) { targetRank ->
                        Text(
                            text = "$targetRank",
                            color = rankColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Provider Initial Badge
                ProviderBadge(provider = model.provider)

                Spacer(modifier = Modifier.width(10.dp))

                // Model Name & Provider
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = model.cleanModelName,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "${model.provider} • ${model.formattedSpeed}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Context Pill
                PillBadge(
                    text = model.formattedContext,
                    textColor = CyanSpeed,
                    bgColor = CyanSpeed.copy(alpha = 0.12f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Price Pill
                PillBadge(
                    text = model.formattedPromptPrice,
                    textColor = EmeraldValue,
                    bgColor = EmeraldValue.copy(alpha = 0.12f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animated Horizontal Score Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(SurfaceCardElevated)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(AccentIndigo, EmeraldValue)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = String.format(Locale.US, "%.1f", model.intelligenceScore),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AccentIndigo
                )
            }
        }
    }
}

@Composable
fun ProviderBadge(
    provider: String,
    modifier: Modifier = Modifier
) {
    val initial = provider.take(2).uppercase(Locale.ROOT)
    val color = when (provider.lowercase(Locale.ROOT)) {
        "anthropic" -> Color(0xFFD97706)
        "openai" -> Color(0xFF10B981)
        "google" -> Color(0xFF3B82F6)
        "deepseek" -> Color(0xFF06B6D4)
        "meta" -> Color(0xFF6366F1)
        "qwen" -> Color(0xFFA855F7)
        "xai" -> Color(0xFFEC4899)
        else -> Color(0xFF94A3B8)
    }

    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun PillBadge(
    text: String,
    textColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
