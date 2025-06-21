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
    val watchedEpisodes: List<WatchedEpisode> = emptyList(),
    val totalSeasons: Int? = null,
    val totalEpisodes: Int? = null,
    val nextEpisodeAirDate: LocalDate? = null,
    val isCompleted: Boolean = false,
    val reminderEnabled: Boolean = false
)

@Serializable
data class WatchedEpisode(
    val seasonNumber: Int,
    val episodeNumber: Int,
    val watchedDate: LocalDate,
    val rating: Double? = null,
    val notes: String? = null
)

@Serializable
data class SeasonProgress(
    val seasonNumber: Int,
    val totalEpisodes: Int,
    val watchedEpisodes: List<Int> = emptyList(),
    val isCompleted: Boolean = false
) {
    val progress: Float
        get() = if (totalEpisodes > 0) watchedEpisodes.size.toFloat() / totalEpisodes else 0f
    
    val watchedCount: Int
        get() = watchedEpisodes.size
}

@Serializable
data class ShowReminder(
    val id: String,
    val userId: String,
    val showId: Int,
    val showName: String,
    val reminderType: ReminderType,
    val scheduledDate: LocalDate,
    val isActive: Boolean = true,
    val createdDate: LocalDate
)

enum class ReminderType {
    NEW_EPISODE,
    NEW_SEASON,
    FINALE,
    CUSTOM
}

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
    val theme: String = "system", // system, light, dark
    val preferredGenres: List<Int> = emptyList(),
    val excludedGenres: List<Int> = emptyList(),
    val autoMarkWatched: Boolean = false,
    val reminderOffset: Int = 24, // hours before air date
    val showAdultContent: Boolean = false,
    val preferredStreamingServices: List<String> = emptyList()
)
