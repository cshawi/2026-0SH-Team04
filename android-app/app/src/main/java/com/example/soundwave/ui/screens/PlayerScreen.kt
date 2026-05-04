package com.example.soundwave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.viewModels.LibraryViewModel
import com.example.soundwave.viewModels.PlayerViewModel

@Composable
fun PlayerScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel
) {

    val context = LocalContext.current
    val musicList = playerViewModel.musicList

    val music = playerViewModel.currentTrack

    val isPlaying = AudioPlayerController.isPlaying
    val duration = AudioPlayerController.durationMs
    val position = AudioPlayerController.positionMs

    val coroutineScope = rememberCoroutineScope()
    val libraryViewModel: LibraryViewModel = viewModel()

    val progress =
        if (duration > 0)
            (position.toFloat() / duration.toFloat()).coerceIn(0f,1f)
        else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp, 30.dp,30.dp,10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                if(music == null){
                    IconButton(
                        onClick = {
                            AudioPlayerController.stop()
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                    
                }
                else{

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, -30) }
                            .padding(5.dp, 0.dp)
                    ) {
                        MusicOptionsMenu(music, libraryViewModel, context, coroutineScope)
                    }
                }
            }
        
            Spacer(modifier = Modifier.height(25.dp))

            AsyncImage(
                model = music?.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = music?.title ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(

                text = music?.username ?: "AI",
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(25.dp))

            Slider(
                value = progress,
                onValueChange = { newValue ->

                    val newPosition = (newValue * duration).toLong()
                    AudioPlayerController.seekTo(newPosition)

                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){

                Text(
                    text = formatTime(position),
                    color = Color.LightGray
                )

                Text(
                    text = formatTime(duration),
                    color = Color.LightGray
                )

            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                val prevEnabled = musicList.isNotEmpty() && musicList.first() != playerViewModel.currentTrack
                IconButton(
                    onClick = {
                        val previous = playerViewModel.getPreviousMusic()

                        previous?.let {
                            playerViewModel.currentTrack = previous
                            AudioPlayerController.play(context, it, playerViewModel.musicList, playerViewModel)
                        }

                    },
                    enabled = prevEnabled,
                    modifier = Modifier.padding(20.dp, 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = if (prevEnabled) 1f else 0.4f),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF4FACFE),
                                    Color(0xFF7B61FF),
                                    Color(0xFF9F5DE2)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            music?.let {
                                if (AudioPlayerController.currentUrl == it.audioUrl) {
                                    AudioPlayerController.togglePlayPause()
                                } else {
                                    AudioPlayerController.play(context, it)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                val nextEnabled = musicList.isNotEmpty() && musicList.last() != playerViewModel.currentTrack
                IconButton(
                    onClick = {
                        val next = playerViewModel.getNextMusic()

                        next?.let {
                            playerViewModel.currentTrack = next
                            AudioPlayerController.play(context, it, playerViewModel.musicList, playerViewModel)
                        }

                    },
                    enabled = nextEnabled,
                    modifier = Modifier.padding(20.dp, 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = if (nextEnabled) 1f else 0.4f),
                        modifier = Modifier.size(40.dp)
                    )
                }

            }

        }

    }

}

fun formatTime(ms: Long): String {

    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%d:%02d".format(minutes, seconds)

}