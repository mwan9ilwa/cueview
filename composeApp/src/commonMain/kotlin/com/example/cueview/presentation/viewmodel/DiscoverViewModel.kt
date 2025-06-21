package com.example.cueview.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cueview.domain.model.TvShow
import com.example.cueview.domain.model.UserShow
import com.example.cueview.domain.model.WatchStatus
import com.example.cueview.domain.usecase.AddShowToLibraryUseCase
import com.example.cueview.domain.usecase.GetCurrentUserUseCase
import com.example.cueview.domain.usecase.GetPopularShowsUseCase
import com.example.cueview.domain.usecase.GetTrendingShowsUseCase
import com.example.cueview.domain.usecase.SearchShowsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class DiscoverUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<TvShow> = emptyList(),
    val trendingShows: List<TvShow> = emptyList(),
    val popularShows: List<TvShow> = emptyList(),
    val errorMessage: String? = null,
    val addToLibraryMessage: String? = null,
    val isAddingToLibrary: Boolean = false
)

class DiscoverViewModel(
    private val getTrendingShowsUseCase: GetTrendingShowsUseCase,
    private val getPopularShowsUseCase: GetPopularShowsUseCase,
    private val searchShowsUseCase: SearchShowsUseCase,
    private val addShowToLibraryUseCase: AddShowToLibraryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
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

    fun clearAddToLibraryMessage() {
        _uiState.value = _uiState.value.copy(addToLibraryMessage = null)
    }

    fun addToLibrary(show: TvShow) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingToLibrary = true)
            
            // Get current user from auth state
            getCurrentUserUseCase().first { it != null }?.let { currentUser ->
                val userShow = UserShow(
                    id = "", // Firestore will generate this
                    userId = currentUser.id,
                    showId = show.id,
                    showName = show.name,
                    posterPath = show.posterPath,
                    status = WatchStatus.PLAN_TO_WATCH,
                    currentSeason = 1,
                    currentEpisode = 1,
                    personalRating = null,
                    personalNotes = "",
                    dateAdded = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    lastWatched = null,
                    watchedEpisodes = emptyList()
                )
                
                addShowToLibraryUseCase(userShow).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isAddingToLibrary = false,
                            addToLibraryMessage = "'${show.name}' added to your library!"
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isAddingToLibrary = false,
                            errorMessage = "Failed to add show to library: ${error.message}"
                        )
                    }
                )
            } ?: run {
                _uiState.value = _uiState.value.copy(
                    isAddingToLibrary = false,
                    errorMessage = "Please sign in to add shows to your library"
                )
            }
        }
    }
}
