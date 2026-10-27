package com.pulseai.leaderboard.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pulseai.leaderboard.data.repository.ModelRepository
import com.pulseai.leaderboard.domain.model.AiModel
import com.pulseai.leaderboard.domain.model.CategoryFilter
import com.pulseai.leaderboard.domain.model.Resource
import com.pulseai.leaderboard.domain.model.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class LeaderboardViewModel(
    private val repository: ModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadModels(forceRefresh = false)
    }

    fun loadModels(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !forceRefresh, isRefreshing = forceRefresh, errorMessage = null) }
            repository.getModelsStream(forceRefresh = forceRefresh).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val models = resource.data
                        _uiState.update { current ->
                            val filtered = computeFilteredModels(
                                models = models,
                                query = current.searchQuery,
                                category = current.selectedCategory,
                                sort = current.selectedSortOrder
                            )
                            val compA = current.compareModelA ?: filtered.firstOrNull()
                            val compB = current.compareModelB ?: filtered.getOrNull(1)
                            current.copy(
                                isLoading = false,
                                isRefreshing = false,
                                isOffline = resource.isOffline,
                                allModels = models,
                                filteredModels = filtered,
                                compareModelA = compA,
                                compareModelB = compB
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            val filtered = computeFilteredModels(
                models = current.allModels,
                query = query,
                category = current.selectedCategory,
                sort = current.selectedSortOrder
            )
            current.copy(searchQuery = query, filteredModels = filtered)
        }
    }

    fun setCategoryFilter(category: CategoryFilter) {
        _uiState.update { current ->
            val filtered = computeFilteredModels(
                models = current.allModels,
                query = current.searchQuery,
                category = category,
                sort = current.selectedSortOrder
            )
            current.copy(selectedCategory = category, filteredModels = filtered)
        }
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _uiState.update { current ->
            val filtered = computeFilteredModels(
                models = current.allModels,
                query = current.searchQuery,
                category = current.selectedCategory,
                sort = sortOrder
            )
            current.copy(selectedSortOrder = sortOrder, filteredModels = filtered)
        }
    }

    fun refresh() {
        loadModels(forceRefresh = true)
    }

    fun selectModelForDetail(model: AiModel?) {
        _uiState.update { it.copy(selectedModelForDetail = model) }
    }

    fun selectCompareModelA(model: AiModel?) {
        _uiState.update { it.copy(compareModelA = model) }
    }

    fun selectCompareModelB(model: AiModel?) {
        _uiState.update { it.copy(compareModelB = model) }
    }

    fun swapCompareModels() {
        _uiState.update {
            it.copy(
                compareModelA = it.compareModelB,
                compareModelB = it.compareModelA
            )
        }
    }

    fun setMonthlyVolumeTokens(tokens: Long) {
        _uiState.update { it.copy(monthlyVolumeTokens = tokens.coerceIn(1_000_000L, 100_000_000L)) }
    }

    private fun computeFilteredModels(
        models: List<AiModel>,
        query: String,
        category: CategoryFilter,
        sort: SortOrder
    ): List<AiModel> {
        val trimmedQuery = query.trim().lowercase(Locale.ROOT)

        // 1. Search Query filter
        val searchFiltered = if (trimmedQuery.isEmpty()) {
            models
        } else {
            models.filter { model ->
                model.name.lowercase(Locale.ROOT).contains(trimmedQuery) ||
                model.id.lowercase(Locale.ROOT).contains(trimmedQuery) ||
                model.provider.lowercase(Locale.ROOT).contains(trimmedQuery) ||
                model.description.lowercase(Locale.ROOT).contains(trimmedQuery)
            }
        }

        // 2. Category filter
        val categoryFiltered = when (category) {
            CategoryFilter.OVERALL -> searchFiltered
            CategoryFilter.CODING -> searchFiltered
            CategoryFilter.REASONING -> searchFiltered
            CategoryFilter.VALUE -> searchFiltered
            CategoryFilter.SPEED -> searchFiltered
            CategoryFilter.CONTEXT_1M -> searchFiltered.filter { it.contextLength >= 1_000_000 }
            CategoryFilter.OPEN_WEIGHT -> searchFiltered.filter { it.isOpenWeight }
            CategoryFilter.PROPRIETARY -> searchFiltered.filter { !it.isOpenWeight }
        }

        // 3. Sorting
        return when (sort) {
            SortOrder.SCORE_DESC -> {
                when (category) {
                    CategoryFilter.CODING -> categoryFiltered.sortedByDescending { it.codingScore }
                    CategoryFilter.REASONING -> categoryFiltered.sortedByDescending { it.reasoningScore }
                    CategoryFilter.VALUE -> categoryFiltered.sortedByDescending { it.valueIndex }
                    CategoryFilter.SPEED -> categoryFiltered.sortedByDescending { it.speedTps }
                    else -> categoryFiltered.sortedByDescending { it.intelligenceScore }
                }
            }
            SortOrder.PRICE_ASC -> categoryFiltered.sortedBy { it.promptPricePerMillion }
            SortOrder.SPEED_DESC -> categoryFiltered.sortedByDescending { it.speedTps }
            SortOrder.CONTEXT_DESC -> categoryFiltered.sortedByDescending { it.contextLength }
            SortOrder.VALUE_DESC -> categoryFiltered.sortedByDescending { it.valueIndex }
            SortOrder.NAME_ASC -> categoryFiltered.sortedBy { it.name.lowercase(Locale.ROOT) }
        }
    }

    class Factory(private val repository: ModelRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
                return LeaderboardViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
