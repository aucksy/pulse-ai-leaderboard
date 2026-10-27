package com.pulseai.leaderboard.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.ui.theme.AmberCrown
import com.pulseai.leaderboard.ui.theme.BronzeRank
import com.pulseai.leaderboard.ui.theme.EmeraldValue
import com.pulseai.leaderboard.ui.theme.SilverRank
import com.pulseai.leaderboard.ui.theme.SurfaceCard
import com.pulseai.leaderboard.ui.theme.SurfaceCardBorder
import com.pulseai.leaderboard.ui.theme.TextMuted
import com.pulseai.leaderboard.ui.theme.TextPrimary
import java.util.Locale

@Composable
fun TopPodiumHeader(
    podiumModels: List<AiModel>,
    onModelClick: (AiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (podiumModels.isEmpty()) return

    val rank1 = podiumModels.getOrNull(0)
    val rank2 = podiumModels.getOrNull(1)
    val rank3 = podiumModels.getOrNull(2)

    val infiniteTransition = rememberInfiniteTransition(label = "crownPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Rank 2 (Silver)
        if (rank2 != null) {
            PodiumCard(
                model = rank2,
                rank = 2,
                height = 140.dp,
                accentColor = SilverRank,
                onClick = { onModelClick(rank2) },
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // Rank 1 (Gold / Crown) - Elevated
        if (rank1 != null) {
            PodiumCard(
                model = rank1,
                rank = 1,
                height = 160.dp,
                accentColor = AmberCrown,
                pulseModifier = Modifier.scale(pulseScale),
                onClick = { onModelClick(rank1) },
                modifier = Modifier.weight(1.15f)
            )
        }

        // Rank 3 (Bronze)
        if (rank3 != null) {
            PodiumCard(
                model = rank3,
                rank = 3,
                height = 130.dp,
                accentColor = BronzeRank,
                onClick = { onModelClick(rank3) },
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PodiumCard(
    model: AiModel,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pulseModifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(height)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(
            width = if (rank == 1) 1.5.dp else 1.dp,
            brush = if (rank == 1) {
                Brush.verticalGradient(listOf(AmberCrown, SurfaceCardBorder))
            } else {
                Brush.verticalGradient(listOf(accentColor.copy(alpha = 0.6f), SurfaceCardBorder))
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Rank Badge with Crown/Star
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f))
                        .border(1.dp, accentColor, CircleShape)
                )
                if (rank == 1) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Rank 1 Crown",
                        tint = AmberCrown,
                        modifier = Modifier
                            .size(18.dp)
                            .then(pulseModifier)
                    )
                } else {
                    Text(
                        text = "#$rank",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Model name & Provider
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = model.provider,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Score Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.size(3.dp))
                Text(
                    text = String.format(Locale.US, "%.1f", model.intelligenceScore),
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
