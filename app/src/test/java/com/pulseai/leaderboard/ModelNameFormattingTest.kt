package com.pulseai.leaderboard

import com.pulseai.leaderboard.domain.model.AiModel
import org.junit.Assert.assertEquals
import org.junit.Test

class ModelNameFormattingTest {

    @Test
    fun testProviderPrefixStripping() {
        assertEquals(
            "GPT-4o",
            AiModel.formatCleanModelName("OpenAI: GPT-4o", "openai/gpt-4o", "OpenAI")
        )
        assertEquals(
            "Claude 3.5 Sonnet",
            AiModel.formatCleanModelName("Anthropic: Claude 3.5 Sonnet", "anthropic/claude-3.5-sonnet", "Anthropic")
        )
        assertEquals(
            "Gemini 2.0 Flash",
            AiModel.formatCleanModelName("Google: Gemini 2.0 Flash", "google/gemini-2.0-flash", "Google")
        )
        assertEquals(
            "Kimi K3.1 Pro",
            AiModel.formatCleanModelName("MoonshotAI: Kimi K3.1 Pro", "moonshotai/kimi-k3.1-pro", "MoonshotAI")
        )
    }

    @Test
    fun testTrailingClutterRemoval() {
        assertEquals(
            "Flint",
            AiModel.formatCleanModelName("Recursive: Flint (free)", "recursive/flint-v1.0:free", "Recursive")
        )
        assertEquals(
            "Xing4.0 29B A4B",
            AiModel.formatCleanModelName("XingChen: Xing4.0 29B A4B:free", "xingchen-agi/xing4.0-29b-a4b:free", "XingChen")
        )
        assertEquals(
            "Gemini 1.5 Pro",
            AiModel.formatCleanModelName("Google: Gemini 1.5 Pro (self-moderated)", "google/gemini-pro-1.5", "Google")
        )
    }

    @Test
    fun testSlugFormattingWhenRawIdUsedAsName() {
        assertEquals(
            "DeepSeek R1",
            AiModel.formatCleanModelName("deepseek/deepseek-r1", "deepseek/deepseek-r1", "DeepSeek")
        )
        assertEquals(
            "Llama 3.3 70B Instruct",
            AiModel.formatCleanModelName("meta-llama/llama-3.3-70b-instruct", "meta-llama/llama-3.3-70b-instruct", "Meta")
        )
    }

    @Test
    fun testAiModelCleanNameProperty() {
        val model = AiModel(
            id = "openai/gpt-6.1-terra-pro",
            name = "OpenAI: GPT-6.1 Terra Pro",
            provider = "OpenAI",
            description = "Frontier reasoning model",
            contextLength = 1_000_000,
            promptPricePerMillion = 10.0,
            completionPricePerMillion = 40.0,
            intelligenceScore = 99.0,
            codingScore = 98.5,
            reasoningScore = 99.2,
            agenticScore = 98.0,
            speedTps = 65.0,
            valueIndex = 4.0,
            isOpenWeight = false,
            supportsTools = true,
            supportsReasoning = true,
            supportsVision = true,
            isFlagship = true
        )

        // The clean model name must show the actual model name first, NOT the company name!
        assertEquals("GPT-6.1 Terra Pro", model.cleanModelName)
    }
}
