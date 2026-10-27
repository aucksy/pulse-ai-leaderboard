package com.pulseai.leaderboard

import com.pulseai.leaderboard.data.benchmark.BenchmarkOverlayRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BenchmarkOverlayTest {

    @Test
    fun testCuratedFlagshipModelsProfileResolution() {
        val opus = BenchmarkOverlayRepository.resolveProfile(
            modelId = "anthropic/claude-opus-5.5",
            modelName = "Claude Opus 5.5",
            contextLength = 1_000_000,
            pricePerMillion = 15.0
        )
        assertEquals("Anthropic", opus.provider)
        assertFalse(opus.isOpenWeight)
        assertEquals(99.4, opus.intelligenceScore, 0.01)
        assertEquals(98.7, opus.codingScore, 0.01)
        assertEquals(99.6, opus.reasoningScore, 0.01)
        assertTrue(opus.isFlagship)

        val deepseek = BenchmarkOverlayRepository.resolveProfile(
            modelId = "deepseek/deepseek-v4.1-flash",
            modelName = "DeepSeek V4.1 Flash",
            contextLength = 128_000,
            pricePerMillion = 0.27
        )
        assertEquals("DeepSeek", deepseek.provider)
        assertTrue(deepseek.isOpenWeight)
        assertTrue(deepseek.speedTps >= 100.0)
    }

    @Test
    fun testHeuristicResolutionForArbitraryModel() {
        val customModel = BenchmarkOverlayRepository.resolveProfile(
            modelId = "meta-llama/custom-research-32b",
            modelName = "Custom Research 32B",
            contextLength = 64_000,
            pricePerMillion = 0.50
        )
        assertEquals("Meta", customModel.provider)
        assertTrue(customModel.isOpenWeight)
        assertTrue(customModel.intelligenceScore in 70.0..99.5)
        assertTrue(customModel.codingScore in 68.0..99.5)
        assertTrue(customModel.reasoningScore in 68.0..99.9)
        assertTrue(customModel.speedTps > 0.0)
    }

    @Test
    fun testProviderNameInference() {
        assertEquals("Anthropic", BenchmarkOverlayRepository.resolveProvider("anthropic/claude-3-opus"))
        assertEquals("OpenAI", BenchmarkOverlayRepository.resolveProvider("openai/gpt-4o"))
        assertEquals("Google", BenchmarkOverlayRepository.resolveProvider("google/gemini-pro"))
        assertEquals("DeepSeek", BenchmarkOverlayRepository.resolveProvider("deepseek/deepseek-chat"))
        assertEquals("Meta", BenchmarkOverlayRepository.resolveProvider("meta-llama/llama-3-8b"))
        assertEquals("Qwen", BenchmarkOverlayRepository.resolveProvider("qwen/qwen-2.5-coder"))
        assertEquals("xAI", BenchmarkOverlayRepository.resolveProvider("x-ai/grok-2"))
    }
}
