package com.example.cueview.presentation.screens.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cueview.domain.model.TvShow
import com.example.cueview.presentation.viewmodel.DiscoverViewModel
import com.example.cueview.presentation.ui.components.NetworkImage
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable  
fun SimpleEnhancedDiscoverScreen(
    onNavigateToShowDetails: (Int) -> Unit,
    viewModel: DiscoverViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle success/error messages
    LaunchedEffect(uiState.addToLibraryMessage) {
        uiState.addToLibraryMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearAddToLibraryMessage()
        }
    }
    
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Discover", 
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                onValueChange = { 
                    searchQuery = it
                    viewModel.updateSearchQuery(it)
                },
                label = { Text("Search TV Shows") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Loading shows...")
                        }
                    }
                }
                
                uiState.errorMessage != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                searchQuery.isNotBlank() && uiState.searchResults.isNotEmpty() -> {
                    // Search Results
                    Text(
                        text = "Search Results (${uiState.searchResults.size})",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.searchResults) { show ->
                            ShowCard(
                                show = show,
                                onShowClick = { onNavigateToShowDetails(show.id.toInt()) },
                                onAddToLibrary = { viewModel.addToLibrary(show) }
                            )
                        }
                    }
                }
                
                else -> {
                    // Popular Shows
                    Text(
                        text = "Popular TV Shows",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.popularShows) { show ->
                            ShowCard(
                                show = show,
                                onShowClick = { onNavigateToShowDetails(show.id.toInt()) },
                                onAddToLibrary = { viewModel.addToLibrary(show) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowCard(
    show: TvShow,
    onShowClick: () -> Unit,
    onAddToLibrary: () -> Unit
) {
    Card(
        onClick = onShowClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Poster Image
            NetworkImage(
                url = "https://image.tmdb.org/t/p/w200${show.posterPath}",
                contentDescription = "${show.name} poster",
                modifier = Modifier
                    .size(80.dp, 120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (show.overview.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = show.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "★ ${String.format("%.1f", show.voteAverage)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = show.firstAirDate?.toString() ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = onAddToLibrary) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add to Library",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
