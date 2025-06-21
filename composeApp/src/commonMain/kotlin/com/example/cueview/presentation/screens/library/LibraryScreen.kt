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
import com.example.cueview.domain.model.UserShow
import com.example.cueview.domain.model.WatchStatus
import com.example.cueview.presentation.viewmodel.LibraryViewModel
import org.koin.compose.koinInject

@Composable
fun LibraryScreen(
    onNavigateToShowDetails: (Int) -> Unit,
    viewModel: LibraryViewModel = koinInject()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Watching", "Completed", "Plan to Watch", "On Hold", "Dropped")
    
    val watchingShows by viewModel.watchingShows.collectAsState()
    val completedShows by viewModel.completedShows.collectAsState()
    val planToWatchShows by viewModel.planToWatchShows.collectAsState()
    val onHoldShows by viewModel.onHoldShows.collectAsState()
    val droppedShows by viewModel.droppedShows.collectAsState()
    
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
            0 -> LibraryContent("Watching", watchingShows, onNavigateToShowDetails, viewModel)
            1 -> LibraryContent("Completed", completedShows, onNavigateToShowDetails, viewModel)
            2 -> LibraryContent("Plan to Watch", planToWatchShows, onNavigateToShowDetails, viewModel)
            3 -> LibraryContent("On Hold", onHoldShows, onNavigateToShowDetails, viewModel)
            4 -> LibraryContent("Dropped", droppedShows, onNavigateToShowDetails, viewModel)
        }
    }
}

@Composable
private fun LibraryContent(
    title: String,
    shows: List<UserShow>,
    onNavigateToShowDetails: (Int) -> Unit,
    viewModel: LibraryViewModel
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
                    onClick = { onNavigateToShowDetails(show.showId) },
                    onRemove = { viewModel.removeShowFromLibrary(show) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryShowCard(
    show: UserShow,
    onClick: () -> Unit,
    onRemove: () -> Unit
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
                    text = show.showName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                val progressText = when {
                    show.status == WatchStatus.COMPLETED -> "Completed"
                    show.currentSeason > 1 || show.currentEpisode > 1 -> 
                        "S${show.currentSeason} E${show.currentEpisode}"
                    else -> "Not started"
                }
                
                Text(
                    text = progressText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (show.personalRating != null) {
                    Text(
                        text = "Your rating: ★ ${show.personalRating}",
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
            
            // Remove button
            TextButton(
                onClick = onRemove
            ) {
                Text("Remove")
            }
        }
    }
}


