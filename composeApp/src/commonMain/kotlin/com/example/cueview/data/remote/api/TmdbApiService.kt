package com.example.cueview.data.remote.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class TmdbApiService(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.themoviedb.org/3"
) {
    
    suspend fun getTrendingTvShows(timeWindow: String = "week"): TmdbResponse<TmdbTvShow> {
        return httpClient.get("$baseUrl/trending/tv/$timeWindow") {
            parameter("api_key", apiKey)
        }.body()
    }
    
    suspend fun getPopularTvShows(page: Int = 1): TmdbResponse<TmdbTvShow> {
        return httpClient.get("$baseUrl/tv/popular") {
            parameter("api_key", apiKey)
            parameter("page", page)
        }.body()
    }
    
    suspend fun getTopRatedTvShows(page: Int = 1): TmdbResponse<TmdbTvShow> {
        return httpClient.get("$baseUrl/tv/top_rated") {
            parameter("api_key", apiKey)
            parameter("page", page)
        }.body()
    }
    
    suspend fun searchTvShows(query: String, page: Int = 1): TmdbResponse<TmdbTvShow> {
        return httpClient.get("$baseUrl/search/tv") {
            parameter("api_key", apiKey)
            parameter("query", query)
            parameter("page", page)
        }.body()
    }
    
    suspend fun getTvShowDetails(tvId: Int): TmdbTvShow {
        return httpClient.get("$baseUrl/tv/$tvId") {
            parameter("api_key", apiKey)
        }.body()
    }
    
    suspend fun getTvShowSeason(tvId: Int, seasonNumber: Int): TmdbSeason {
        return httpClient.get("$baseUrl/tv/$tvId/season/$seasonNumber") {
            parameter("api_key", apiKey)
        }.body()
    }
    
    suspend fun getTvShowEpisode(tvId: Int, seasonNumber: Int, episodeNumber: Int): TmdbEpisode {
        return httpClient.get("$baseUrl/tv/$tvId/season/$seasonNumber/episode/$episodeNumber") {
            parameter("api_key", apiKey)
        }.body()
    }
    
    suspend fun getGenres(): List<TmdbGenre> {
        val response: Map<String, List<TmdbGenre>> = httpClient.get("$baseUrl/genre/tv/list") {
            parameter("api_key", apiKey)
        }.body()
        return response["genres"] ?: emptyList()
    }
    
    companion object {
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
        const val POSTER_SIZE_W500 = "w500"
        const val BACKDROP_SIZE_W1280 = "w1280"
        const val STILL_SIZE_W300 = "w300"
        
        fun getImageUrl(path: String?, size: String = POSTER_SIZE_W500): String? {
            return if (path != null) "${IMAGE_BASE_URL}${size}${path}" else null
        }
    }
}
