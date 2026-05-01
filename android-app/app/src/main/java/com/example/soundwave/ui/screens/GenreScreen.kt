package com.example.soundwave.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.example.soundwave.data.local.DownloadEntity
import com.example.soundwave.data.local.DownloadStore
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.util.TimeUtils
import com.example.soundwave.viewModels.HomeViewModel
import com.example.soundwave.viewModels.LibraryViewModel
import com.example.soundwave.viewModels.PlayerViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("LocalContextResourcesRead", "DiscouragedApi")
@Composable
fun GenreScreen(
    genreName: String,
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {


    // request server for tracks of this genre
    LaunchedEffect(genreName) {
        homeViewModel.getMusicByStyle(genreName)
    }

    val musics = homeViewModel.tracksByStyle.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }

                Text(
                    text = "Musique par style",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current
            val headerResName = "style_image"
            val resId = context.resources.getIdentifier(headerResName, "drawable", context.packageName)
            val headerImageModel: Any? = if (resId != 0) resId else musics.firstOrNull()?.coverUrl

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F17))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = headerImageModel,
                        contentDescription = genreName,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = genreName, color = Color(0xFFEE82FF), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Des musiques selon votre style.", color = Color(0xFFBFBFBF), maxLines = 3)
                        Spacer(modifier = Modifier.height(8.dp))

                        val size = musics.size.toString()
                        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF1E004D)) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFEE82FF), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "$size musiques", color = Color(0xFFEE82FF), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(text = "Titres populaires", color = Color.White, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(musics) { music ->
                    PopularTrackItem(music)
                }
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
                text = music.username ?: "AI",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
fun PopularTrackItem(music: MusicTrack) {
    val context = LocalContext.current
    val playerViewModel: PlayerViewModel = viewModel(LocalActivity.current)
    val vm: com.example.soundwave.viewModels.LibraryViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0E0E14), RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { AudioPlayerController.play(context, music, listOf(music), playerViewModel) }) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
        }

        AsyncImage(
            model = music.coverUrl,
            contentDescription = music.title,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = music.title, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            Text(text = music.username ?: "AI", color = Color(0xFFB0B0C2), style = MaterialTheme.typography.bodySmall)
            Text(text = TimeUtils.formatSecondsToMMSS(music.duration), color = Color(0xFF777788), style = MaterialTheme.typography.bodySmall)
        }

        IconButton(onClick = {
            try {
                vm.addMusic(music)
                if (vm.getUser() != null) vm.addToLiked(music.id)
            } catch (_: Exception) {}
        }) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Like", tint = Color(0xFFEE82FF))
        }

        val menuExpanded = remember { mutableStateOf(false) }
        val showPlaylistPicker = remember { mutableStateOf(false) }
        val downloadState = remember(music.id) { mutableStateOf<DownloadEntity?>(null) }

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
            downloadState.value = DownloadStore(context).getById(music.id)
        }

        val ds = downloadState.value

        Box(modifier = Modifier.clickable { menuExpanded.value = true }) {
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
                    IconButton(onClick = { menuExpanded.value = true }, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                    }
                }
            }
        }

        DropdownMenu(
            expanded = menuExpanded.value,
            onDismissRequest = { menuExpanded.value = false }
        ) {
            DropdownMenuItem(
                text = { Text("Ajouter à une playlist") },
                onClick = {
                    menuExpanded.value = false
                    showPlaylistPicker.value = true
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
                    menuExpanded.value = false
                    try {
                        vm.addMusic(music)
                        if (vm.getUser() != null) vm.addToLiked(music.id)
                    } catch (_: Exception) {}
                    Toast.makeText(context, "Ajouté aux favoris", Toast.LENGTH_SHORT).show()
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
                    menuExpanded.value = false
                    coroutineScope.launch {
                        try {
                            val store = DownloadStore(context)
                            store.upsert(DownloadEntity(trackId = music.id, title = music.title, localPath = null, status = "DOWNLOADING", progress = 0))
                        } catch (_: Exception) {}
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
                    menuExpanded.value = false
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

        if (showPlaylistPicker.value) {
            AlertDialog(
                onDismissRequest = { showPlaylistPicker.value = false },
                title = { Text("Ajouter à la playlist") },
                text = {
                    Column {
                        vm.playlistsForUser().forEach { p ->
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    vm.addMusic(music)
                                    vm.addTrackToPlaylist(vm.playlistsForUser().indexOf(p)+1, music.id)
                                    showPlaylistPicker.value = false
                                    Toast.makeText(context, "Ajouté à ${p.title}", Toast.LENGTH_SHORT).show()
                                }
                                .padding(8.dp)) {
                                Text(p.title)
                            }
                        }
                        if (vm.playlistsForUser().isEmpty()) {
                            Text("Aucune playlist trouvée", color = Color.Gray)
                        }
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { showPlaylistPicker.value = false }) {
                        Text("Fermer")
                    }
                }
            )
        }

        if (showCreatePlaylist.value) {
            AlertDialog(
                onDismissRequest = { showCreatePlaylist.value = false },
                title = { Text("Créer une playlist") },
                text = {
                    Column {
                        OutlinedTextField(value = newPlaylistTitle, onValueChange = { newPlaylistTitle = it }, label = { Text("Nom de la playlist") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val tid = showPlaylistPickerFor.value
                        if (!newPlaylistTitle.isBlank()) {
                            val newId = createViewModel.createPlaylist(newPlaylistTitle)
                            if (tid != null) {
                                createViewModel.addMusic(generationResult.tracks.first { it.id == tid })
                                createViewModel.addTrackToPlaylist(newId, tid)
                            }
                            Toast.makeText(context, "Playlist créée", Toast.LENGTH_SHORT).show()
                            newPlaylistTitle = ""
                            showCreatePlaylist.value = false
                            showPlaylistPickerFor.value = null
                        }
                    }) { Text("Créer") }
                },
                dismissButton = {
                    Button(onClick = { showCreatePlaylist.value = false }) { Text("Annuler") }
                }
            )
        }
    }
}