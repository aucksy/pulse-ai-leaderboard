package com.pulseai.leaderboard

import com.pulseai.leaderboard.data.fallback.FallbackSeedData
import com.pulseai.leaderboard.data.local.ModelDao
import com.pulseai.leaderboard.data.local.ModelEntity
import com.pulseai.leaderboard.data.remote.OpenRouterApiService
import com.pulseai.leaderboard.data.remote.OpenRouterResponse
import com.pulseai.leaderboard.data.repository.ModelRepository
import com.pulseai.leaderboard.domain.model.CategoryFilter
import com.pulseai.leaderboard.domain.model.SortOrder
import com.pulseai.leaderboard.ui.viewmodel.LeaderboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Fake DAO
    private val fakeDao = object : ModelDao {
        private val list = mutableListOf<ModelEntity>()
        override fun getAllModels(): Flow<List<ModelEntity>> = flowOf(list)
        override suspend fun getModelById(id: String): ModelEntity? = list.find { it.id == id }
        override suspend fun insertModels(models: List<ModelEntity>) {
            list.clear()
            list.addAll(models)
        }
        override suspend fun clearAll() { list.clear() }
        override suspend fun getLastCacheTime(): Long = System.currentTimeMillis()
    }

    // Fake API service
    private val fakeApi = object : OpenRouterApiService {
        override suspend fun getModels(sort: String?): OpenRouterResponse {
            return OpenRouterResponse(data = FallbackSeedData.seedModels)
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialLoadAndFiltering() = runTest(testDispatcher) {
        val repository = ModelRepository(fakeApi, fakeDao)
        val viewModel = LeaderboardViewModel(repository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.allModels.isNotEmpty())
        assertEquals(state.allModels.size, state.filteredModels.size)

        // Test search query
        viewModel.setSearchQuery("claude")
        val searchedState = viewModel.uiState.value
        assertTrue(searchedState.filteredModels.all { it.name.contains("Claude", ignoreCase = true) || it.id.contains("claude", ignoreCase = true) })

        // Clear search
        viewModel.setSearchQuery("")
        assertEquals(state.allModels.size, viewModel.uiState.value.filteredModels.size)

        // Test category filter
        viewModel.setCategoryFilter(CategoryFilter.OPEN_WEIGHT)
        val openWeightState = viewModel.uiState.value
        assertTrue(openWeightState.filteredModels.all { it.isOpenWeight })

        // Test sort order
        viewModel.setSortOrder(SortOrder.PRICE_ASC)
        val sortedState = viewModel.uiState.value
        val prices = sortedState.filteredModels.map { it.promptPricePerMillion }
        assertEquals(prices.sorted(), prices)

        // Test Model Selection for detail
        val firstModel = state.allModels.first()
        viewModel.selectModelForDetail(firstModel)
        assertEquals(firstModel, viewModel.uiState.value.selectedModelForDetail)

        // Test compare swap
        val modelA = state.allModels[0]
        val modelB = state.allModels[1]
        viewModel.selectCompareModelA(modelA)
        viewModel.selectCompareModelB(modelB)
        assertEquals(modelA, viewModel.uiState.value.compareModelA)
        assertEquals(modelB, viewModel.uiState.value.compareModelB)

        viewModel.swapCompareModels()
        assertEquals(modelB, viewModel.uiState.value.compareModelA)
        assertEquals(modelA, viewModel.uiState.value.compareModelB)
    }
}
