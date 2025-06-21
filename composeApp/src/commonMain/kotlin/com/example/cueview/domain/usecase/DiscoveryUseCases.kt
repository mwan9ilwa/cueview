package com.example.cueview.domain.usecase

import com.example.cueview.domain.model.*
import com.example.cueview.domain.repository.TvShowRepository
import com.example.cueview.domain.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull

/**
 * Enhanced use cases for content discovery and personalization
 */

data class PersonalizedContent(
    val trending: List<TvShow>,
    val popular: List<TvShow>,
    val topRated: List<TvShow>,
    val forYou: List<TvShow>,
    val genres: List<Genre>
)

class GetPersonalizedRecommendationsUseCase(
    private val tvShowRepository: TvShowRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<PersonalizedContent> {
        return try {
            val userProfile = userRepository.getUserProfile(userId).getOrNull()
            val userShows = userRepository.getUserShows(userId).firstOrNull() ?: emptyList()
            val preferences = userProfile?.preferences
            
            // Get trending shows
            val trending = tvShowRepository.getTrendingShows().getOrNull() ?: emptyList()
            
            // Get popular shows
            val popular = tvShowRepository.getPopularShows().getOrNull() ?: emptyList()
            
            // Get top rated shows
            val topRated = tvShowRepository.getTopRatedShows().getOrNull() ?: emptyList()
            
            // Filter based on user preferences
            val filteredTrending = filterByPreferences(trending, preferences, userShows)
            val filteredPopular = filterByPreferences(popular, preferences, userShows)
            val filteredTopRated = filterByPreferences(topRated, preferences, userShows)
            
            // Get genre-based recommendations
            val genreRecommendations = getGenreBasedRecommendations(userShows, preferences)
            
            Result.success(
                PersonalizedContent(
                    trending = filteredTrending.take(10),
                    popular = filteredPopular.take(10),
                    topRated = filteredTopRated.take(10),
                    forYou = genreRecommendations.take(15),
                    genres = tvShowRepository.getGenres().getOrNull() ?: emptyList()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun filterByPreferences(
        shows: List<TvShow>,
        preferences: UserPreferences?,
        userShows: List<UserShow>
    ): List<TvShow> {
        return shows.filter { show ->
            // Don't show already tracked shows
            !userShows.any { it.showId == show.id } &&
            // Filter by preferred genres if set
            (preferences?.preferredGenres?.isEmpty() != false || 
             show.genreIds.any { it in preferences.preferredGenres }) &&
            // Exclude disliked genres
            (preferences?.excludedGenres?.isEmpty() != false || 
             !show.genreIds.any { it in preferences.excludedGenres })
        }
    }
    
    private suspend fun getGenreBasedRecommendations(
        userShows: List<UserShow>,
        preferences: UserPreferences?
    ): List<TvShow> {
        // This is a simplified implementation
        // In a real app, you'd analyze user's watching patterns more deeply
        return tvShowRepository.getPopularShows().getOrNull()?.take(15) ?: emptyList()
    }
}

class GetShowsByGenreUseCase(
    private val tvShowRepository: TvShowRepository
) {
    suspend operator fun invoke(genreId: Int, page: Int = 1): Result<List<TvShow>> {
        return try {
            // This would require a new method in TvShowRepository
            // For now, we'll filter popular shows by genre
            val popularShows = tvShowRepository.getPopularShows(page).getOrNull() ?: emptyList()
            val filteredShows = popularShows.filter { it.genreIds.contains(genreId) }
            Result.success(filteredShows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetWatchlistSuggestionsUseCase(
    private val tvShowRepository: TvShowRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<List<TvShow>> {
        return try {
            val userShows = userRepository.getUserShows(userId).firstOrNull() ?: emptyList()
            
            // Get shows similar to what user is watching
            val watchingShows = userShows.filter { it.status == WatchStatus.WATCHING }
            val suggestions = mutableListOf<TvShow>()
            
            // For each show the user is watching, find similar ones
            for (userShow in watchingShows.take(3)) {
                val showDetails = tvShowRepository.getShowDetails(userShow.showId).getOrNull()
                if (showDetails != null) {
                    // Find shows with similar genres
                    val popularShows = tvShowRepository.getPopularShows().getOrNull() ?: emptyList()
                    val similarShows = popularShows.filter { show ->
                        show.id != userShow.showId &&
                        !userShows.any { it.showId == show.id } &&
                        show.genreIds.any { it in showDetails.genreIds }
                    }.take(3)
                    
                    suggestions.addAll(similarShows)
                }
            }
            
            Result.success(suggestions.distinctBy { it.id }.take(10))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Content filter options
 */
data class DiscoveryFilter(
    val genres: List<Int> = emptyList(),
    val status: List<String> = emptyList(),
    val rating: ClosedFloatingPointRange<Double>? = null,
    val year: IntRange? = null
)
