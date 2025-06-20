package com.example.cueview.data.remote.api

import com.example.cueview.domain.model.*
import kotlinx.datetime.LocalDate

/**
 * Extension functions to map TMDb API responses to domain models
 */

fun TmdbTvShow.toDomainModel(): TvShow {
    return TvShow(
        id = id,
        name = name,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        firstAirDate = firstAirDate?.toLocalDateOrNull(),
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        genreIds = genreIds,
        originalLanguage = originalLanguage,
        originalName = originalName,
        status = status,
        numberOfSeasons = numberOfSeasons,
        numberOfEpisodes = numberOfEpisodes,
        inProduction = inProduction,
        networks = networks?.map { it.toDomainModel() },
        createdBy = createdBy?.map { it.toDomainModel() },
        genres = genres?.map { it.toDomainModel() },
        seasons = seasons?.map { it.toDomainModel() }
    )
}

fun TmdbSeason.toDomainModel(): Season {
    return Season(
        id = id,
        name = name,
        overview = overview,
        posterPath = posterPath,
        seasonNumber = seasonNumber,
        episodeCount = episodeCount,
        airDate = airDate?.toLocalDateOrNull()
    )
}

fun TmdbEpisode.toDomainModel(): Episode {
    return Episode(
        id = id,
        name = name,
        overview = overview,
        stillPath = stillPath,
        episodeNumber = episodeNumber,
        seasonNumber = seasonNumber,
        airDate = airDate?.toLocalDateOrNull(),
        voteAverage = voteAverage,
        voteCount = voteCount,
        runtime = runtime
    )
}

fun TmdbGenre.toDomainModel(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun TmdbNetwork.toDomainModel(): Network {
    return Network(
        id = id,
        name = name,
        logoPath = logoPath,
        originCountry = originCountry
    )
}

fun TmdbCreator.toDomainModel(): Creator {
    return Creator(
        id = id,
        name = name,
        profilePath = profilePath
    )
}

/**
 * Helper function to safely parse date strings from TMDb API
 * TMDb uses YYYY-MM-DD format
 */
fun String?.toLocalDateOrNull(): LocalDate? {
    return try {
        if (this.isNullOrBlank()) null
        else LocalDate.parse(this)
    } catch (e: Exception) {
        null
    }
}
