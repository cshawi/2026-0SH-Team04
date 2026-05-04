package com.example.soundwave.ui.screens

import android.util.Log
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.models.PlaylistView
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.util.TimeUtils
import com.example.soundwave.viewModels.LibraryViewModel
import com.example.soundwave.viewModels.PlayerViewModel
import com.example.soundwave.viewModels.PlaylistItem


@Composable
fun LibraryScreen(vm: LibraryViewModel = viewModel()) {
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
                        MusicListItem(track = track, libraryViewModel = vm, onPlay = { context ->
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
fun MusicListItem(track: MusicTrack, libraryViewModel: LibraryViewModel, onPlay: (android.content.Context) -> Unit) {
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
                                libraryViewModel.addMusic(track)
                                if (libraryViewModel.getUser() != null) libraryViewModel.addToLiked(track.id)
                            } catch (_: Exception) {}
                        }) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Like", tint = Color(0xFFB65EFF))
                        }

                        MusicOptionsMenu(track, libraryViewModel, context, coroutineScope)
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




