package com.example.soundwave.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.soundwave.data.repository.UserSession
import com.example.soundwave.navigation.Screen
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.viewModels.HomeViewModel
import com.example.soundwave.viewModels.PlayerViewModel
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {

    val searchText by viewModel.searchText
    val results = viewModel.getFilteredMusic()
    val context = LocalContext.current
    val playerViewModel: PlayerViewModel = viewModel(LocalActivity.current)

    // trigger remote search when the search text changes, debounced to avoid spamming the API
    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.searchText.value }
            .debounce(300)
            .distinctUntilChanged()
            .collectLatest { current ->
                if (current.isNotBlank()) {
                    viewModel.searchTracks()
                }
            }
    }

    val user = viewModel.getUser()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF9C4DFF))
                        .clickable { navController.navigate(Screen.Profile.route) },
                    contentAlignment = Alignment.Center
                ) {
                    if (user?.avatarUrl != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(user.avatarUrl).crossfade(true).build(),
                            contentDescription = user.name,
                            modifier = Modifier.size(45.dp).clip(RoundedCornerShape(50)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = user?.name?.take(2)?.uppercase() ?: "AN",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    "Rechercher",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Icon(
                Icons.Default.CameraAlt,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEAEAEA), RoundedCornerShape(30.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)

            Spacer(modifier = Modifier.width(10.dp))

            TextField(
                value = searchText,
                onValueChange = { viewModel.updateSearch(it) },
                placeholder = { Text("Que souhaitez-vous écouter ?") },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (searchText.isNotEmpty()) {

            LazyColumn {

                items(results) { music ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                AudioPlayerController.play(context, music, listOf(music), playerViewModel)
                                // navController.navigate("player")
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = music.coverUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column() {
                            val displayName = music.username ?: (music.username ?: "AI")
                            Text(music.title, color = Color.White)
                            Text(displayName, color = Color.Gray, style = MaterialTheme.typography.bodyMedium )
                        }

                    }
                }
            }

        } else {

            LazyColumn {

                item {
                    Text(
                        "Commencer la navigation",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item { GridSection() }

                item { Spacer(modifier = Modifier.height(20.dp)) }

                item {
                    Text(
                        "Découvrez de nouveaux horizons",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item { DiscoverSection() }

                item { Spacer(modifier = Modifier.height(20.dp)) }

                item {
                    Text(
                        "Tout parcourir",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item { BrowseAllSection() }
            }
        }
    }


}

@Composable
fun GridSection() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(220.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(
            listOf(
                "Musique" to listOf(Color.Magenta, Color.Red),
                "Podcasts" to listOf(Color(0xFF0F9D58), Color(0xFF34A853)),
                "Livres audio" to listOf(Color(0xFF3F51B5), Color(0xFF2196F3)),
                "Événements live" to listOf(Color(0xFF9C27B0), Color(0xFFE040FB))
            )
        ) { item ->

            Box(
                modifier = Modifier
                    .height(100.dp)
                    .background(
                        Brush.linearGradient(item.second),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(item.first, color = Color.White)
            }
        }
    }
}

@Composable
fun DiscoverSection() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        listOf(
            "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4",
            "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f",
            "https://images.unsplash.com/photo-1507874457470-272b3c8d8ee2"
        ).forEach {

            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
fun BrowseAllSection() {

    val categories = listOf(
        "Hip-Hop" to Color(0xFFE91E63),
        "Afro" to Color(0xFFFF9800),
        "Chill" to Color(0xFF03A9F4),
        "Workout" to Color(0xFF4CAF50),
        "Party" to Color(0xFF9C27B0),
        "Focus" to Color(0xFF607D8B)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(300.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(categories) { item ->

            Box(
                modifier = Modifier
                    .height(100.dp)
                    .background(item.second, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(item.first, color = Color.White)
            }
        }
    }
}