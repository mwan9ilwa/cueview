package com.example.cueview.presentation.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cueview.domain.model.TvShow
import com.example.cueview.presentation.ui.components.NetworkImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    showId: Int,
    onNavigateBack: () -> Unit,
    onAddToLibrary: (TvShow) -> Unit,
    // For now we'll use mock data, in real app you'd get from ViewModel
    show: TvShow? = getMockShow(showId)
) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(show?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (show != null) {
                        IconButton(onClick = { onAddToLibrary(show) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add to Library"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (show == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading show details...")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Header with poster and basic info
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NetworkImage(
                        url = "https://image.tmdb.org/t/p/w300${show.posterPath}",
                        contentDescription = "${show.name} poster",
                        modifier = Modifier
                            .size(120.dp, 180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = show.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "★ ${String.format("%.1f", show.voteAverage)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = show.firstAirDate?.toString() ?: "Unknown",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (show.genreIds.isNotEmpty()) {
                            Text(
                                text = "Genres: ${show.genreIds.joinToString(", ")}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Overview
                if (show.overview.isNotBlank()) {
                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = show.overview,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Additional Details
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        DetailRow("Rating", "${show.voteAverage}/10")
                        DetailRow("Popularity", "${show.popularity.toInt()}")
                        DetailRow("Original Language", show.originalLanguage.uppercase())
                        if (show.status != null) {
                            DetailRow("Status", show.status!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// Mock data function - in real app this would come from ViewModel/Repository
private fun getMockShow(showId: Int): TvShow? {
    return TvShow(
        id = showId,
        name = "The Bear",
        originalName = "The Bear",
        overview = "Carmen 'Carmy' Berzatto, a young chef from the fine dining world, comes home to Chicago to run his deceased brother's Italian beef sandwich shop. This is a workplace comedy about a small group of misfits trying to make sure this business doesn't fail.",
        posterPath = "/sHFlbKS3WLqMnp9t2ghADIJFnuQ.jpg",
        backdropPath = "/9f5sB0RK0cSeb4LZHHttei0krNr.jpg",
        firstAirDate = kotlinx.datetime.LocalDate(2022, 6, 23),
        genreIds = listOf(35, 18), // Comedy, Drama
        originalLanguage = "en",
        popularity = 2547.645,
        voteAverage = 8.3,
        voteCount = 756,
        status = "Returning Series"
    )
}
