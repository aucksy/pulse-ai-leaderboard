package com.pulseai.leaderboard.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenRouterResponse(
    @SerialName("data")
    val data: List<OpenRouterModelDto> = emptyList()
)

@Serializable
data class OpenRouterModelDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("created")
    val created: Long? = 0L,
    @SerialName("description")
    val description: String? = null,
    @SerialName("context_length")
    val contextLength: Int? = 0,
    @SerialName("pricing")
    val pricing: OpenRouterPricingDto? = null,
    @SerialName("architecture")
    val architecture: OpenRouterArchitectureDto? = null,
    @SerialName("top_provider")
    val topProvider: OpenRouterTopProviderDto? = null,
    @SerialName("supported_parameters")
    val supportedParameters: List<String>? = emptyList()
) {
    /**
     * Converts raw per-token price string (e.g., "0.000003") into USD per 1M tokens.
     */
    val promptPricePerMillion: Double
        get() = (pricing?.prompt?.toDoubleOrNull() ?: 0.0) * 1_000_000.0

    val completionPricePerMillion: Double
        get() = (pricing?.completion?.toDoubleOrNull() ?: 0.0) * 1_000_000.0

    val supportsTools: Boolean
        get() = supportedParameters?.any { it.equals("tools", ignoreCase = true) } == true

    val supportsReasoning: Boolean
        get() = supportedParameters?.any {
            it.contains("reasoning", ignoreCase = true) || it.contains("thinking", ignoreCase = true)
        } == true || id.contains("r1", ignoreCase = true) || id.contains("reasoner", ignoreCase = true)

    val supportsVision: Boolean
        get() = architecture?.inputModalities?.any { it.contains("image", ignoreCase = true) } == true ||
                architecture?.modality?.contains("multimodal", ignoreCase = true) == true
}

@Serializable
data class OpenRouterPricingDto(
    @SerialName("prompt")
    val prompt: String? = null,
    @SerialName("completion")
    val completion: String? = null
)

@Serializable
data class OpenRouterArchitectureDto(
    @SerialName("modality")
    val modality: String? = null,
    @SerialName("input_modalities")
    val inputModalities: List<String>? = emptyList(),
    @SerialName("output_modalities")
    val outputModalities: List<String>? = emptyList()
)

@Serializable
data class OpenRouterTopProviderDto(
    @SerialName("max_completion_tokens")
    val maxCompletionTokens: Int? = null,
    @SerialName("is_moderated")
    val isModerated: Boolean? = null
)
