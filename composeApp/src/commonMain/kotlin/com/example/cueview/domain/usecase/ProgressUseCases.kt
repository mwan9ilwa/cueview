package com.example.cueview.domain.usecase

import com.example.cueview.domain.model.*
import com.example.cueview.domain.repository.UserRepository
import com.example.cueview.domain.repository.TvShowRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Enhanced use cases for progress tracking and episode management
 */

class MarkEpisodeWatchedUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String, 
        showId: Int, 
        seasonNumber: Int, 
        episodeNumber: Int,
        rating: Double? = null,
        notes: String? = null
    ): Result<Unit> {
        val watchedEpisode = WatchedEpisode(
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            watchedDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            rating = rating,
            notes = notes
        )
        return userRepository.addWatchedEpisode(userId, showId, watchedEpisode)
    }
}

class GetShowProgressUseCase(
    private val userRepository: UserRepository,
    private val tvShowRepository: TvShowRepository
) {
    suspend operator fun invoke(userId: String, showId: Int): Result<ShowProgress> {
        return try {
            val userShows = userRepository.getUserShows(userId).firstOrNull() ?: emptyList()
            val userShow = userShows.firstOrNull { it.showId == showId }
                ?: return Result.failure(Exception("Show not found in user library"))
            
            val showDetails = tvShowRepository.getShowDetails(showId).getOrThrow()
            
            val totalEpisodes = showDetails.numberOfEpisodes ?: 0
            val watchedEpisodesCount = userShow.watchedEpisodes.size
            val progressPercentage = if (totalEpisodes > 0) {
                (watchedEpisodesCount.toFloat() / totalEpisodes) * 100
            } else 0f
            
            val seasonProgress = showDetails.seasons?.map { season ->
                val watchedInSeason = userShow.watchedEpisodes
                    .filter { it.seasonNumber == season.seasonNumber }
                    .map { it.episodeNumber }
                
                SeasonProgress(
                    seasonNumber = season.seasonNumber,
                    totalEpisodes = season.episodeCount,
                    watchedEpisodes = watchedInSeason,
                    isCompleted = watchedInSeason.size == season.episodeCount
                )
            } ?: emptyList()
            
            Result.success(
                ShowProgress(
                    showId = showId,
                    totalEpisodes = totalEpisodes,
                    watchedEpisodes = userShow.watchedEpisodes,
                    progressPercentage = progressPercentage,
                    seasonProgress = seasonProgress,
                    currentSeason = userShow.currentSeason,
                    currentEpisode = userShow.currentEpisode,
                    status = userShow.status
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetRecommendationsUseCase(
    private val tvShowRepository: TvShowRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<List<TvShow>> {
        return try {
            val userShows = userRepository.getUserShows(userId).firstOrNull() ?: emptyList()
            
            if (userShows.isEmpty()) {
                return Result.success(emptyList())
            }
            
            val favoriteGenres = mutableListOf<Int>()
            
            // Get genre preferences from user shows
            for (userShow in userShows) {
                val showDetails = tvShowRepository.getShowDetails(userShow.showId).getOrNull()
                showDetails?.let { 
                    favoriteGenres.addAll(it.genreIds)
                }
            }
            
            val genreCounts = favoriteGenres.groupingBy { it }.eachCount()
            val topGenres = genreCounts.toList()
                .sortedByDescending { it.second }
                .take(3)
                .map { it.first }
            
            // Get popular shows in user's favorite genres
            val recommendations = mutableListOf<TvShow>()
            val popularShows = tvShowRepository.getPopularShows().getOrNull() ?: emptyList()
            
            recommendations.addAll(
                popularShows.filter { show ->
                    show.genreIds.any { it in topGenres } &&
                    !userShows.any { userShow -> userShow.showId == show.id }
                }.take(10)
            )
            
            // Add trending shows as fallback
            if (recommendations.size < 10) {
                val trendingShows = tvShowRepository.getTrendingShows().getOrNull() ?: emptyList()
                recommendations.addAll(
                    trendingShows.filter { show ->
                        !userShows.any { userShow -> userShow.showId == show.id } &&
                        !recommendations.any { it.id == show.id }
                    }.take(10 - recommendations.size)
                )
            }
            
            Result.success(recommendations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SetReminderUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(reminder: ShowReminder): Result<Unit> {
        // This would typically integrate with a notification service
        // For now, we'll store it in the user's preferences or a separate collection
        return try {
            // Implementation would depend on your notification system
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Data class to represent comprehensive show progress
 */
data class ShowProgress(
    val showId: Int,
    val totalEpisodes: Int,
    val watchedEpisodes: List<WatchedEpisode>,
    val progressPercentage: Float,
    val seasonProgress: List<SeasonProgress>,
    val currentSeason: Int,
    val currentEpisode: Int,
    val status: WatchStatus
)
