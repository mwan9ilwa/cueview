package com.example.cueview.presentation.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cueview.domain.model.TvShow
import com.example.cueview.domain.model.WatchStatus
import com.example.cueview.presentation.viewmodel.LibraryViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleEnhancedLibraryScreen(
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
            
            when {
                false -> { // No loading state in current ViewModel
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Loading your library...")
                        }
                    }
                }
                
                false -> { // No error state in current ViewModel
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Error loading library",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                else -> {
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
                            ) {                            Text(
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
                                LibraryShowCard(
                                    userShow = userShow,
                                    onShowClick = { onNavigateToShowDetails(userShow.showId.toInt()) },
                                    onStatusChange = { newStatus ->
                                        viewModel.updateShowStatus(userShow, newStatus)
                                    },
                                    onRemoveFromLibrary = {
                                        viewModel.removeShowFromLibrary(userShow)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryShowCard(
    userShow: com.example.cueview.domain.model.UserShow,
    onShowClick: () -> Unit,
    onStatusChange: (WatchStatus) -> Unit,
    onRemoveFromLibrary: () -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }
    
    Card(
        onClick = onShowClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
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
            
            // Progress indicator (if watching)
            if (userShow.status == WatchStatus.WATCHING) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Currently watching",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
