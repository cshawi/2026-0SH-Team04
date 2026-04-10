package com.example.soundwave.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.soundwave.ui.components.AudioPlayerController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.viewModels.PlayerViewModel

@Composable
fun PlayerScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel? = null
) {

    val context = LocalContext.current
    val activity = LocalActivity.current
    val vm: PlayerViewModel = playerViewModel ?: viewModel(activity)
    val musicList = vm.musicList

    // log the current musicList for debugging when PlayerScreen composes
    Log.d("PlayerScreen", "musicList=$musicList")

    val music = vm.currentTrack

    val isPlaying = AudioPlayerController.isPlaying
    val duration = AudioPlayerController.durationMs
    val position = AudioPlayerController.positionMs

    val progress =
        if (duration > 0)
            (position.toFloat() / duration.toFloat()).coerceIn(0f,1f)
        else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F0F1A),
                        Color(0xFF151525),
                        Color(0xFF0A0A12)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        AudioPlayerController.stop()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }

            }

            Spacer(modifier = Modifier.height(40.dp))

            AsyncImage(
                model = music?.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {

                IconButton(
                    onClick = {

                        val previous = vm.getPreviousMusic()

                        previous?.let {

                            vm.currentTrack = previous
                            //navController.navigate("player/${it.id}")

                            AudioPlayerController.play(context, it, vm.musicList, vm)
                        }

                    },
                    enabled = musicList.isNotEmpty() && musicList.first() != vm.currentTrack
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

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
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF4FACFE),
                                    Color(0xFF7B61FF),
                                    Color(0xFF9F5DE2)
                                )
                            ),
                            CircleShape
                        )
                ) {

                    Icon(
                        imageVector =
                            if (isPlaying) Icons.Default.Pause
                            else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )

                }

                IconButton(
                    onClick = {

                        val next = vm.getNextMusic()

                        next?.let {
                            vm.currentTrack = next
                            //navController.navigate("player/${it.id}")

                                AudioPlayerController.play(context, it, vm.musicList, vm)
                        }

                    },
                    enabled = musicList.isNotEmpty() && musicList.last() != vm.currentTrack
                ) {
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

fun formatTime(ms: Long): String {

    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%d:%02d".format(minutes, seconds)

}