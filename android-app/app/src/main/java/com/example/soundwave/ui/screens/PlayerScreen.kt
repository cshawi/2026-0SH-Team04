package com.example.soundwave.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.soundwave.ui.components.AudioPlayerController
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
@Composable
fun PlayerScreen(musicId: String) {

    val context = LocalContext.current
    val music = musicList.find { it.id == musicId }
    val currentIndex = musicList.indexOfFirst { it.id == musicId }

    val isPlaying = AudioPlayerController.isPlaying

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A0933),
                        Color(0xFF2E0F5A),
                        Color(0xFF120421)
                    )
                )
            )
    ){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Spacer(modifier = Modifier.height(40.dp))

            // 🎵 Image album
            AsyncImage(
                model = music?.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 🎶 Titre
            Text(
                text = music?.title ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "SoundWave",
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ⏱ Barre de progression (visuelle)
            Slider(
                value = if (isPlaying) 0.5f else 0f,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ▶ Bouton Play / Pause
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ){

                // ⏮ Musique précédente
                IconButton(
                    onClick = {

                        val previous = musicList.getOrNull(currentIndex - 1)

                        previous?.let {
                            AudioPlayerController.play(
                                context,
                                it.audioUrl,
                                it.title
                            )
                        }

                    }
                ){
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                // ▶ Play / Pause
                IconButton(
                    onClick = {

                        music?.let {

                            if (AudioPlayerController.currentUrl == it.audioUrl) {
                                AudioPlayerController.togglePlayPause()
                            } else {
                                AudioPlayerController.play(
                                    context,
                                    it.audioUrl,
                                    it.title
                                )
                            }

                        }

                    },
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Color(0xFF9C4DFF),
                            CircleShape
                        )
                ){

                    Icon(
                        imageVector =
                            if (isPlaying) Icons.Default.Pause
                            else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )

                }

                // ⏭ Musique suivante
                IconButton(
                    onClick = {

                        val next = musicList.getOrNull(currentIndex + 1)

                        next?.let {
                            AudioPlayerController.play(
                                context,
                                it.audioUrl,
                                it.title
                            )
                        }

                    }
                ){
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

            }

        }

    }

}