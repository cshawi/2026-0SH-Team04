package com.example.soundwave.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soundwave.ui.LocalActivity
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.soundwave.viewModels.AlbumItem
import com.example.soundwave.viewModels.LibraryViewModel
import com.example.soundwave.viewModels.PlaylistItem
import com.example.soundwave.data.TestDataProvider
import com.example.soundwave.ui.components.AudioPlayerController
import com.example.soundwave.viewModels.PlayerViewModel
import com.example.soundwave.util.TimeUtils


@Composable
fun LibraryScreen(navController: NavController, vm: LibraryViewModel = viewModel()) {
    val albums by vm.albums

    val playlists = vm.playlistsForUser()
    val playlistViews = vm.playlistViewsForUser()
    val likedMusicsUser = vm.likedMusicsUser()
    val playerViewModel: PlayerViewModel = viewModel(LocalActivity.current)

    val expanded = remember { mutableStateMapOf<Int, Boolean>() }

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
                                PlaylistCard(view = view, modifier = Modifier.weight(1f)) {
                                    val current = expanded[view.id] ?: false
                                    if (current) {
                                        expanded.remove(view.id)
                                    } else {
                                        expanded.clear()
                                        expanded[view.id] = true
                                        showLiked = false
                                    }
                                }
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
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)) {
                                view.trackIds.forEach { tid ->
                                    val track = TestDataProvider.musics.firstOrNull { it.id == tid }
                                    if (track != null) {
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
                                                // build playlist's music list in order and set it on the player
                                                val playlistMusicList = view.trackIds.mapNotNull { id -> TestDataProvider.musics.firstOrNull { it.id == id } }
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Albums Récents", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(text = "Vos derniers albums écoutés", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(albums.size) { idx ->
                        val a = albums[idx]
                        AlbumCard(item = a)
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
fun PlaylistCard(view: com.example.soundwave.data.TestDataProvider.PlaylistView, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
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

            val owner = TestDataProvider.users.firstOrNull { it.id == view.ownerId }
            Column(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)) {
                Text(view.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "${view.trackIds.size} tracks • ${owner?.name ?: "Unknown"}", color = Color(0xCCFFFFFF), fontSize = 12.sp)
            }

            if (owner?.avatarUrl != null) {
                AsyncImage(
                    model = owner.avatarUrl,
                    contentDescription = owner.name,
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

