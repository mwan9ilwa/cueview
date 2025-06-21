package com.example.cueview.data.repository

import com.example.cueview.data.firebase.FirebaseService
import com.example.cueview.domain.model.*
import com.example.cueview.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * User Repository Implementation for Firestore operations
 */
class UserRepositoryImpl(
    private val firebaseService: FirebaseService
) : UserRepository {
    
    override suspend fun getUserProfile(userId: String): Result<UserProfile?> {
        return firebaseService.getUserProfile(userId)
    }

    override suspend fun updateUserProfile(profile: UserProfile): Result<Unit> {
        return firebaseService.updateUserProfile(profile)
    }

    override fun getUserShows(userId: String): Flow<List<UserShow>> {
        return firebaseService.getUserShows(userId)
    }

    override suspend fun addShowToLibrary(userShow: UserShow): Result<Unit> {
        return firebaseService.addShowToLibrary(userShow)
    }

    override suspend fun removeShowFromLibrary(userId: String, showId: Int): Result<Unit> {
        return firebaseService.removeShowFromLibrary(userId, showId)
    }

    override suspend fun updateShowProgress(userId: String, showId: Int, season: Int, episode: Int): Result<Unit> {
        return try {
            firebaseService.updateShowProgress(userId, showId, season, episode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateShowStatus(userId: String, showId: Int, status: WatchStatus): Result<Unit> {
        return try {
            firebaseService.updateShowStatus(userId, showId, status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addWatchedEpisode(userId: String, showId: Int, episode: WatchedEpisode): Result<Unit> {
        return try {
            firebaseService.addWatchedEpisode(userId, showId, episode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rateShow(userId: String, showId: Int, rating: Double): Result<Unit> {
        return try {
            firebaseService.rateShow(userId, showId, rating)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addShowNotes(userId: String, showId: Int, notes: String): Result<Unit> {
        return try {
            firebaseService.addShowNotes(userId, showId, notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markEpisodeWatched(userId: String, showId: Int, season: Int, episode: Int): Result<Unit> {
        return try {
            // Create a WatchedEpisode object and add it to the show
            val watchedEpisode = WatchedEpisode(
                seasonNumber = season,
                episodeNumber = episode,
                watchedDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
            )
            
            // Add the episode to watched list and update current progress
            firebaseService.addWatchedEpisode(userId, showId, watchedEpisode).fold(
                onSuccess = {
                    // Update current progress to next episode
                    firebaseService.updateShowProgress(userId, showId, season, episode + 1)
                },
                onFailure = { error -> Result.failure(error) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markSeasonCompleted(userId: String, showId: Int, season: Int): Result<Unit> {
        return try {
            // Mark the season as completed and move to next season
            firebaseService.markSeasonCompleted(userId, showId, season).fold(
                onSuccess = {
                    // Update progress to next season, episode 1
                    firebaseService.updateShowProgress(userId, showId, season + 1, 1)
                },
                onFailure = { error -> Result.failure(error) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
