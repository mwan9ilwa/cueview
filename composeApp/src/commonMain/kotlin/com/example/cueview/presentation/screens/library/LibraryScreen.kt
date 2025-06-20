package com.example.cueview.presentation.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LibraryScreen(
    onNavigateToShowDetails: (Int) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Watching", "Completed", "Plan to Watch", "On Hold", "Dropped")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Library",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (selectedTab) {
            0 -> LibraryContent("Watching", getWatchingShows(), onNavigateToShowDetails)
            1 -> LibraryContent("Completed", getCompletedShows(), onNavigateToShowDetails)
            2 -> LibraryContent("Plan to Watch", getPlanToWatchShows(), onNavigateToShowDetails)
            3 -> LibraryContent("On Hold", getOnHoldShows(), onNavigateToShowDetails)
            4 -> LibraryContent("Dropped", getDroppedShows(), onNavigateToShowDetails)
        }
    }
}

@Composable
private fun LibraryContent(
    title: String,
    shows: List<LibraryShowItem>,
    onNavigateToShowDetails: (Int) -> Unit
) {
    if (shows.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No shows in $title",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn {
            items(shows) { show ->
                LibraryShowCard(
                    show = show,
                    onClick = { onNavigateToShowDetails(show.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryShowCard(
    show: LibraryShowItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO: Add poster image with Coil
            Box(
                modifier = Modifier
                    .size(60.dp, 90.dp)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("IMG")
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = show.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                if (show.progress != null) {
                    Text(
                        text = show.progress,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                if (show.personalRating != null) {
                    Text(
                        text = "Your rating: ★ ${(show.personalRating * 10).toInt() / 10.0}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (show.lastWatched != null) {
                    Text(
                        text = "Last watched: ${show.lastWatched}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// TODO: Replace with actual data models
data class LibraryShowItem(
    val id: Int,
    val name: String,
    val progress: String?,
    val personalRating: Double?,
    val lastWatched: String?,
    val posterPath: String?
)

// TODO: Replace with actual data from repository
private fun getWatchingShows(): List<LibraryShowItem> = listOf(
    LibraryShowItem(1, "Breaking Bad", "S3 E5", 9.0, "2 days ago", null),
    LibraryShowItem(2, "Stranger Things", "S4 E2", 8.5, "1 week ago", null)
)

private fun getCompletedShows(): List<LibraryShowItem> = listOf(
    LibraryShowItem(3, "The Office", "Completed", 9.5, "1 month ago", null)
)

private fun getPlanToWatchShows(): List<LibraryShowItem> = listOf(
    LibraryShowItem(4, "Game of Thrones", null, null, null, null)
)

private fun getOnHoldShows(): List<LibraryShowItem> = emptyList()
private fun getDroppedShows(): List<LibraryShowItem> = emptyList()
