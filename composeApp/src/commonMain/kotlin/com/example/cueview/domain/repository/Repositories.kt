package com.example.cueview.domain.repository

import com.example.cueview.domain.model.*
import kotlinx.coroutines.flow.Flow

interface TvShowRepository {
    // Remote data (TMDb)
    suspend fun getTrendingShows(): Result<List<TvShow>>
    suspend fun getPopularShows(page: Int = 1): Result<List<TvShow>>
    suspend fun getTopRatedShows(page: Int = 1): Result<List<TvShow>>
    suspend fun searchShows(query: String, page: Int = 1): Result<List<TvShow>>
    suspend fun getShowDetails(showId: Int): Result<TvShow>
    suspend fun getShowSeason(showId: Int, seasonNumber: Int): Result<Season>
    suspend fun getShowEpisode(showId: Int, seasonNumber: Int, episodeNumber: Int): Result<Episode>
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun getShowsByGenre(genreId: Int, page: Int = 1): Result<List<TvShow>>
    suspend fun getSimilarShows(showId: Int): Result<List<TvShow>>
    suspend fun getRecommendations(showId: Int): Result<List<TvShow>>
    suspend fun getShowImages(showId: Int): Result<ShowImages>
    suspend fun getShowVideos(showId: Int): Result<List<Video>>
    
    // Local cached data
    suspend fun getCachedShow(showId: Int): TvShow?
    suspend fun cacheShow(show: TvShow)
    suspend fun getCachedGenres(): List<Genre>
    suspend fun cacheGenres(genres: List<Genre>)
}

interface UserRepository {
    // User profile
    suspend fun getUserProfile(userId: String): Result<UserProfile?>
    suspend fun updateUserProfile(profile: UserProfile): Result<Unit>
    
    // User shows (watchlist/library)
    fun getUserShows(userId: String): Flow<List<UserShow>>
    suspend fun addShowToLibrary(userShow: UserShow): Result<Unit>
    suspend fun removeShowFromLibrary(userId: String, showId: Int): Result<Unit>
    suspend fun updateShowProgress(userId: String, showId: Int, season: Int, episode: Int): Result<Unit>
    suspend fun updateShowStatus(userId: String, showId: Int, status: WatchStatus): Result<Unit>
    suspend fun addWatchedEpisode(userId: String, showId: Int, episode: WatchedEpisode): Result<Unit>
    suspend fun rateShow(userId: String, showId: Int, rating: Double): Result<Unit>
    suspend fun addShowNotes(userId: String, showId: Int, notes: String): Result<Unit>
}

interface AuthRepository {
    fun getCurrentUser(): Flow<UserProfile?>
    suspend fun signInWithEmail(email: String, password: String): Result<UserProfile>
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<UserProfile>
    suspend fun signInWithGoogle(): Result<UserProfile>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
}
