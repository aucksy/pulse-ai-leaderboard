package com.pulseai.leaderboard.domain.model

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T, val isOffline: Boolean = false) : Resource<T>()
    data class Error<T>(val message: String, val cachedData: T? = null) : Resource<T>()
}
