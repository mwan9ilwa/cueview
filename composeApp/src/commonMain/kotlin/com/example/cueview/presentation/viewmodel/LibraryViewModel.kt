package com.example.cueview.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cueview.domain.model.*
import com.example.cueview.domain.repository.AuthRepository
import com.example.cueview.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LibraryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val canRetry: Boolean = false,
    val retryAction: (() -> Unit)? = null
)

/**
 * ViewModel for the Library screen
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUser = authRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    val userShows = _currentUser.flatMapLatest { user ->
        if (user?.id != null) {
            userRepository.getUserShows(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter shows by status
    val watchingShows = userShows.map { shows ->
        shows.filter { it.status == WatchStatus.WATCHING }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedShows = userShows.map { shows ->
        shows.filter { it.status == WatchStatus.COMPLETED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val planToWatchShows = userShows.map { shows ->
        shows.filter { it.status == WatchStatus.PLAN_TO_WATCH }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val onHoldShows = userShows.map { shows ->
        shows.filter { it.status == WatchStatus.ON_HOLD }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val droppedShows = userShows.map { shows ->
        shows.filter { it.status == WatchStatus.DROPPED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeShowFromLibrary(show: UserShow, retryCount: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            _currentUser.value?.let { user ->
                userRepository.removeShowFromLibrary(user.id, show.showId).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "'${show.showName}' removed from library"
                        )
                    },
                    onFailure = { error ->
                        val canRetry = retryCount < 3
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = getErrorMessage(error, retryCount, "Failed to remove show"),
                            canRetry = canRetry,
                            retryAction = if (canRetry) {
                                { removeShowFromLibrary(show, retryCount + 1) }
                            } else null
                        )
                    }
                )
            }
        }
    }

    fun updateShowStatus(show: UserShow, newStatus: WatchStatus, retryCount: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            _currentUser.value?.let { user ->
                userRepository.updateShowStatus(user.id, show.showId, newStatus).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Status updated to ${newStatus.name.replace('_', ' ')}"
                        )
                    },
                    onFailure = { error ->
                        val canRetry = retryCount < 3
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = getErrorMessage(error, retryCount, "Failed to update status"),
                            canRetry = canRetry,
                            retryAction = if (canRetry) {
                                { updateShowStatus(show, newStatus, retryCount + 1) }
                            } else null
                        )
                    }
                )
            }
        }
    }

    fun markEpisodeWatched(show: UserShow, season: Int, episode: Int, retryCount: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            _currentUser.value?.let { user ->
                userRepository.markEpisodeWatched(user.id, show.showId, season, episode).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Episode S${season}E${episode} marked as watched"
                        )
                    },
                    onFailure = { error ->
                        val canRetry = retryCount < 3
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = getErrorMessage(error, retryCount, "Failed to mark episode as watched"),
                            canRetry = canRetry,
                            retryAction = if (canRetry) {
                                { markEpisodeWatched(show, season, episode, retryCount + 1) }
                            } else null
                        )
                    }
                )
            }
        }
    }

    fun markSeasonCompleted(show: UserShow, season: Int, retryCount: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            _currentUser.value?.let { user ->
                userRepository.markSeasonCompleted(user.id, show.showId, season).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Season $season completed!"
                        )
                    },
                    onFailure = { error ->
                        val canRetry = retryCount < 3
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = getErrorMessage(error, retryCount, "Failed to complete season"),
                            canRetry = canRetry,
                            retryAction = if (canRetry) {
                                { markSeasonCompleted(show, season, retryCount + 1) }
                            } else null
                        )
                    }
                )
            }
        }
    }
    
    fun updateShowProgress(show: UserShow, currentSeason: Int, currentEpisode: Int) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                userRepository.updateShowProgress(user.id, show.showId, currentSeason, currentEpisode)
            }
        }
    }

    fun rateShow(show: UserShow, rating: Double) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                userRepository.rateShow(user.id, show.showId, rating)
            }
        }
    }
    
    fun retry() {
        _uiState.value.retryAction?.invoke()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null,
            canRetry = false,
            retryAction = null
        )
    }
    
    private fun getErrorMessage(error: Throwable, retryCount: Int, prefix: String): String {
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
}
