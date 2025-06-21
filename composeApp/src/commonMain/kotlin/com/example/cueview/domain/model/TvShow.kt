package com.example.cueview.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class TvShow(
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val firstAirDate: LocalDate?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val genreIds: List<Int>,
    val originalLanguage: String,
    val originalName: String,
    val status: String? = null,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
    val inProduction: Boolean? = null,
    val networks: List<Network>? = null,
    val createdBy: List<Creator>? = null,
    val genres: List<Genre>? = null,
    val seasons: List<Season>? = null
)

@Serializable
data class Season(
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val seasonNumber: Int,
    val episodeCount: Int,
    val airDate: LocalDate?
)

@Serializable
data class Episode(
    val id: Int,
    val name: String,
    val overview: String,
    val stillPath: String?,
    val episodeNumber: Int,
    val seasonNumber: Int,
    val airDate: LocalDate?,
    val voteAverage: Double,
    val voteCount: Int,
    val runtime: Int?
)

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

@Serializable
data class Network(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val originCountry: String
)

@Serializable
data class Creator(
    val id: Int,
    val name: String,
    val profilePath: String?
)

@Serializable
data class ShowImages(
    val backdrops: List<ImageData>,
    val posters: List<ImageData>
)

@Serializable
data class ImageData(
    val filePath: String,
    val width: Int,
    val height: Int,
    val aspectRatio: Double,
    val voteAverage: Double,
    val voteCount: Int
)

@Serializable
data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val official: Boolean
)
