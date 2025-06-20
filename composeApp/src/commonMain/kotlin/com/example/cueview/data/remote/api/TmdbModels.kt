package com.example.cueview.data.remote.api

import com.example.cueview.domain.model.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbResponse<T>(
    val page: Int,
    @SerialName("total_results")
    val totalResults: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    val results: List<T>
)

@Serializable
data class TmdbTvShow(
    val id: Int,
    val name: String,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("first_air_date")
    val firstAirDate: String?,
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerialName("vote_count")
    val voteCount: Int,
    val popularity: Double,
    @SerialName("genre_ids")
    val genreIds: List<Int>,
    @SerialName("original_language")
    val originalLanguage: String,
    @SerialName("original_name")
    val originalName: String,
    val status: String? = null,
    @SerialName("number_of_seasons")
    val numberOfSeasons: Int? = null,
    @SerialName("number_of_episodes")
    val numberOfEpisodes: Int? = null,
    @SerialName("in_production")
    val inProduction: Boolean? = null,
    val networks: List<TmdbNetwork>? = null,
    @SerialName("created_by")
    val createdBy: List<TmdbCreator>? = null,
    val genres: List<TmdbGenre>? = null,
    val seasons: List<TmdbSeason>? = null
)

@Serializable
data class TmdbSeason(
    val id: Int,
    val name: String,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("season_number")
    val seasonNumber: Int,
    @SerialName("episode_count")
    val episodeCount: Int,
    @SerialName("air_date")
    val airDate: String?
)

@Serializable
data class TmdbEpisode(
    val id: Int,
    val name: String,
    val overview: String,
    @SerialName("still_path")
    val stillPath: String?,
    @SerialName("episode_number")
    val episodeNumber: Int,
    @SerialName("season_number")
    val seasonNumber: Int,
    @SerialName("air_date")
    val airDate: String?,
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerialName("vote_count")
    val voteCount: Int,
    val runtime: Int?
)

@Serializable
data class TmdbGenre(
    val id: Int,
    val name: String
)

@Serializable
data class TmdbNetwork(
    val id: Int,
    val name: String,
    @SerialName("logo_path")
    val logoPath: String?,
    @SerialName("origin_country")
    val originCountry: String
)

@Serializable
data class TmdbCreator(
    val id: Int,
    val name: String,
    @SerialName("profile_path")
    val profilePath: String?
)
