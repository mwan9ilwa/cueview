package com.example.cueview.presentation.screens.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.cueview.domain.model.TvShow
import com.example.cueview.presentation.viewmodel.DiscoverViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedDiscoverScreen(
    onNavigateToShowDetails: (Int) -> Unit,
    viewModel: DiscoverViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle messages
    LaunchedEffect(uiState.addToLibraryMessage) {
        uiState.addToLibraryMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearAddToLibraryMessage()
        }
    }
    
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Discover") },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                            contentDescription = "Toggle Filters"
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
            // Enhanced Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        viewModel.updateSearchQuery(it)
                    },
                    label = { Text("Search shows...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { 
                                searchQuery = ""
                                viewModel.updateSearchQuery("")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear"
                                )
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
            
            // Quick Filter Chips
            if (showFilters) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Quick Filters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            items(
                                listOf(
                                    "Popular",
                                    "Trending",
                                    "Top Rated",
                                    "Action",
                                    "Comedy",
                                    "Drama",
                                    "Sci-Fi"
                                )
                            ) { filter ->
                                FilterChip(
                                    selected = false,
                                    onClick = { /* TODO: Implement filter logic */ },
                                    label = { Text(filter) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Content
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
                            Text(
                                text = "Discovering great shows...",
                                modifier = Modifier.padding(top = 16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                searchQuery.isNotBlank() -> {
                    SearchResultsSection(
                        results = uiState.searchResults,
                        onShowClick = onNavigateToShowDetails,
                        onAddToLibrary = { viewModel.addToLibrary(it) },
                        isAddingToLibrary = uiState.isAddingToLibrary,
                        searchQuery = searchQuery
                    )
                }
                
                else -> {
                    ContentSections(
                        trendingShows = uiState.trendingShows,
                        popularShows = uiState.popularShows,
                        onShowClick = onNavigateToShowDetails,
                        onAddToLibrary = { viewModel.addToLibrary(it) },
                        isAddingToLibrary = uiState.isAddingToLibrary
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultsSection(
    results: List<TvShow>,
    onShowClick: (Int) -> Unit,
    onAddToLibrary: (TvShow) -> Unit,
    isAddingToLibrary: Boolean,
    searchQuery: String
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = if (results.isNotEmpty()) {
                    "Found ${results.size} results for \"$searchQuery\""
                } else {
                    "No results found for \"$searchQuery\""
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        
        if (results.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No shows found",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = "Try adjusting your search terms",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(results) { show ->
                EnhancedShowCard(
                    show = show,
                    onShowClick = onShowClick,
                    onAddToLibrary = onAddToLibrary,
                    isAddingToLibrary = isAddingToLibrary,
                    isHorizontal = true
                )
            }
        }
    }
}

@Composable
private fun ContentSections(
    trendingShows: List<TvShow>,
    popularShows: List<TvShow>,
    onShowClick: (Int) -> Unit,
    onAddToLibrary: (TvShow) -> Unit,
    isAddingToLibrary: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Trending Section
        if (trendingShows.isNotEmpty()) {
            item {
                ShowSection(
                    title = "Trending This Week",
                    subtitle = "Shows everyone is watching",
                    shows = trendingShows,
                    onShowClick = onShowClick,
                    onAddToLibrary = onAddToLibrary,
                    isAddingToLibrary = isAddingToLibrary
                )
            }
        }
        
        // Popular Section
        if (popularShows.isNotEmpty()) {
            item {
                ShowSection(
                    title = "Popular Shows",
                    subtitle = "Highly rated and loved",
                    shows = popularShows,
                    onShowClick = onShowClick,
                    onAddToLibrary = onAddToLibrary,
                    isAddingToLibrary = isAddingToLibrary
                )
            }
        }
        
        // Suggestions Section (placeholder)
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "More personalized recommendations coming soon!",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Add more shows to your library to get better suggestions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowSection(
    title: String,
    subtitle: String,
    shows: List<TvShow>,
    onShowClick: (Int) -> Unit,
    onAddToLibrary: (TvShow) -> Unit,
    isAddingToLibrary: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            TextButton(
                onClick = { /* TODO: Navigate to full list */ }
            ) {
                Text("See All")
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(shows.take(10)) { show ->
                EnhancedShowCard(
                    show = show,
                    onShowClick = onShowClick,
                    onAddToLibrary = onAddToLibrary,
                    isAddingToLibrary = isAddingToLibrary,
                    isHorizontal = false,
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedShowCard(
    show: TvShow,
    onShowClick: (Int) -> Unit,
    onAddToLibrary: (TvShow) -> Unit,
    isAddingToLibrary: Boolean,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.then(
            if (isHorizontal) Modifier.fillMaxWidth() else Modifier
        ),
        onClick = { onShowClick(show.id) }
    ) {
        if (isHorizontal) {
            // Horizontal layout for search results
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Poster placeholder
                Card(
                    modifier = Modifier.size(60.dp, 90.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📺", fontSize = 24.sp)
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = show.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = show.overview,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " ${(show.voteAverage * 10).toInt() / 10.0}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        show.firstAirDate?.year?.let { year ->
                            Text(
                                text = " • $year",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Add button
                if (isAddingToLibrary) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = { onAddToLibrary(show) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to Library"
                        )
                    }
                }
            }
        } else {
            // Vertical layout for sections
            Column {
                // Poster placeholder
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📺", fontSize = 48.sp)
                    }
                }
                
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = show.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = " ${(show.voteAverage * 10).toInt() / 10.0}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (isAddingToLibrary) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(
                                onClick = { onAddToLibrary(show) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add to Library",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
