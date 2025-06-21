package com.example.cueview.presentation.ui.common

/**
 * Sort options for library and discovery content
 */
enum class SortOption(val displayName: String) {
    POPULARITY_DESC("Popularity ↓"),
    POPULARITY_ASC("Popularity ↑"),
    TITLE_ASC("Title A-Z"),
    TITLE_DESC("Title Z-A"), 
    RATING_HIGH("Rating High-Low"),
    RATING_LOW("Rating Low-High"),
    DATE_ADDED("Date Added")
}

/**
 * Content filter options for discovery
 */
enum class ContentFilter {
    ALL, WATCHING, COMPLETED, PLAN_TO_WATCH, DROPPED, ON_HOLD
}

/**
 * Advanced filter options with properties
 */
data class AdvancedFilter(
    val genres: List<Int> = emptyList(),
    val rating: ClosedFloatingPointRange<Double>? = null,
    val year: IntRange? = null
)
