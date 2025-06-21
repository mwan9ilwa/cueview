package com.example.cueview.presentation.screens.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cueview.presentation.viewmodel.DiscoverViewModel
import com.example.cueview.presentation.viewmodel.DiscoverUiState
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onNavigateToShowDetails: (Int) -> Unit,
    viewModel: DiscoverViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show success message when show is added to library
    LaunchedEffect(uiState.addToLibraryMessage) {
        uiState.addToLibraryMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearAddToLibraryMessage()
        }
    }
    
    // Show error message
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }
    
    // Trigger search when query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.searchShows(searchQuery)
        } else {
            viewModel.loadInitialData()
        }
    }
    
    // Load trending shows on first load
    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Discover",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search TV Shows") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            searchQuery.isBlank() -> {
                // Show trending/popular shows
                LazyColumn {
                    if (uiState.trendingShows.isNotEmpty()) {
                        item {
                            Text(
                                text = "Trending This Week",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        
                        items(uiState.trendingShows) { show ->
                            ShowCard(
                                show = show,
                                onAddToLibrary = { viewModel.addToLibrary(show) },
                                onClick = { onNavigateToShowDetails(show.id) },
                                isAddingToLibrary = uiState.isAddingToLibrary
                            )
                        }
                    }
                    
                    if (uiState.popularShows.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Popular Shows",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        
                        items(uiState.popularShows) { show ->
                            ShowCard(
                                show = show,
                                onAddToLibrary = { viewModel.addToLibrary(show) },
                                onClick = { onNavigateToShowDetails(show.id) },
                                isAddingToLibrary = uiState.isAddingToLibrary
                            )
                        }
                    }
                }
            }
            
            else -> {
                // Show search results
                LazyColumn {
                    if (uiState.searchResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "Search Results",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        
                        items(uiState.searchResults) { show ->
                            ShowCard(
                                show = show,
                                onAddToLibrary = { viewModel.addToLibrary(show) },
                                onClick = { onNavigateToShowDetails(show.id) },
                                isAddingToLibrary = uiState.isAddingToLibrary
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "No shows found for \"$searchQuery\"",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowCard(
    show: com.example.cueview.domain.model.TvShow,
    onAddToLibrary: () -> Unit,
    onClick: () -> Unit,
    isAddingToLibrary: Boolean = false
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
                Text("📺", fontSize = 30.sp)
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = show.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = show.overview,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "★ ${(show.voteAverage * 10).toInt() / 10.0}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = show.firstAirDate?.year?.toString() ?: "Unknown",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Add to Library Button
            if (isAddingToLibrary) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(
                    onClick = onAddToLibrary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to Library",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
