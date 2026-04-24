package com.example.soundwave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.example.soundwave.viewModels.HomeViewModel
import com.example.soundwave.models.MusicTrack

@Composable
fun GenreScreen(
    genreName: String,
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {


    val musics = homeViewModel.musicList.shuffled()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }

            Text(
                text = genreName,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(musics) { music ->
                MusicListItem(music)
            }

        }
    }
}

@Composable
fun MusicListItem(music: MusicTrack) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E2E), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = music.coverUrl,
            contentDescription = music.title,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {


            Text(
                text = music.title,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )


            Text(
                text = music.username ?: "Unknown",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}