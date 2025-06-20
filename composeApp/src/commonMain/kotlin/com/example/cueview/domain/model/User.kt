package com.example.cueview.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class UserShow(
    val id: String,
    val userId: String,
    val showId: Int,
    val showName: String,
    val posterPath: String?,
    val status: WatchStatus,
    val currentSeason: Int = 1,
    val currentEpisode: Int = 1,
    val personalRating: Double? = null,
    val personalNotes: String? = null,
    val dateAdded: LocalDate,
    val lastWatched: LocalDate? = null,
    val watchedEpisodes: List<WatchedEpisode> = emptyList()
)

@Serializable
data class WatchedEpisode(
    val seasonNumber: Int,
    val episodeNumber: Int,
    val watchedDate: LocalDate,
    val rating: Double? = null
)

enum class WatchStatus {
    WATCHING,
    COMPLETED,
    ON_HOLD,
    DROPPED,
    PLAN_TO_WATCH
}

@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String?,
    val profilePictureUrl: String? = null,
    val joinDate: LocalDate,
    val preferences: UserPreferences = UserPreferences()
)

@Serializable
data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val spoilerProtection: Boolean = true,
    val preferredLanguage: String = "en",
    val theme: String = "system" // system, light, dark
)
