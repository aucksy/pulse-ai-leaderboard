package com.pulseai.leaderboard.ui.viewmodel

import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.domain.model.CategoryFilter
import com.pulseai.leaderboard.domain.model.SortOrder

data class LeaderboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: CategoryFilter = CategoryFilter.OVERALL,
    val selectedSortOrder: SortOrder = SortOrder.SCORE_DESC,
    val allModels: List<AiModel> = emptyList(),
    val filteredModels: List<AiModel> = emptyList(),
    val selectedModelForDetail: AiModel? = null,
    val compareModelA: AiModel? = null,
    val compareModelB: AiModel? = null,
    val monthlyVolumeTokens: Long = 10_000_000L,
    val inputRatio: Float = 0.75f
) {
    val podiumModels: List<AiModel>
        get() = filteredModels.take(3)

    fun calculateMonthlyCost(model: AiModel): Double {
        val inputTokens = monthlyVolumeTokens * inputRatio
        val outputTokens = monthlyVolumeTokens * (1.0f - inputRatio)
        val inputCost = (inputTokens / 1_000_000.0) * model.promptPricePerMillion
        val outputCost = (outputTokens / 1_000_000.0) * model.completionPricePerMillion
        return inputCost + outputCost
    }

    val categoryAverages: CategoryAverages
        get() {
            if (filteredModels.isEmpty()) return CategoryAverages()
            return CategoryAverages(
                avgIntelligence = filteredModels.map { it.intelligenceScore }.average(),
                avgCoding = filteredModels.map { it.codingScore }.average(),
                avgReasoning = filteredModels.map { it.reasoningScore }.average(),
                avgAgentic = filteredModels.map { it.agenticScore }.average(),
                avgSpeed = filteredModels.map { it.speedTps }.average(),
                avgPrice = filteredModels.map { it.avgPricePerMillion }.average()
            )
        }
}

data class CategoryAverages(
    val avgIntelligence: Double = 90.0,
    val avgCoding: Double = 90.0,
    val avgReasoning: Double = 90.0,
    val avgAgentic: Double = 90.0,
    val avgSpeed: Double = 80.0,
    val avgPrice: Double = 3.0
)
