package com.example.soundwave.ui.screens

import android.content.Intent
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
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.soundwave.ui.components.AudioPlayerController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.soundwave.data.local.DownloadEntity
import com.example.soundwave.data.local.DownloadStore
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.viewModels.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlayerScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel
) {

    val context = LocalContext.current
    val activity = LocalActivity.current
    val vm: PlayerViewModel = playerViewModel
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
                
                if(music == null){
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
                else{

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, -30) }
                            .padding(5.dp, 0.dp)
                    ) {
                        val menuExpandedFor = remember { mutableStateOf<String?>(null) }
                        val showPlaylistPickerFor = remember { mutableStateOf<String?>(null) }
                        val showCreatePlaylist = remember { mutableStateOf(false) }
                        var newPlaylistTitle by remember { mutableStateOf("") }
                        val downloadState = remember(music.id) { mutableStateOf<DownloadEntity?>(null) }
                        val user = playerViewModel.getUser()
                        val coroutineScope = rememberCoroutineScope()

                        LaunchedEffect(music.id) {
                            val store = DownloadStore(context)
                            val start = System.currentTimeMillis()
                            val timeout = 60_000L
                            while (System.currentTimeMillis() - start < timeout) {
                                val d = store.getById(music.id)
                                downloadState.value = d
                                if (d != null && (d.status == "DONE" || d.status == "FAILED")) break
                                delay(700)
                            }
                            downloadState.value = store.getById(music.id)
                        }

                        val ds = downloadState.value

                        Box(modifier = Modifier.clickable { menuExpandedFor.value = music.id }) {
                            when (ds?.status) {
                                "DOWNLOADING" -> {
                                    val pf = (ds.progress.coerceIn(0, 100)) / 100f
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(
                                            progress = { pf },
                                            modifier = Modifier.size(28.dp),
                                            color = MaterialTheme.colorScheme.secondary,
                                            strokeWidth = 2.dp,
                                            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                                            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${ds.progress}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                                "DONE" -> {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "...",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.clickable {
                                            menuExpandedFor.value = music.id
                                        },
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = menuExpandedFor.value == music.id,
                            onDismissRequest = { menuExpandedFor.value = null }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Ajouter à une playlist") },
                                onClick = {
                                    menuExpandedFor.value = null
                                    showPlaylistPickerFor.value = music.id
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Ajouter aux favoris") },
                                onClick = {
                                    menuExpandedFor.value = null
                                    try {
                                        playerViewModel.addMusic(music)
                                        if (user != null) playerViewModel.addToLiked(music.id)
                                    } catch (_: Exception) {}
                                    Toast.makeText(
                                        context,
                                        "Ajouté aux favoris",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Télécharger") },
                                onClick = {
                                    menuExpandedFor.value = null

                                    coroutineScope.launch {
                                        try {
                                            val store = DownloadStore(context)
                                            store.upsert(DownloadEntity(trackId = music.id, title = music.title, localPath = null, status = "DOWNLOADING", progress = 0))
                                        } catch (_: Exception) {
                                        }
                                    }

                                    val data = workDataOf(
                                        "trackId" to music.id,
                                        "audioUrl" to music.audioUrl,
                                        "title" to music.title
                                    )
                                    val request = OneTimeWorkRequestBuilder<com.example.soundwave.data.worker.DownloadWorker>()
                                        .setInputData(data)
                                        .build()
                                    WorkManager.getInstance(context).enqueue(request)
                                    Toast.makeText(context, "Téléchargement lancé", Toast.LENGTH_SHORT).show()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Partager") },
                                onClick = {
                                    menuExpandedFor.value = null
                                    coroutineScope.launch {
                                        try {
                                            val store = DownloadStore(context)
                                            val download = withContext(kotlinx.coroutines.Dispatchers.IO) { store.getById(music.id) }
                                            if (download?.localPath != null) {
                                                val file = java.io.File(download.localPath)
                                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                                val share = Intent(Intent.ACTION_SEND).apply {
                                                    type = "audio/*"
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(share, "Partager la piste"))
                                            } else {
                                                val shareText = "${music.title}\n${music.audioUrl}"
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_SUBJECT, music.title)
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Partager via"))
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Impossible d'ouvrir le partage", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            )
                        }
                    }
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

                text = music?.username ?: "AI",
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