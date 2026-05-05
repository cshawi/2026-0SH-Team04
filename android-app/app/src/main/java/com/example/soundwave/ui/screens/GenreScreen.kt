package com.example.soundwave.ui.screens

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.soundwave.data.worker.DownloadWorker
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.util.TimeUtils
import com.example.soundwave.viewModels.CreateViewModel
import com.example.soundwave.viewModels.HomeViewModel
import com.example.soundwave.viewModels.LibraryViewModel
import com.example.soundwave.viewModels.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
                    PopularTrackItem(music, homeViewModel)
                }
            }

        }
    }
}

@Composable
fun PopularTrackItem(music: MusicTrack, homeViewModel: HomeViewModel) {
    val context = LocalContext.current
    val playerViewModel: PlayerViewModel = viewModel(LocalActivity.current)
    val libraryViewModel: LibraryViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0E0E14), RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { AudioPlayerController.play(context, music, homeViewModel.tracksByStyle, playerViewModel) }) {
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
            val displayDuration = if (AudioPlayerController.currentId == music.id && AudioPlayerController.durationMs > 0L) {
                (AudioPlayerController.durationMs / 1000L).toInt()
            } else music.duration

            Text(text = TimeUtils.formatSecondsToMMSS(displayDuration), color = Color(0xFF777788), style = MaterialTheme.typography.bodySmall)
        }

        IconButton(onClick = {
            try {
                libraryViewModel.addMusic(music)
                if (libraryViewModel.getUser() != null) libraryViewModel.persistLike(music)
            } catch (_: Exception) {}
        }) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Like", tint = Color(0xFFEE82FF))
        }

        MusicOptionsMenu(music, libraryViewModel, context, coroutineScope)
    }
}

@Composable
fun MusicOptionsMenu(
    music: MusicTrack,
    libraryViewModel: LibraryViewModel,
    context: Context,
    coroutineScope: CoroutineScope
) {

    val menuExpanded = remember { mutableStateOf(false) }
    val showPlaylistPickerFor = remember { mutableStateOf<String?>(null) }
    val downloadState = remember(music.id) { mutableStateOf<DownloadEntity?>(null) }

    LaunchedEffect(music.id) {
        val store = DownloadStore(context)
        val start = System.currentTimeMillis()
        val timeout = 60_000L

        while (System.currentTimeMillis() - start < timeout) {
            val d = store.getById(music.id)
            downloadState.value = d
            if (d != null && (d.status == "DONE" || d.status == "FAILED")) break
            kotlinx.coroutines.delay(700)
        }

        downloadState.value = store.getById(music.id)
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
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text("${ds.progress}%")
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
                IconButton(
                    onClick = { menuExpanded.value = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = Color.White
                    )
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
                menuExpanded.value = false
                libraryViewModel.addMusic(music)
                libraryViewModel.getUser()?.let { libraryViewModel.persistLike(music) }
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
                    DownloadStore(context).upsert(
                        DownloadEntity(
                            trackId = music.id,
                            title = music.title,
                            localPath = null,
                            status = "DOWNLOADING",
                            progress = 0
                        )
                    )
                }

                val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                    .setInputData(
                        workDataOf(
                            "trackId" to music.id,
                            "audioUrl" to music.audioUrl,
                            "title" to music.title
                        )
                    )
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
                    val store = DownloadStore(context)
                    val download = store.getById(music.id)

                    if (download?.localPath != null) {
                        val file = java.io.File(download.localPath)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "audio/*"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(Intent.createChooser(intent, "Partager"))
                    } else {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "${music.title}\n${music.audioUrl}")
                        }

                        context.startActivity(Intent.createChooser(intent, "Partager via"))
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

    PlaylistDialogs(music, libraryViewModel, context, showPlaylistPickerFor)
}

@Composable
fun PlaylistDialogs(
    music: MusicTrack,
    libraryViewModel: LibraryViewModel,
    context: Context,
    showPlaylistPickerFor: MutableState<String?>
) {

    val createViewModel: CreateViewModel = viewModel()
    val showCreatePlaylist = remember { mutableStateOf(false) }
    var newPlaylistTitle by remember { mutableStateOf("") }
    val playlistsView by libraryViewModel.playlistViewsState

    if (showPlaylistPickerFor.value != null) {
        AlertDialog(
            onDismissRequest = { showPlaylistPickerFor.value = null },
            title = { Text("Ajouter à la playlist") },
            text = {
                Column {
                    playlistsView.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    libraryViewModel.addMusic(music)
                                    libraryViewModel.addTrackToPlaylistServer(p.id, music.id) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Ajouté à ${p.title}", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    showPlaylistPickerFor.value = null
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = p.title, modifier = Modifier.weight(1f))
                            val count = libraryViewModel.getPlaylistTrackCount(p.id)
                            Text(text = "$count tracks", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { showCreatePlaylist.value = true }) {
                            Text("Créer nouvelle playlist")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { showPlaylistPickerFor.value = null }) {
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
                    OutlinedTextField(
                        value = newPlaylistTitle,
                        onValueChange = { newPlaylistTitle = it },
                        label = { Text("Nom de la playlist") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val tid = showPlaylistPickerFor.value
                    if (!newPlaylistTitle.isBlank()) {
                        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            val newId = createViewModel.createPlaylistOnServer(newPlaylistTitle)
                            if (newId != null) {
                                if (tid != null) {
                                    createViewModel.addMusic(music)
                                    libraryViewModel.addTrackToPlaylistServer(newId, tid) {}
                                }
                                Toast.makeText(context, "Playlist créée", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Impossible de créer la playlist", Toast.LENGTH_SHORT).show()
                            }
                            newPlaylistTitle = ""
                            showCreatePlaylist.value = false
                            showPlaylistPickerFor.value = null
                        }
                    }
                }) {
                    Text("Créer")
                }
            },
            dismissButton = {
                Button(onClick = { showCreatePlaylist.value = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}