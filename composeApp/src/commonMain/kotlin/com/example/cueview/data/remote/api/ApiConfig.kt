package com.example.cueview.data.remote.api

object ApiConfig {
    // TODO: Replace with your actual TMDb API key from https://www.themoviedb.org/settings/api
    // For now using a placeholder - get your key at: https://www.themoviedb.org/
    const val TMDB_API_KEY = "29231141753f554541606a489eea855a" // Replace this!
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3"
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    
    // Common image sizes
    const val POSTER_SIZE_W342 = "w342"
    const val POSTER_SIZE_W500 = "w500"
    const val BACKDROP_SIZE_W780 = "w780"
    const val BACKDROP_SIZE_W1280 = "w1280"
    
    fun getPosterUrl(path: String?, size: String = POSTER_SIZE_W342): String? {
        return if (path != null) "${TMDB_IMAGE_BASE_URL}${size}${path}" else null
    }
    
    fun getBackdropUrl(path: String?, size: String = BACKDROP_SIZE_W780): String? {
        return if (path != null) "${TMDB_IMAGE_BASE_URL}${size}${path}" else null
    }
}
