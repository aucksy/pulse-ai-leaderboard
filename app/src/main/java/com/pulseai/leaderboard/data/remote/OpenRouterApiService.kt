package com.pulseai.leaderboard.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenRouterApiService {
    @GET("models")
    suspend fun getModels(
        @Query("sort") sort: String? = null
    ): OpenRouterResponse
}
