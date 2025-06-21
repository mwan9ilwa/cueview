package com.example.cueview.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cueview.domain.model.TvShow
import com.example.cueview.domain.model.UserShow
import com.example.cueview.domain.model.WatchStatus
import com.example.cueview.domain.usecase.AddShowToLibraryUseCase
import com.example.cueview.domain.usecase.GetCurrentUserUseCase
import com.example.cueview.domain.usecase.GetShowDetailsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class ShowDetailUiState(
    val isLoading: Boolean = false,
    val show: TvShow? = null,
    val errorMessage: String? = null,
    val addToLibraryMessage: String? = null,
    val isAddingToLibrary: Boolean = false
)

class ShowDetailViewModel(
    private val getShowDetailsUseCase: GetShowDetailsUseCase,
    private val addShowToLibraryUseCase: AddShowToLibraryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowDetailUiState())
    val uiState: StateFlow<ShowDetailUiState> = _uiState.asStateFlow()

    fun loadShowDetails(showId: Int) {
        viewModelScope.launch {
            // Clear previous show data and set loading state
            _uiState.value = _uiState.value.copy(
                isLoading = true, 
                show = null, 
                errorMessage = null,
                addToLibraryMessage = null
            )
            
            getShowDetailsUseCase(showId).fold(
                onSuccess = { show ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        show = show
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load show details"
                    )
                }
            )
        }
    }

    fun addToLibrary(show: TvShow) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingToLibrary = true)
            
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
                    personalNotes = null,
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearAddToLibraryMessage() {
        _uiState.value = _uiState.value.copy(addToLibraryMessage = null)
    }
}
