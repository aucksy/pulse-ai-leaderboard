package com.pulseai.leaderboard

import com.pulseai.leaderboard.data.remote.NetworkModule
import com.pulseai.leaderboard.data.remote.OpenRouterResponse
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenRouterParsingTest {

    @Test
    fun testOpenRouterResponseParsingAndPricingMath() {
        val sampleJson = """
        {
            "data": [
                {
                    "id": "anthropic/claude-opus-5.5",
                    "name": "Claude Opus 5.5",
                    "created": 1735689600,
                    "description": "Anthropic's flagship intelligence model",
                    "context_length": 1000000,
                    "pricing": {
                        "prompt": "0.000015",
                        "completion": "0.000075"
                    },
                    "architecture": {
                        "modality": "multimodal",
                        "input_modalities": ["text", "image"],
                        "output_modalities": ["text"]
                    },
                    "top_provider": {
                        "max_completion_tokens": 32768,
                        "is_moderated": true
                    },
                    "supported_parameters": ["tools", "reasoning"]
                },
                {
                    "id": "deepseek/deepseek-v4.1-flash",
                    "name": "DeepSeek V4.1 Flash",
                    "pricing": {
                        "prompt": "0.00000027",
                        "completion": "0.0000011"
                    },
                    "architecture": {
                        "input_modalities": ["text"]
                    }
                }
            ]
        }
        """.trimIndent()

        val response = NetworkModule.json.decodeFromString<OpenRouterResponse>(sampleJson)
        assertEquals(2, response.data.size)

        val opus = response.data[0]
        assertEquals("anthropic/claude-opus-5.5", opus.id)
        assertEquals("Claude Opus 5.5", opus.name)
        assertEquals(1_000_000, opus.contextLength)

        // Math check: 0.000015 * 1,000,000 = 15.0 USD
        assertEquals(15.0, opus.promptPricePerMillion, 0.0001)
        // Math check: 0.000075 * 1,000,000 = 75.0 USD
        assertEquals(75.0, opus.completionPricePerMillion, 0.0001)

        assertTrue(opus.supportsTools)
        assertTrue(opus.supportsReasoning)
        assertTrue(opus.supportsVision)

        val deepseek = response.data[1]
        assertEquals(0.27, deepseek.promptPricePerMillion, 0.0001)
        assertEquals(1.10, deepseek.completionPricePerMillion, 0.0001)
        assertFalse(deepseek.supportsVision)
    }

    @Test
    fun testNullAndMissingFieldsHandledSafely() {
        val minimalJson = """
        {
            "data": [
                {
                    "id": "unknown/minimal-model"
                }
            ]
        }
        """.trimIndent()

        val response = NetworkModule.json.decodeFromString<OpenRouterResponse>(minimalJson)
        assertEquals(1, response.data.size)

        val model = response.data[0]
        assertEquals("unknown/minimal-model", model.id)
        assertEquals(0.0, model.promptPricePerMillion, 0.0001)
        assertEquals(0.0, model.completionPricePerMillion, 0.0001)
        assertFalse(model.supportsTools)
        assertFalse(model.supportsReasoning)
        assertFalse(model.supportsVision)
    }
}
