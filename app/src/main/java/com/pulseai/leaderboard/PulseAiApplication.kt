package com.pulseai.leaderboard

import android.app.Application
import com.pulseai.leaderboard.data.local.PulseAiDatabase
import com.pulseai.leaderboard.data.remote.NetworkModule
import com.pulseai.leaderboard.data.repository.ModelRepository

class PulseAiApplication : Application() {

    lateinit var repository: ModelRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = PulseAiDatabase.getDatabase(this)
        val apiService = NetworkModule.apiService
        repository = ModelRepository(apiService, database.modelDao())
    }
}
