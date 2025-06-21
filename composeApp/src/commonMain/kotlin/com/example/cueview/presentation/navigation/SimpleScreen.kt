package com.example.cueview.presentation.navigation

/**
 * Screen routes for navigation
 * This is a simplified version to avoid platform-specific navigation dependencies
 * In a real app, you would use proper navigation libraries like:
 * - Android: Jetpack Navigation Compose
 * - iOS: UINavigationController or SwiftUI Navigation
 */

sealed class Screen(val route: String) {
    data object Discover : Screen("discover")
    data object Library : Screen("library") 
    data object Profile : Screen("profile")
    data object Auth : Screen("auth")
    data class ShowDetail(val showId: Int) : Screen("show_detail/$showId") {
        companion object {
            const val route = "show_detail/{showId}"
        }
    }
    data class SeasonDetail(val showId: Int, val seasonNumber: Int) : Screen("season_detail/$showId/$seasonNumber") {
        companion object {
            const val route = "season_detail/{showId}/{seasonNumber}"
        }
    }
    data class EpisodeDetail(val showId: Int, val seasonNumber: Int, val episodeNumber: Int) : 
        Screen("episode_detail/$showId/$seasonNumber/$episodeNumber") {
        companion object {
            const val route = "episode_detail/{showId}/{seasonNumber}/{episodeNumber}"
        }
    }
}
