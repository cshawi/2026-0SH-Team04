package com.example.soundwave.ui.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.example.soundwave.models.PlaylistView
import com.example.soundwave.data.local.DownloadEntity
import com.example.soundwave.data.local.DownloadStore
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.util.TimeUtils
import com.example.soundwave.viewModels.AlbumItem
import com.example.soundwave.viewModels.LibraryViewModel
import com.example.soundwave.viewModels.PlayerViewModel
import com.example.soundwave.viewModels.CreateViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import com.example.soundwave.viewModels.PlaylistItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun LibraryScreen(navController: NavController, vm: LibraryViewModel = viewModel()) {
    val albums by vm.albums
    val generatedList = vm.generatedList

    val playlists by vm.playlistItemsState
    val playlistViews by vm.playlistViewsState
    val likedMusicsUser = vm.likedMusicsUser()
    val playerViewModel: PlayerViewModel = viewModel(LocalActivity.current)

    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    var showLiked by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            LaunchedEffect(Unit) { vm.loadPlaylists()
                Log.d("LScreen", playlists.toString())
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Bibliothèque",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 34.sp, fontWeight = FontWeight.Bold)
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Rechercher",
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (showLiked) {
                        showLiked = false
                    } else {
                        expanded.clear()
                        showLiked = true
                    }
                }) {
                Row(modifier = Modifier
                    .background(brush = Brush.horizontalGradient(listOf(Color(0xFF6B1EFF), Color(0xFFA23CFF))))
                    .padding(16.dp),
                     verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Morceaux Likés", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "Vos chansons préférées", color = Color(0xCCFFFFFF), fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Titres Likés", color = Color.White, modifier = Modifier.padding(end = 8.dp))
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }
            }


            if (showLiked) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    likedMusicsUser.forEach { track ->
                        val context = LocalContext.current
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                    model = track.coverUrl,
                                contentDescription = track.title,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = track.title, color = Color.White)
                                Text(text = TimeUtils.formatSecondsToMMSS(track.duration), color = Color(0xFFB0B0C2), fontSize = 12.sp)
                            }

                            IconButton(onClick = {
                                // when playing from liked list, set player music list to liked items
                                AudioPlayerController.play(context, track, likedMusicsUser, playerViewModel)
                            }) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }

            if(!playlists.isEmpty()){
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Playlists", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "Voir toutes vos playlists créées", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                val rows = playlists.chunked(2)
                rows.forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { p ->
                            val view = playlistViews.firstOrNull { it.title == p.title }
                            if (view != null) {
                                PlaylistCard(view = view, modifier = Modifier.weight(1f), onClick = {
                                    val current = expanded[view.id] ?: false
                                    if (current) {
                                        expanded.remove(view.id)
                                    } else {
                                        expanded.clear()
                                        expanded[view.id] = true
                                        showLiked = false
                                    }
                                })
                            } else {
                                PlaylistCard(item = p, modifier = Modifier.weight(1f))
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    rowItems.forEach { p ->
                        val view = playlistViews.firstOrNull { it.title == p.title }
                        if (view != null && (expanded[view.id] == true)) {
                            val playlistMusicList = vm.getPlaylistTracksById(view.id)
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)) {
                                playlistMusicList.forEach { track ->
                                    val context = LocalContext.current
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = track.coverUrl,
                                            contentDescription = track.title,
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = track.title, color = Color.White)
                                            Text(text = TimeUtils.formatSecondsToMMSS(track.duration), color = Color(0xFFB0B0C2), fontSize = 12.sp)
                                        }

                                        IconButton(onClick = {
                                            // play this playlist
                                            AudioPlayerController.play(context, track, playlistMusicList, playerViewModel)
                                        }) {
                                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                                        }

                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Vos créations", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(text = "Vos musiques créées", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LaunchedEffect(Unit) { vm.loadGenerated() }
                Column(modifier = Modifier.fillMaxWidth()) {
                    generatedList.forEach { track ->
                        MusicListItem(track = track, vm = vm, onPlay = { context ->
                            AudioPlayerController.play(
                                context, track,
                                generatedList, playerViewModel
                            )
                        })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun MusicListItem(track: com.example.soundwave.models.MusicTrack, vm: LibraryViewModel, onPlay: (android.content.Context) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier
        .fillMaxWidth()
        .height(76.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

            Box(modifier = Modifier.size(64.dp)) {
                AsyncImage(
                    model = track.coverUrl,
                    contentDescription = track.title,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                IconButton(onClick = { onPlay(context) }, modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = track.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = track.username ?: track.username ?: "AI", color = Color(0xFFB0B0C2), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = TimeUtils.formatSecondsToMMSS(track.duration), color = Color(0xFFB0B0C2), fontSize = 12.sp)
                        Row {
                            IconButton(onClick = {
                                try {
                                    vm.addMusic(track)
                                    if (vm.getUser() != null) vm.addToLiked(track.id)
                                } catch (_: Exception) {}
                            }) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Like", tint = Color(0xFFB65EFF))
                            }

                            val menuExpanded = remember { mutableStateOf(false) }
                            val showPlaylistPickerFor = remember { mutableStateOf<String?>(null) }
                            val showCreatePlaylist = remember { mutableStateOf(false) }
                            var newPlaylistTitle by remember { mutableStateOf("") }
                            val createViewModel: CreateViewModel = viewModel()
                            val downloadState = remember(track.id) { mutableStateOf<DownloadEntity?>(null) }

                            LaunchedEffect(track.id) {
                                val store = DownloadStore(context)
                                val start = System.currentTimeMillis()
                                val timeout = 60_000L
                                while (System.currentTimeMillis() - start < timeout) {
                                    val d = store.getById(track.id)
                                    downloadState.value = d
                                    if (d != null && (d.status == "DONE" || d.status == "FAILED")) break
                                    delay(700)
                                }
                                downloadState.value = DownloadStore(context).getById(track.id)
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
                                        showPlaylistPickerFor.value = track.id
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
                                            vm.addMusic(track)
                                            if (vm.getUser() != null) vm.addToLiked(track.id)
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
                                                store.upsert(DownloadEntity(trackId = track.id, title = track.title, localPath = null, status = "DOWNLOADING", progress = 0))
                                            } catch (_: Exception) {}
                                        }

                                        val data = workDataOf(
                                            "trackId" to track.id,
                                            "audioUrl" to track.audioUrl,
                                            "title" to track.title
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
                                                val download = withContext(kotlinx.coroutines.Dispatchers.IO) { store.getById(track.id) }
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
                                                    val shareText = "${track.title}\n${track.audioUrl}"
                                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "text/plain"
                                                        putExtra(Intent.EXTRA_SUBJECT, track.title)
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

                            if (showPlaylistPickerFor.value != null) {
                                val tid = showPlaylistPickerFor.value!!
                                AlertDialog(
                                    onDismissRequest = { showPlaylistPickerFor.value = null },
                                    title = { Text("Ajouter à la playlist") },
                                    text = {
                                        Column {
                                            vm.playlistViewsForUser().forEach { p ->
                                                Row(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        // add to local collection and to server
                                                        vm.addMusic(track)
                                                        vm.addTrackToPlaylistServer(p.id, track.id) { success ->
                                                            if (success) Toast.makeText(context, "Ajouté à ${p.title}", Toast.LENGTH_SHORT).show()
                                                            else Toast.makeText(context, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                                                        }
                                                        showPlaylistPickerFor.value = null
                                                    }
                                                    .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Text(text = p.title, modifier = Modifier.weight(1f))
                                                    val count = vm.getPlaylistTrackCount(p.id)
                                                    Text(text = "$count tracks", color = Color.Gray)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                                Button(onClick = { showCreatePlaylist.value = true }) {
                                                    Text("Créer nouvelle playlist")
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {},
                                    dismissButton = {
                                        Button(onClick = { showPlaylistPickerFor.value = null }) { Text("Fermer") }
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
                                                coroutineScope.launch {
                                                    val newId = createViewModel.createPlaylistOnServer(newPlaylistTitle)
                                                    if (newId != null) {
                                                        if (tid != null) {
                                                            vm.addMusic(track)
                                                            vm.addTrackToPlaylistServer(newId, tid) {}
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
                                        }) { Text("Créer") }
                                    },
                                    dismissButton = {
                                        Button(onClick = { showCreatePlaylist.value = false }) { Text("Annuler") }
                                    }
                                )
                            }
                        }
                    }
        }
    }
}


@Composable
fun PlaylistCard(item: PlaylistItem, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Box(modifier = Modifier
            .height(120.dp)
            .fillMaxWidth()) {
            Box(modifier = Modifier
                .matchParentSize()
                .background(brush = Brush.linearGradient(listOf(Color(0xFF5B8CFF), Color(0xFF9B59FF)))))

            Column(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)) {
                Text(item.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "${item.trackCount} trãcks", color = Color(0xCCFFFFFF), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PlaylistCard(view: PlaylistView, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, vm: LibraryViewModel = viewModel()) {
    val cardModifier = if (onClick != null) modifier.clickable { onClick() } else modifier
    Card(shape = RoundedCornerShape(12.dp), modifier = cardModifier) {
        Box(modifier = Modifier
            .height(140.dp)
            .fillMaxWidth()) {

            if (!view.coverUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = view.coverUrl,
                    contentDescription = view.title,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier
                    .matchParentSize()
                    .background(brush = Brush.linearGradient(listOf(Color(0xFF5B8CFF), Color(0xFF9B59FF)))))
            }

            // derive owner info from ViewModel user or fallback
            val ownerUser = vm.getUser()
            val ownerName = ownerUser?.name ?: "AI"
            val ownerAvatar = ownerUser?.avatarUrl

            Column(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)) {
                Text(view.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                val count = vm.getPlaylistTrackCount(view.id)
                Text(text = "$count tracks • $ownerName", color = Color(0xCCFFFFFF), fontSize = 12.sp)
            }

            if (!ownerAvatar.isNullOrEmpty()) {
                AsyncImage(
                    model = ownerAvatar,
                    contentDescription = ownerName,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

        }
    }
}


@Composable
fun AlbumCard(item: AlbumItem) {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier
        .width(180.dp)
        .height(140.dp)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(painter = ColorPainter(Color(0xFF6E2CE3)), contentDescription = null, modifier = Modifier.matchParentSize())
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)) {
                Text(item.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(item.subtitle, color = Color(0xCCFFFFFF), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

