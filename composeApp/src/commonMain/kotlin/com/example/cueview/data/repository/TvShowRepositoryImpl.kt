package com.example.cueview.data.repository

import com.example.cueview.data.remote.api.TmdbApiService
import com.example.cueview.data.remote.api.toDomainModel
import com.example.cueview.domain.model.*
import com.example.cueview.domain.repository.TvShowRepository

class TvShowRepositoryImpl(
    private val tmdbApiService: TmdbApiService
) : TvShowRepository {

    // Simple in-memory cache for genres and shows
    // TODO: Replace with proper local database when SQLDelight is added back
    private val genreCache = mutableMapOf<String, List<Genre>>()
    private val showCache = mutableMapOf<Int, TvShow>()

    override suspend fun getTrendingShows(): Result<List<TvShow>> {
        return try {
            val response = tmdbApiService.getTrendingTvShows()
            val shows = response.results.map { it.toDomainModel() }
            Result.success(shows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPopularShows(page: Int): Result<List<TvShow>> {
        return try {
            val response = tmdbApiService.getPopularTvShows(page)
            val shows = response.results.map { it.toDomainModel() }
            Result.success(shows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTopRatedShows(page: Int): Result<List<TvShow>> {
        return try {
            val response = tmdbApiService.getTopRatedTvShows(page)
            val shows = response.results.map { it.toDomainModel() }
            Result.success(shows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchShows(query: String, page: Int): Result<List<TvShow>> {
        return try {
            val response = tmdbApiService.searchTvShows(query, page)
            val shows = response.results.map { it.toDomainModel() }
            Result.success(shows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShowDetails(showId: Int): Result<TvShow> {
        return try {
            val response = tmdbApiService.getTvShowDetails(showId)
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShowSeason(showId: Int, seasonNumber: Int): Result<Season> {
        return try {
            val response = tmdbApiService.getTvShowSeason(showId, seasonNumber)
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShowEpisode(showId: Int, seasonNumber: Int, episodeNumber: Int): Result<Episode> {
        return try {
            val response = tmdbApiService.getTvShowEpisode(showId, seasonNumber, episodeNumber)
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = tmdbApiService.getGenres()
            val genres = response.map { it.toDomainModel() }
            Result.success(genres)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShowsByGenre(genreId: Int, page: Int): Result<List<TvShow>> {
        return try {
            val response = tmdbApiService.getTvShowsByGenre(genreId, page)
            val shows = response.results.map { it.toDomainModel() }
            Result.success(shows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSimilarShows(showId: Int): Result<List<TvShow>> {
        return try {
            val response = tmdbApiService.getSimilarTvShows(showId)
            val shows = response.results.map { it.toDomainModel() }
            Result.success(shows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecommendations(showId: Int): Result<List<TvShow>> {
        return try {
            val response = tmdbApiService.getTvShowRecommendations(showId)
            val shows = response.results.map { it.toDomainModel() }
            Result.success(shows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShowImages(showId: Int): Result<ShowImages> {
        return try {
            val response = tmdbApiService.getTvShowImages(showId)
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShowVideos(showId: Int): Result<List<Video>> {
        return try {
            val response = tmdbApiService.getTvShowVideos(showId)
            val videos = response.results.map { it.toDomainModel() }
            Result.success(videos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // TODO: Implement local caching with SQLDelight when we add it back
    override suspend fun getCachedShow(showId: Int): TvShow? {
        return showCache[showId]
    }

    override suspend fun cacheShow(show: TvShow) {
        showCache[show.id] = show
    }

    override suspend fun getCachedGenres(): List<Genre> {
        // Simple implementation without time-based expiry for now
        return genreCache["genres"] ?: emptyList()
    }

    override suspend fun cacheGenres(genres: List<Genre>) {
        genreCache["genres"] = genres
    }
}
