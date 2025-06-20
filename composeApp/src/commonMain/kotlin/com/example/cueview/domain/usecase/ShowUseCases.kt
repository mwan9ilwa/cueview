package com.example.cueview.domain.usecase

import com.example.cueview.domain.model.TvShow
import com.example.cueview.domain.repository.TvShowRepository

/**
 * Use cases for TV show discovery and search operations
 */

class GetTrendingShowsUseCase(
    private val repository: TvShowRepository
) {
    suspend operator fun invoke(): Result<List<TvShow>> {
        return repository.getTrendingShows()
    }
}

class GetPopularShowsUseCase(
    private val repository: TvShowRepository
) {
    suspend operator fun invoke(page: Int = 1): Result<List<TvShow>> {
        return repository.getPopularShows(page)
    }
}

class SearchShowsUseCase(
    private val repository: TvShowRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<List<TvShow>> {
        return if (query.isBlank()) {
            Result.success(emptyList())
        } else {
            repository.searchShows(query, page)
        }
    }
}

class GetShowDetailsUseCase(
    private val repository: TvShowRepository
) {
    suspend operator fun invoke(showId: Int): Result<TvShow> {
        return repository.getShowDetails(showId)
    }
}
