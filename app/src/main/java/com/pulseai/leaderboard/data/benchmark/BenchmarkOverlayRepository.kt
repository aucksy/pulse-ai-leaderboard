package com.pulseai.leaderboard.data.benchmark

import java.util.Locale
import kotlin.math.abs

data class BenchmarkProfile(
    val intelligenceScore: Double,
    val codingScore: Double,
    val reasoningScore: Double,
    val agenticScore: Double,
    val speedTps: Double,
    val isOpenWeight: Boolean,
    val provider: String,
    val isFlagship: Boolean = false
)

object BenchmarkOverlayRepository {

    // Curated flagship benchmark profiles
    private val curatedProfiles = mapOf(
        "anthropic/claude-opus-5.5" to BenchmarkProfile(
            intelligenceScore = 99.4,
            codingScore = 98.7,
            reasoningScore = 99.6,
            agenticScore = 98.9,
            speedTps = 48.0,
            isOpenWeight = false,
            provider = "Anthropic",
            isFlagship = true
        ),
        "anthropic/claude-fable-5.1" to BenchmarkProfile(
            intelligenceScore = 98.8,
            codingScore = 99.1,
            reasoningScore = 98.2,
            agenticScore = 99.4,
            speedTps = 62.0,
            isOpenWeight = false,
            provider = "Anthropic",
            isFlagship = true
        ),
        "anthropic/claude-3.5-sonnet" to BenchmarkProfile(
            intelligenceScore = 96.5,
            codingScore = 97.2,
            reasoningScore = 95.8,
            agenticScore = 96.9,
            speedTps = 78.0,
            isOpenWeight = false,
            provider = "Anthropic",
            isFlagship = true
        ),
        "openai/gpt-6-astra" to BenchmarkProfile(
            intelligenceScore = 99.1,
            codingScore = 98.2,
            reasoningScore = 99.4,
            agenticScore = 98.5,
            speedTps = 52.0,
            isOpenWeight = false,
            provider = "OpenAI",
            isFlagship = true
        ),
        "openai/gpt-6.1-sol" to BenchmarkProfile(
            intelligenceScore = 98.9,
            codingScore = 98.8,
            reasoningScore = 98.5,
            agenticScore = 99.2,
            speedTps = 58.0,
            isOpenWeight = false,
            provider = "OpenAI",
            isFlagship = true
        ),
        "openai/o1" to BenchmarkProfile(
            intelligenceScore = 97.8,
            codingScore = 96.9,
            reasoningScore = 99.8,
            agenticScore = 94.2,
            speedTps = 38.0,
            isOpenWeight = false,
            provider = "OpenAI",
            isFlagship = true
        ),
        "openai/gpt-4o" to BenchmarkProfile(
            intelligenceScore = 95.4,
            codingScore = 94.8,
            reasoningScore = 94.2,
            agenticScore = 95.1,
            speedTps = 105.0,
            isOpenWeight = false,
            provider = "OpenAI",
            isFlagship = true
        ),
        "google/gemini-4-argon" to BenchmarkProfile(
            intelligenceScore = 98.6,
            codingScore = 97.5,
            reasoningScore = 98.0,
            agenticScore = 97.8,
            speedTps = 85.0,
            isOpenWeight = false,
            provider = "Google",
            isFlagship = true
        ),
        "google/gemini-2.0-flash" to BenchmarkProfile(
            intelligenceScore = 94.2,
            codingScore = 92.5,
            reasoningScore = 93.8,
            agenticScore = 94.0,
            speedTps = 175.0,
            isOpenWeight = false,
            provider = "Google",
            isFlagship = true
        ),
        "deepseek/deepseek-v4.1-flash" to BenchmarkProfile(
            intelligenceScore = 97.5,
            codingScore = 98.0,
            reasoningScore = 97.2,
            agenticScore = 96.5,
            speedTps = 145.0,
            isOpenWeight = true,
            provider = "DeepSeek",
            isFlagship = true
        ),
        "deepseek/deepseek-r1" to BenchmarkProfile(
            intelligenceScore = 96.8,
            codingScore = 96.5,
            reasoningScore = 99.2,
            agenticScore = 93.4,
            speedTps = 42.0,
            isOpenWeight = true,
            provider = "DeepSeek",
            isFlagship = true
        ),
        "deepseek/deepseek-v3" to BenchmarkProfile(
            intelligenceScore = 95.0,
            codingScore = 95.4,
            reasoningScore = 94.5,
            agenticScore = 94.0,
            speedTps = 88.0,
            isOpenWeight = true,
            provider = "DeepSeek",
            isFlagship = true
        ),
        "meta-llama/llama-4-maverick" to BenchmarkProfile(
            intelligenceScore = 97.2,
            codingScore = 96.8,
            reasoningScore = 96.4,
            agenticScore = 95.9,
            speedTps = 92.0,
            isOpenWeight = true,
            provider = "Meta",
            isFlagship = true
        ),
        "meta-llama/llama-3.3-70b-instruct" to BenchmarkProfile(
            intelligenceScore = 93.8,
            codingScore = 92.4,
            reasoningScore = 92.9,
            agenticScore = 91.5,
            speedTps = 110.0,
            isOpenWeight = true,
            provider = "Meta",
            isFlagship = true
        ),
        "qwen/qwen-3-max" to BenchmarkProfile(
            intelligenceScore = 96.7,
            codingScore = 96.2,
            reasoningScore = 97.1,
            agenticScore = 95.4,
            speedTps = 82.0,
            isOpenWeight = true,
            provider = "Qwen",
            isFlagship = true
        ),
        "x-ai/grok-3" to BenchmarkProfile(
            intelligenceScore = 97.0,
            codingScore = 95.8,
            reasoningScore = 97.5,
            agenticScore = 95.0,
            speedTps = 75.0,
            isOpenWeight = false,
            provider = "xAI",
            isFlagship = true
        ),
        "mistralai/mistral-large-2411" to BenchmarkProfile(
            intelligenceScore = 94.6,
            codingScore = 93.7,
            reasoningScore = 94.1,
            agenticScore = 93.2,
            speedTps = 80.0,
            isOpenWeight = false,
            provider = "Mistral",
            isFlagship = true
        )
    )

