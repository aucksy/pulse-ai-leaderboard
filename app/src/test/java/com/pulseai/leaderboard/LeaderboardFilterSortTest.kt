package com.pulseai.leaderboard

import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.domain.model.CategoryFilter
import com.pulseai.leaderboard.domain.model.SortOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class LeaderboardFilterSortTest {

    private val testModels = listOf(
        AiModel(
            id = "anthropic/claude-opus-5.5",
            name = "Claude Opus 5.5",
            provider = "Anthropic",
            description = "Apex intelligence",
            contextLength = 1_000_000,
            promptPricePerMillion = 15.0,
            completionPricePerMillion = 75.0,
            intelligenceScore = 99.4,
            codingScore = 98.7,
            reasoningScore = 99.6,
            agenticScore = 98.9,
            speedTps = 48.0,
            valueIndex = 2.2,
            isOpenWeight = false,
            supportsTools = true,
            supportsReasoning = true,
            supportsVision = true,
            isFlagship = true
        ),
        AiModel(
            id = "deepseek/deepseek-v4.1-flash",
            name = "DeepSeek V4.1 Flash",
            provider = "DeepSeek",
            description = "High speed MoE",
            contextLength = 128_000,
            promptPricePerMillion = 0.27,
            completionPricePerMillion = 1.10,
            intelligenceScore = 97.5,
            codingScore = 98.0,
            reasoningScore = 97.2,
            agenticScore = 96.5,
            speedTps = 145.0,
            valueIndex = 124.2,
            isOpenWeight = true,
            supportsTools = true,
            supportsReasoning = false,
            supportsVision = false,
            isFlagship = true
        ),
        AiModel(
            id = "google/gemini-4-argon",
            name = "Gemini 4 Argon",
            provider = "Google",
            description = "2M context titan",
            contextLength = 2_000_000,
            promptPricePerMillion = 5.0,
            completionPricePerMillion = 20.0,
            intelligenceScore = 98.6,
            codingScore = 97.5,
            reasoningScore = 98.0,
            agenticScore = 97.8,
            speedTps = 85.0,
            valueIndex = 7.8,
            isOpenWeight = false,
            supportsTools = true,
            supportsReasoning = true,
            supportsVision = true,
            isFlagship = true
        )
    )

    @Test
    fun testContextLengthFilter() {
        val filtered = testModels.filter { it.contextLength >= 1_000_000 }
        assertEquals(2, filtered.size)
        assertTrue(filtered.any { it.name == "Claude Opus 5.5" })
        assertTrue(filtered.any { it.name == "Gemini 4 Argon" })
    }

    @Test
    fun testOpenWeightFilter() {
        val openWeights = testModels.filter { it.isOpenWeight }
        assertEquals(1, openWeights.size)
        assertEquals("DeepSeek V4.1 Flash", openWeights[0].name)

        val proprietary = testModels.filter { !it.isOpenWeight }
        assertEquals(2, proprietary.size)
    }

    @Test
    fun testValueIndexSorting() {
        val sortedByValue = testModels.sortedByDescending { it.valueIndex }
        assertEquals("DeepSeek V4.1 Flash", sortedByValue[0].name)
    }

    @Test
    fun testPriceAscendingSorting() {
        val sortedByPrice = testModels.sortedBy { it.promptPricePerMillion }
        assertEquals("DeepSeek V4.1 Flash", sortedByPrice[0].name)
        assertEquals("Gemini 4 Argon", sortedByPrice[1].name)
        assertEquals("Claude Opus 5.5", sortedByPrice[2].name)
    }

    @Test
    fun testSearchQueryMatching() {
        val query = "deepseek"
        val results = testModels.filter {
            it.name.lowercase(Locale.ROOT).contains(query) ||
            it.provider.lowercase(Locale.ROOT).contains(query)
        }
        assertEquals(1, results.size)
        assertEquals("DeepSeek V4.1 Flash", results[0].name)
    }
}
