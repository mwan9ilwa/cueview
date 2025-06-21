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
    val isAddingToLibrary: Boolean = false,
    val canRetry: Boolean = false,
    val retryAction: (() -> Unit)? = null
)

class ShowDetailViewModel(
    private val getShowDetailsUseCase: GetShowDetailsUseCase,
    private val addShowToLibraryUseCase: AddShowToLibraryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowDetailUiState())
    val uiState: StateFlow<ShowDetailUiState> = _uiState.asStateFlow()

    fun loadShowDetails(showId: Int, retryCount: Int = 0) {
        viewModelScope.launch {
            // Clear previous show data and set loading state
            _uiState.value = _uiState.value.copy(
                isLoading = true, 
                show = null, 
                errorMessage = null,
                addToLibraryMessage = null,
                canRetry = false,
                retryAction = null
            )
            
            getShowDetailsUseCase(showId).fold(
                onSuccess = { show ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        show = show
                    )
                },
                onFailure = { error ->
                    val canRetry = retryCount < 3
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(error, retryCount),
                        canRetry = canRetry,
                        retryAction = if (canRetry) {
                            { loadShowDetails(showId, retryCount + 1) }
                        } else null
                    )
                }
            )
        }
    }

    fun addToLibrary(show: TvShow, retryCount: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAddingToLibrary = true,
                errorMessage = null,
                canRetry = false,
                retryAction = null
            )
            
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
                        val canRetry = retryCount < 3
                        _uiState.value = _uiState.value.copy(
                            isAddingToLibrary = false,
                            errorMessage = getErrorMessage(error, retryCount, "Failed to add show to library"),
                            canRetry = canRetry,
                            retryAction = if (canRetry) {
                                { addToLibrary(show, retryCount + 1) }
                            } else null
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

    fun retry() {
        _uiState.value.retryAction?.invoke()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            addToLibraryMessage = null,
            canRetry = false,
            retryAction = null
        )
    }
    
    private fun getErrorMessage(error: Throwable, retryCount: Int, prefix: String = "Failed to load show details"): String {
        return when {
            error.message?.contains("network", ignoreCase = true) == true -> {
                if (retryCount < 3) "$prefix. Check your internet connection. (Attempt ${retryCount + 1}/3)"
                else "$prefix. Please check your internet connection and try again."
            }
            error.message?.contains("timeout", ignoreCase = true) == true -> {
                if (retryCount < 3) "$prefix. Request timed out. (Attempt ${retryCount + 1}/3)"
                else "$prefix. The request timed out. Please try again later."
            }
            else -> {
                if (retryCount < 3) "$prefix: ${error.message ?: "Unknown error"} (Attempt ${retryCount + 1}/3)"
                else "$prefix: ${error.message ?: "Unknown error"}"
            }
        }
    }

    fun clearAddToLibraryMessage() {
        _uiState.value = _uiState.value.copy(addToLibraryMessage = null)
    }
}
