package com.example.soundwave.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.soundwave.data.TestDataProvider

data class PlaylistItem(val title: String, val trackCount: Int)
data class AlbumItem(val title: String, val subtitle: String)

class LibraryViewModel : ViewModel() {
    val likedCount = derivedStateOf { TestDataProvider.likedMusicIds.size }

    val playlists = derivedStateOf { TestDataProvider.playlists.map { p -> PlaylistItem(title = p.title, trackCount = p.trackIds.size) } }
    val playlistViews = derivedStateOf { TestDataProvider.playlists.toList() }
    val albums = derivedStateOf { TestDataProvider.playlists.map { p -> AlbumItem(title = p.title, subtitle = "${p.trackIds.size} tracks") } }

    // Placeholder setters kept for API compatibility
    fun setPlaylists(list: List<PlaylistItem>) { /* playlists are derived from TestDataProvider */ }
    fun setAlbums(list: List<AlbumItem>) { /* albums are derived from TestDataProvider */ }
    fun setLikedCount(n: Int) { /* likedCount is derived */ }
}
