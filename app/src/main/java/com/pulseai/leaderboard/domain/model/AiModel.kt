package com.pulseai.leaderboard.domain.model

import java.util.Locale

data class AiModel(
    val id: String,
    val name: String,
    val provider: String,
    val description: String,
    val contextLength: Int,
    val promptPricePerMillion: Double,
    val completionPricePerMillion: Double,
    val intelligenceScore: Double,
    val codingScore: Double,
    val reasoningScore: Double,
    val agenticScore: Double,
    val speedTps: Double,
    val valueIndex: Double,
    val isOpenWeight: Boolean,
    val supportsTools: Boolean,
    val supportsReasoning: Boolean,
    val supportsVision: Boolean,
    val isFlagship: Boolean
) {
    val avgPricePerMillion: Double
        get() = (promptPricePerMillion + completionPricePerMillion) / 2.0

    val formattedContext: String
        get() = when {
            contextLength >= 1_000_000 -> String.format(Locale.US, "%.1fM", contextLength / 1_000_000.0).replace(".0M", "M")
            contextLength >= 1_000 -> "${contextLength / 1_000}K"
            else -> "$contextLength"
        }

    val formattedPromptPrice: String
        get() = if (promptPricePerMillion == 0.0) "Free" else String.format(Locale.US, "$%.2f/1M", promptPricePerMillion)

    val formattedCompletionPrice: String
        get() = if (completionPricePerMillion == 0.0) "Free" else String.format(Locale.US, "$%.2f/1M", completionPricePerMillion)

    val formattedPricingSummary: String
        get() = if (promptPricePerMillion == 0.0 && completionPricePerMillion == 0.0) {
            "Free"
        } else {
            String.format(Locale.US, "$%.2f / $%.2f", promptPricePerMillion, completionPricePerMillion)
        }

    val formattedSpeed: String
        get() = String.format(Locale.US, "%.0f tps", speedTps)

    val overallScore: Double
        get() = intelligenceScore
}
