package com.pulseai.leaderboard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class ModelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val created: Long,
    val description: String,
    val contextLength: Int,
    val promptPricePerToken: Double,
    val completionPricePerToken: Double,
    val promptPricePerMillion: Double,
    val completionPricePerMillion: Double,
    val inputModalities: String,
    val outputModalities: String,
    val isModerated: Boolean,
    val supportsTools: Boolean,
    val supportsReasoning: Boolean,
    val cachedAt: Long
)