    fun resolveProfile(modelId: String, modelName: String, contextLength: Int, pricePerMillion: Double): BenchmarkProfile {
        val lowerId = modelId.lowercase(Locale.ROOT)

        // Exact match
        curatedProfiles[lowerId]?.let { return it }

        // Partial match against curated keys
        for ((key, profile) in curatedProfiles) {
            val keySlug = key.substringAfter('/')
            if (lowerId.contains(keySlug) || lowerId.contains(key)) {
                return profile
            }
        }

        // Heuristic resolution
        val provider = resolveProvider(lowerId)
        val isOpen = resolveIsOpenWeight(lowerId, provider)

        // Deterministic hash seed based on model id
        val hash = abs(modelId.hashCode())
        val baseScoreVariation = (hash % 120) / 10.0 // 0.0 to 12.0

        val tierMultiplier = when {
            lowerId.contains("opus") || lowerId.contains("ultra") || lowerId.contains("max") || lowerId.contains("large") -> 93.0
            lowerId.contains("sonnet") || lowerId.contains("pro") || lowerId.contains("plus") || lowerId.contains("70b") -> 88.0
            lowerId.contains("flash") || lowerId.contains("mini") || lowerId.contains("haiku") || lowerId.contains("8b") -> 82.0
            lowerId.contains("reasoner") || lowerId.contains("thinking") || lowerId.contains("r1") || lowerId.contains("o1") || lowerId.contains("o3") -> 92.0
            else -> 80.0
        }

        val intelligence = (tierMultiplier + (baseScoreVariation * 0.4)).coerceIn(70.0, 99.5)
        val codingBonus = if (lowerId.contains("code") || lowerId.contains("deepseek") || lowerId.contains("sonnet")) 2.5 else 0.0
        val reasoningBonus = if (lowerId.contains("reason") || lowerId.contains("r1") || lowerId.contains("think")) 4.0 else 0.0
        val agenticBonus = if (lowerId.contains("agent") || lowerId.contains("opus") || lowerId.contains("4o")) 2.0 else 0.0

        val coding = (intelligence + codingBonus + ((hash / 10 % 30) / 10.0 - 1.5)).coerceIn(68.0, 99.5)
        val reasoning = (intelligence + reasoningBonus + ((hash / 100 % 30) / 10.0 - 1.5)).coerceIn(68.0, 99.9)
        val agentic = (intelligence + agenticBonus + ((hash / 1000 % 30) / 10.0 - 1.5)).coerceIn(68.0, 99.0)

        val speed = when {
            lowerId.contains("flash") || lowerId.contains("mini") || lowerId.contains("haiku") -> 140.0 + (hash % 60)
            lowerId.contains("reason") || lowerId.contains("r1") || lowerId.contains("o1") -> 35.0 + (hash % 20)
            else -> 65.0 + (hash % 45)
        }

        return BenchmarkProfile(
            intelligenceScore = String.format(Locale.US, "%.1f", intelligence).toDouble(),
            codingScore = String.format(Locale.US, "%.1f", coding).toDouble(),
            reasoningScore = String.format(Locale.US, "%.1f", reasoning).toDouble(),
            agenticScore = String.format(Locale.US, "%.1f", agentic).toDouble(),
            speedTps = String.format(Locale.US, "%.1f", speed).toDouble(),
            isOpenWeight = isOpen,
            provider = provider,
            isFlagship = false
        )
    }

    fun resolveProvider(lowerId: String): String {
        return when {
            lowerId.startsWith("anthropic/") || lowerId.contains("claude") -> "Anthropic"
            lowerId.startsWith("openai/") || lowerId.contains("gpt") || lowerId.contains("o1") -> "OpenAI"
            lowerId.startsWith("google/") || lowerId.contains("gemini") -> "Google"
            lowerId.startsWith("deepseek/") || lowerId.contains("deepseek") -> "DeepSeek"
            lowerId.startsWith("meta-llama/") || lowerId.contains("llama") -> "Meta"
            lowerId.startsWith("qwen/") || lowerId.contains("qwen") -> "Qwen"
            lowerId.startsWith("mistralai/") || lowerId.contains("mistral") -> "Mistral"
            lowerId.startsWith("x-ai/") || lowerId.contains("grok") -> "xAI"
            lowerId.startsWith("cohere/") || lowerId.contains("command") -> "Cohere"
            lowerId.startsWith("amazon/") || lowerId.contains("nova") -> "Amazon"
            lowerId.startsWith("microsoft/") || lowerId.contains("phi") -> "Microsoft"
            else -> {
                val prefix = lowerId.substringBefore('/')
                if (prefix != lowerId && prefix.isNotBlank()) {
                    prefix.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                } else {
                    "Independent"
                }
            }
        }
    }

    private fun resolveIsOpenWeight(lowerId: String, provider: String): Boolean {
        if (provider in listOf("Meta", "DeepSeek", "Qwen", "Mistral")) {
            return !lowerId.contains("mistral-large") // Mistral Large is commercial API
        }
        return lowerId.contains("llama") ||
                lowerId.contains("deepseek") ||
                lowerId.contains("qwen") ||
                lowerId.contains("phi") ||
                lowerId.contains("gemma") ||
                lowerId.contains("mistral-small") ||
                lowerId.contains("open")
    }
}
