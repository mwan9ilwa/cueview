package com.example.cueview.data.remote.api

import com.example.cueview.BuildConfig

actual fun getTmdbApiKey(): String {
    return BuildConfig.TMDB_API_KEY
}
