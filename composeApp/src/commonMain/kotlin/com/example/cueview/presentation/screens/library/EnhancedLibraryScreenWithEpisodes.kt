package com.example.cueview.presentation.screens.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cueview.domain.model.TvShow
import com.example.cueview.domain.model.WatchStatus
import com.example.cueview.domain.model.UserShow
import com.example.cueview.presentation.viewmodel.LibraryViewModel
import com.example.cueview.presentation.ui.components.NetworkImage
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedLibraryScreenWithEpisodes(
    onNavigateToShowDetails: (Int) -> Unit,
    viewModel: LibraryViewModel = koinInject()
) {
    val userShows by viewModel.userShows.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<WatchStatus?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "My Library", 
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search your library") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status Filter Chips
            Text(
                text = "Filter by Status:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // All Shows Chip
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { selectedStatus = null },
                    label = { Text("All") }
                )
                
                // Status Chips
                WatchStatus.values().forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = if (selectedStatus == status) null else status },
                        label = { Text(status.name.replace('_', ' ')) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Shows List
            val filteredShows = userShows
                .filter { userShow ->
                    // Filter by search query
                    (searchQuery.isBlank() || userShow.showName.contains(searchQuery, ignoreCase = true)) &&
                    // Filter by status
                    (selectedStatus == null || userShow.status == selectedStatus)
                }
                .sortedByDescending { it.dateAdded }
            
            if (filteredShows.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (userShows.isEmpty()) {
                                "Your library is empty"
                            } else {
                                "No shows match your filters"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (userShows.isEmpty()) {
                                "Add some shows from the Discover tab!"
                            } else {
                                "Try adjusting your search or filters"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    text = "${filteredShows.size} show${if (filteredShows.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredShows) { userShow ->
                        EnhancedLibraryShowCard(
                            userShow = userShow,
                            onShowClick = { onNavigateToShowDetails(userShow.showId.toInt()) },
                            onStatusChange = { newStatus ->
                                viewModel.updateShowStatus(userShow, newStatus)
                            },
                            onRemoveFromLibrary = {
                                viewModel.removeShowFromLibrary(userShow)
                            },
                            onEpisodeWatched = { season, episode ->
                                viewModel.markEpisodeWatched(userShow, season, episode)
                            },
                            onSeasonCompleted = { season ->
                                viewModel.markSeasonCompleted(userShow, season)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedLibraryShowCard(
    userShow: UserShow,
    onShowClick: () -> Unit,
    onStatusChange: (WatchStatus) -> Unit,
    onRemoveFromLibrary: () -> Unit,
    onEpisodeWatched: (Int, Int) -> Unit,
    onSeasonCompleted: (Int) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }
    var showProgressDetails by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Main show info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Poster
                NetworkImage(
                    url = "https://image.tmdb.org/t/p/w200${userShow.posterPath}",
                    contentDescription = "${userShow.showName} poster",
                    modifier = Modifier
                        .size(60.dp, 90.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = userShow.showName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Added: ${userShow.dateAdded}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Progress info for watching shows
                    if (userShow.status == WatchStatus.WATCHING) {
                        Text(
                            text = "S${userShow.currentSeason}E${userShow.currentEpisode}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Status Chip
                Box {
                    FilterChip(
                        selected = true,
                        onClick = { showStatusMenu = true },
                        label = { Text(userShow.status.name.replace('_', ' ')) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (userShow.status) {
                                WatchStatus.WATCHING -> MaterialTheme.colorScheme.primary
                                WatchStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                                WatchStatus.PLAN_TO_WATCH -> MaterialTheme.colorScheme.secondary
                                WatchStatus.ON_HOLD -> MaterialTheme.colorScheme.outline
                                WatchStatus.DROPPED -> MaterialTheme.colorScheme.error
                            }
                        )
                    )
                    
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        WatchStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name.replace('_', ' ')) },
                                onClick = {
                                    onStatusChange(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Remove from Library") },
                            onClick = {
                                onRemoveFromLibrary()
                                showStatusMenu = false
                            }
                        )
                    }
                }
            }
            
            // Progress tracking for watching shows
            if (userShow.status == WatchStatus.WATCHING) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress Tracking",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    TextButton(
                        onClick = { showProgressDetails = !showProgressDetails }
                    ) {
                        Text(if (showProgressDetails) "Hide" else "Show")
                        Icon(
                            if (showProgressDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                AnimatedVisibility(visible = showProgressDetails) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        // Current progress
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Current: Season ${userShow.currentSeason}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Episode ${userShow.currentEpisode}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Episode controls
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { 
                                            onEpisodeWatched(userShow.currentSeason, userShow.currentEpisode + 1)
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Episode")
                                    }
                                    
                                    OutlinedButton(
                                        onClick = { 
                                            onSeasonCompleted(userShow.currentSeason + 1)
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Season")
                                    }
                                }
                            }
                        }
                        
                        // Personal notes and rating
                        if (userShow.personalRating != null || userShow.personalNotes?.isNotBlank() == true) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    if (userShow.personalRating != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Your Rating: ${userShow.personalRating}/10",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                    
                                    if (userShow.personalNotes?.isNotBlank() == true) {
                                        if (userShow.personalRating != null) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }
                                        Text(
                                            text = "Notes: ${userShow.personalNotes}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Main action button
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onShowClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Details")
            }
        }
    }
}
