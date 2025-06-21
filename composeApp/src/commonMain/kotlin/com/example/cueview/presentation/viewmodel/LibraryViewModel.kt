package com.example.cueview.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cueview.domain.model.*
import com.example.cueview.domain.repository.AuthRepository
import com.example.cueview.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    fun removeShowFromLibrary(show: UserShow) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                userRepository.removeShowFromLibrary(user.id, show.showId)
            }
        }
    }

    fun updateShowStatus(show: UserShow, newStatus: WatchStatus) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                userRepository.updateShowStatus(user.id, show.showId, newStatus)
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
}
