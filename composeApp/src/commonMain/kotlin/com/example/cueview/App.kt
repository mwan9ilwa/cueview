package com.example.cueview

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cueview.presentation.screens.auth.LoginScreen
import com.example.cueview.presentation.screens.discover.DiscoverScreen
import com.example.cueview.presentation.screens.library.LibraryScreen
import com.example.cueview.presentation.screens.profile.ProfileScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import com.example.cueview.di.appModules

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModules)
    }) {
        MaterialTheme {
            var currentScreen by remember { mutableStateOf(AppScreen.Login) }
            
            Scaffold(
                bottomBar = {
                    if (currentScreen != AppScreen.Login) {
                        NavigationBar {
                            AppScreen.bottomNavScreens.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) },
                                    selected = currentScreen == screen,
                                    onClick = { currentScreen = screen }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    AppScreen.Login -> {
                        LoginScreen(
                            onNavigateToDiscover = {
                                currentScreen = AppScreen.Discover
                            }
                        )
                    }
                    AppScreen.Discover -> {
                        DiscoverScreen(
                            onNavigateToShowDetails = { showId ->
                                // TODO: Navigate to show details
                            }
                        )
                    }
                    AppScreen.Library -> {
                        LibraryScreen(
                            onNavigateToShowDetails = { showId ->
                                // TODO: Navigate to show details
                            }
                        )
                    }
                    AppScreen.Profile -> {
                        ProfileScreen(
                            onNavigateToLogin = {
                                currentScreen = AppScreen.Login
                            }
                        )
                    }
                }
            }
        }
    }
}
}

enum class AppScreen(
    val title: String,
    val icon: ImageVector
) {
    Login("Login", Icons.Default.Person),
    Discover("Discover", Icons.Default.Home),
    Library("Library", Icons.AutoMirrored.Filled.List),
    Profile("Profile", Icons.Default.Person);
    
    companion object {
        val bottomNavScreens = listOf(Discover, Library, Profile)
    }
}