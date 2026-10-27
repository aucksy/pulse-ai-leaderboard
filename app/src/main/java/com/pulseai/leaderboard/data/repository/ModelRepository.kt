package com.pulseai.leaderboard.data.repository

import com.pulseai.leaderboard.data.benchmark.BenchmarkOverlayRepository
import com.pulseai.leaderboard.data.fallback.FallbackSeedData
import com.pulseai.leaderboard.data.local.ModelDao
import com.pulseai.leaderboard.data.local.ModelEntity
import com.pulseai.leaderboard.data.remote.OpenRouterApiService
import com.pulseai.leaderboard.data.remote.OpenRouterModelDto
import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale

class ModelRepository(
    private val apiService: OpenRouterApiService,
    private val modelDao: ModelDao
) {

    companion object {
        const val CACHE_TTL_MS = 6 * 60 * 60 * 1000L // 6 hours
    }

    fun getModelsStream(forceRefresh: Boolean = false): Flow<Resource<List<AiModel>>> = flow {
        emit(Resource.Loading)

        val lastCacheTime = modelDao.getLastCacheTime() ?: 0L
        val isCacheExpired = System.currentTimeMillis() - lastCacheTime > CACHE_TTL_MS
        val shouldFetchNetwork = forceRefresh || isCacheExpired || lastCacheTime == 0L

        var isOfflineFallback = false

        if (shouldFetchNetwork) {
            try {
                val response = apiService.getModels()
                val dtoList = response.data
                if (dtoList.isNotEmpty()) {
                    val entities = dtoList.map { it.toEntity() }
                    modelDao.insertModels(entities)
                } else {
                    ensureSeedDataIfEmpty()
                }
            } catch (e: Exception) {
                // Network error: fall back to cache or seed
                isOfflineFallback = true
                ensureSeedDataIfEmpty()
            }
        }

        // Fetch from local database
        try {
            modelDao.getAllModels().collect { entities ->
                if (entities.isEmpty()) {
                    // Seed if still empty
                    val seedEntities = FallbackSeedData.seedModels.map { it.toEntity() }
                    modelDao.insertModels(seedEntities)
                } else {
                    val domainModels = entities.map { it.toDomain() }
                    emit(Resource.Success(domainModels, isOffline = isOfflineFallback))
                }
            }
        } catch (e: Exception) {
            // Ultimate fallback to in-memory seed models
            val fallbackDomainModels = FallbackSeedData.seedModels.map { it.toDomainDirect() }
            emit(Resource.Success(fallbackDomainModels, isOffline = true))
        }
    }

    private suspend fun ensureSeedDataIfEmpty() {
        val lastCache = modelDao.getLastCacheTime()
        if (lastCache == null || lastCache == 0L) {
            val seedEntities = FallbackSeedData.seedModels.map { it.toEntity() }
            modelDao.insertModels(seedEntities)
        }
    }

    private fun OpenRouterModelDto.toEntity(): ModelEntity {
        return ModelEntity(
            id = id,
            name = name ?: id.substringAfter('/'),
            created = created ?: 0L,
            description = description ?: "Advanced neural language and intelligence model.",
            contextLength = contextLength ?: 128_000,
            promptPricePerToken = pricing?.prompt?.toDoubleOrNull() ?: 0.0,
            completionPricePerToken = pricing?.completion?.toDoubleOrNull() ?: 0.0,
            promptPricePerMillion = promptPricePerMillion,
            completionPricePerMillion = completionPricePerMillion,
            inputModalities = architecture?.inputModalities?.joinToString(",") ?: "text",
            outputModalities = architecture?.outputModalities?.joinToString(",") ?: "text",
            isModerated = topProvider?.isModerated ?: false,
            supportsTools = supportsTools,
            supportsReasoning = supportsReasoning,
            cachedAt = System.currentTimeMillis()
        )
    }

    private fun ModelEntity.toDomain(): AiModel {
        val benchmark = BenchmarkOverlayRepository.resolveProfile(
            modelId = id,
            modelName = name,
            contextLength = contextLength,
            pricePerMillion = promptPricePerMillion
        )

        val avgPrice = (promptPricePerMillion + completionPricePerMillion) / 2.0
        val valueIdx = benchmark.intelligenceScore / (avgPrice + 0.1)

        return AiModel(
            id = id,
            name = name,
            provider = benchmark.provider,
            description = description,
            contextLength = contextLength,
            promptPricePerMillion = promptPricePerMillion,
            completionPricePerMillion = completionPricePerMillion,
            intelligenceScore = benchmark.intelligenceScore,
            codingScore = benchmark.codingScore,
            reasoningScore = benchmark.reasoningScore,
            agenticScore = benchmark.agenticScore,
            speedTps = benchmark.speedTps,
            valueIndex = String.format(Locale.US, "%.1f", valueIdx).toDouble(),
            isOpenWeight = benchmark.isOpenWeight,
            supportsTools = supportsTools,
            supportsReasoning = supportsReasoning,
            supportsVision = inputModalities.contains("image", ignoreCase = true),
            isFlagship = benchmark.isFlagship
        )
    }

    private fun OpenRouterModelDto.toDomainDirect(): AiModel {
        val benchmark = BenchmarkOverlayRepository.resolveProfile(
            modelId = id,
            modelName = name ?: id,
            contextLength = contextLength ?: 128_000,
            pricePerMillion = promptPricePerMillion
        )
        val avgPrice = (promptPricePerMillion + completionPricePerMillion) / 2.0
        val valueIdx = benchmark.intelligenceScore / (avgPrice + 0.1)

        return AiModel(
            id = id,
            name = name ?: id.substringAfter('/'),
            provider = benchmark.provider,
            description = description ?: "Advanced neural language and intelligence model.",
            contextLength = contextLength ?: 128_000,
            promptPricePerMillion = promptPricePerMillion,
            completionPricePerMillion = completionPricePerMillion,
            intelligenceScore = benchmark.intelligenceScore,
            codingScore = benchmark.codingScore,
            reasoningScore = benchmark.reasoningScore,
            agenticScore = benchmark.agenticScore,
            speedTps = benchmark.speedTps,
            valueIndex = String.format(Locale.US, "%.1f", valueIdx).toDouble(),
            isOpenWeight = benchmark.isOpenWeight,
            supportsTools = supportsTools,
            supportsReasoning = supportsReasoning,
            supportsVision = supportsVision,
            isFlagship = benchmark.isFlagship
        )
    }
}
