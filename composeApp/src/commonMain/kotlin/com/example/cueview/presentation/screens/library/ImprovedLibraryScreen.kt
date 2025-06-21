package com.example.cueview.presentation.screens.library

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cueview.domain.model.*
import com.example.cueview.presentation.viewmodel.LibraryViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedLibraryScreen(
    onNavigateToShowDetails: (Int) -> Unit,
    viewModel: LibraryViewModel = koinInject()
) {
    val watchingShows by viewModel.watchingShows.collectAsState()
    val completedShows by viewModel.completedShows.collectAsState()
    val planToWatchShows by viewModel.planToWatchShows.collectAsState()
    val onHoldShows by viewModel.onHoldShows.collectAsState()
    val droppedShows by viewModel.droppedShows.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    
    val tabs = listOf("Watching", "Completed", "Plan to Watch", "On Hold", "Dropped")
    val tabCounts = listOf(
        watchingShows.size,
        completedShows.size,
        planToWatchShows.size,
        onHoldShows.size,
        droppedShows.size
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Library") },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = if (showSearch) Icons.Default.SearchOff else Icons.Default.Search,
                            contentDescription = "Toggle Search"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            if (showSearch) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search your library...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            
            // Tabs with counts
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    val count = tabCounts[index]
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text("$title${if (count > 0) " ($count)" else ""}") 
                        }
                    )
                }
            }
            
            // Content
            val currentShows = when (selectedTab) {
                0 -> watchingShows
                1 -> completedShows
                2 -> planToWatchShows
                3 -> onHoldShows
                4 -> droppedShows
                else -> emptyList()
            }
            
            // Filter shows based on search
            val filteredShows = if (searchQuery.isNotBlank()) {
                currentShows.filter { 
                    it.showName.contains(searchQuery, ignoreCase = true) 
                }
            } else {
                currentShows
            }
            
            ImprovedLibraryContent(
                shows = filteredShows,
                onNavigateToShowDetails = onNavigateToShowDetails,
                onRemove = { viewModel.removeShowFromLibrary(it) },
                onStatusChange = { show, status -> viewModel.updateShowStatus(show, status) },
                onRatingChange = { show, rating -> viewModel.rateShow(show, rating) },
                statusName = tabs[selectedTab].lowercase()
            )
        }
    }
}

@Composable
private fun ImprovedLibraryContent(
    shows: List<UserShow>,
    onNavigateToShowDetails: (Int) -> Unit,
    onRemove: (UserShow) -> Unit,
    onStatusChange: (UserShow, WatchStatus) -> Unit,
    onRatingChange: (UserShow, Double) -> Unit,
    statusName: String
) {
    if (shows.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No shows $statusName",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Add some shows to get started!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(shows, key = { it.id }) { show ->
                ImprovedLibraryShowCard(
                    show = show,
                    onShowClick = { onNavigateToShowDetails(show.showId) },
                    onRemove = { onRemove(show) },
                    onStatusChange = { newStatus -> onStatusChange(show, newStatus) },
                    onRatingChange = { rating -> onRatingChange(show, rating) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImprovedLibraryShowCard(
    show: UserShow,
    onShowClick: () -> Unit,
    onRemove: () -> Unit,
    onStatusChange: (WatchStatus) -> Unit,
    onRatingChange: (Double) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Main Card Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Poster Placeholder with progress indicator
                Card(
                    modifier = Modifier.size(80.dp, 120.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📺", fontSize = 32.sp)
                        
                        // Progress indicator for watching shows
                        if (show.status == WatchStatus.WATCHING && show.totalEpisodes != null && show.totalEpisodes > 0) {
                            val watchedCount = show.watchedEpisodes.size
                            val progress = watchedCount.toFloat() / show.totalEpisodes
                            
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .align(Alignment.BottomCenter),
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Show Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = show.showName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Enhanced Progress Info
                    val progressText = when {
                        show.status == WatchStatus.COMPLETED -> "Completed"
                        show.totalEpisodes != null -> {
                            val watched = show.watchedEpisodes.size
                            "$watched/${show.totalEpisodes} episodes"
                        }
                        show.currentSeason > 1 || show.currentEpisode > 1 -> 
                            "S${show.currentSeason} E${show.currentEpisode}"
                        else -> "Not started"
                    }
                    
                    Text(
                        text = progressText,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Rating with stars
                    if (show.personalRating != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < (show.personalRating / 2).toInt()) {
                                        Color(0xFFFFD700)
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = " ${show.personalRating}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    
                    // Last Watched
                    show.lastWatched?.let { date ->
                        Text(
                            text = "Last watched: $date",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                
                // Action Buttons
                Column {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }
                }
            }
            
            // Expanded Content
            if (isExpanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Actions",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        // Action Buttons Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // View Details
                            OutlinedButton(
                                onClick = onShowClick
                            ) {
                                Text("View Details")
                            }
                            
                            // Change Status
                            OutlinedButton(
                                onClick = { showStatusMenu = true }
                            ) {
                                Text("Status")
                            }
                            
                            // Rate Show
                            OutlinedButton(
                                onClick = { showRatingDialog = true }
                            ) {
                                Text("Rate")
                            }
                            
                            // Remove
                            OutlinedButton(
                                onClick = onRemove,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Remove")
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Status Change Menu
    if (showStatusMenu) {
        AlertDialog(
            onDismissRequest = { showStatusMenu = false },
            title = { Text("Change Status") },
            text = {
                Column {
                    WatchStatus.values().forEach { status ->
                        TextButton(
                            onClick = {
                                onStatusChange(status)
                                showStatusMenu = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = status.name.replace('_', ' '),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusMenu = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Rating Dialog
    if (showRatingDialog) {
        var selectedRating by remember { mutableStateOf(show.personalRating ?: 5.0) }
        
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Rate this Show") },
            text = {
                Column {
                    Text("Select a rating from 1 to 10:")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Slider(
                        value = selectedRating.toFloat(),
                        onValueChange = { selectedRating = it.toDouble() },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                    
                    Text(
                        text = "Rating: ${selectedRating.toInt()}/10",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        onRatingChange(selectedRating)
                        showRatingDialog = false 
                    }
                ) {
                    Text("Save Rating")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
