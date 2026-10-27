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
    val cleanModelName: String
        get() = formatCleanModelName(name, id, provider)

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

    companion object {
        fun formatCleanModelName(rawName: String, id: String, provider: String): String {
            var clean = rawName.trim()

            // 1. Strip provider colon prefix (e.g. "OpenAI: GPT-4o" -> "GPT-4o", "Anthropic: Claude 3.5 Sonnet" -> "Claude 3.5 Sonnet")
            if (clean.contains(":")) {
                val afterColon = clean.substringAfter(":").trim()
                if (afterColon.isNotBlank()) {
                    clean = afterColon
                }
            }

            // 2. Strip slug prefix if name is raw path (e.g. "openai/gpt-4o" -> "gpt-4o")
            if (clean.contains("/")) {
                val afterSlash = clean.substringAfterLast("/").trim()
                if (afterSlash.isNotBlank()) {
                    clean = afterSlash
                }
            }

            // 3. Clean common trailing clutter flags from OpenRouter catalog
            clean = clean.replace(Regex("(?i)\\s*\\(free\\)"), "")
            clean = clean.replace(Regex("(?i):free"), "")
            clean = clean.replace(Regex("(?i):beta"), "")
            clean = clean.replace(Regex("(?i)\\s*\\(self-moderated\\)"), "")
            clean = clean.replace(Regex("(?i)\\s*\\(nitro\\)"), "")
            clean = clean.trim()

            // 4. If name is slugified with hyphens (e.g. "gpt-4o-mini"), prettify into title words
            if (clean.contains("-") && !clean.contains(" ")) {
                clean = clean.split("-").joinToString(" ") { word ->
                    when (word.lowercase(Locale.ROOT)) {
                        "gpt" -> "GPT"
                        "ai" -> "AI"
                        "moe" -> "MoE"
                        "it" -> "IT"
                        "fp8" -> "FP8"
                        "bf16" -> "BF16"
                        "r1" -> "R1"
                        "v3" -> "V3"
                        "v4" -> "V4"
                        "o1" -> "o1"
                        "o3" -> "o3"
                        else -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    }
                }
            }

            return clean.ifBlank { rawName }
        }
    }
}
