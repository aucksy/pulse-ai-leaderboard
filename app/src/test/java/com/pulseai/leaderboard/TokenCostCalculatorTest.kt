package com.pulseai.leaderboard

import com.pulseai.leaderboard.domain.model.AiModel
import org.junit.Assert.assertEquals
import org.junit.Test

class TokenCostCalculatorTest {

    private val model = AiModel(
        id = "test/model",
        name = "Test Model",
        provider = "Test",
        description = "Test description",
        contextLength = 128_000,
        promptPricePerMillion = 3.0,     // $3 per 1M input tokens
        completionPricePerMillion = 15.0, // $15 per 1M output tokens
        intelligenceScore = 95.0,
        codingScore = 94.0,
        reasoningScore = 93.0,
        agenticScore = 92.0,
        speedTps = 80.0,
        valueIndex = 10.5,
        isOpenWeight = false,
        supportsTools = true,
        supportsReasoning = false,
        supportsVision = false,
        isFlagship = false
    )

    @Test
    fun testMonthlySpendCalculationFor10MillionTokens() {
        val totalVolume = 10_000_000L // 10M tokens
        val inputRatio = 0.75
        val outputRatio = 0.25

        val inputTokens = totalVolume * inputRatio       // 7.5M tokens
        val outputTokens = totalVolume * outputRatio     // 2.5M tokens

        val inputCost = (inputTokens / 1_000_000.0) * model.promptPricePerMillion       // 7.5 * 3.0 = $22.50
        val outputCost = (outputTokens / 1_000_000.0) * model.completionPricePerMillion // 2.5 * 15.0 = $37.50

        val totalCost = inputCost + outputCost // $60.00
        assertEquals(60.0, totalCost, 0.001)
    }

    @Test
    fun testFreeModelMonthlySpendIsZero() {
        val freeModel = model.copy(promptPricePerMillion = 0.0, completionPricePerMillion = 0.0)
        val totalVolume = 50_000_000L
        val inputTokens = totalVolume * 0.75
        val outputTokens = totalVolume * 0.25

        val totalCost = (inputTokens / 1_000_000.0 * freeModel.promptPricePerMillion) +
                (outputTokens / 1_000_000.0 * freeModel.completionPricePerMillion)

        assertEquals(0.0, totalCost, 0.0001)
    }
}
