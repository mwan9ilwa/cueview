package com.example.cueview.data.remote.api

// Expect declaration for platform-specific API key retrieval
expect fun getTmdbApiKey(): String

object ApiConfig {
    // TMDb API key should be provided via build configuration or environment variable
    // Get your key at: https://www.themoviedb.org/settings/api
    // For multiplatform support, we'll use expect/actual pattern
    val TMDB_API_KEY: String = getTmdbApiKey()
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
