package com.example.cueview.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cueview.domain.model.TvShow
import com.example.cueview.domain.usecase.GetPopularShowsUseCase
import com.example.cueview.domain.usecase.GetTrendingShowsUseCase
import com.example.cueview.domain.usecase.SearchShowsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DiscoverUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<TvShow> = emptyList(),
    val trendingShows: List<TvShow> = emptyList(),
    val popularShows: List<TvShow> = emptyList(),
    val errorMessage: String? = null
)

class DiscoverViewModel(
    private val getTrendingShowsUseCase: GetTrendingShowsUseCase,
    private val getPopularShowsUseCase: GetPopularShowsUseCase,
    private val searchShowsUseCase: SearchShowsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        if (query.isNotBlank()) {
            searchShows(query)
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
        }
    }

    fun searchShows(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            searchShowsUseCase(query).fold(
                onSuccess = { shows ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        searchResults = shows
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Search failed"
                    )
                }
            )
        }
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Load trending shows
            getTrendingShowsUseCase().fold(
                onSuccess = { shows ->
                    _uiState.value = _uiState.value.copy(trendingShows = shows)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message ?: "Failed to load trending shows"
                    )
                }
            )
            
            // Load popular shows
            getPopularShowsUseCase().fold(
                onSuccess = { shows ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        popularShows = shows
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load popular shows"
                    )
                }
            )
        }
    }

    fun retry() {
        loadInitialData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
