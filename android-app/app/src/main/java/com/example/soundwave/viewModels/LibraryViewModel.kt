package com.example.soundwave.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.soundwave.data.TestDataProvider

data class PlaylistItem(val title: String, val trackCount: Int)
data class AlbumItem(val title: String, val subtitle: String)

class LibraryViewModel : ViewModel() {
    // number of liked tracks (derived from TestDataProvider snapshot state list)
    val likedCount = derivedStateOf { TestDataProvider.likedMusicIds.size }

    // playlists for UI (title + count) - derived so it updates when TestDataProvider.playlists changes
    val playlists = derivedStateOf { TestDataProvider.playlists.map { p -> PlaylistItem(title = p.title, trackCount = p.trackIds.size) } }

    // expose playlist views (contains ownerId, coverUrl, trackIds) as a snapshot list copy
    val playlistViews = derivedStateOf { TestDataProvider.playlists.toList() }

    // recent albums
    val albums = derivedStateOf { TestDataProvider.playlists.map { p -> AlbumItem(title = p.title, subtitle = "${p.trackIds.size} tracks") } }

    // Placeholder setters kept for API compatibility (they'll overwrite derived state if needed)
    fun setPlaylists(list: List<PlaylistItem>) { /* no-op: playlists are derived from TestDataProvider */ }
    fun setAlbums(list: List<AlbumItem>) { /* no-op: albums are derived from TestDataProvider */ }
    fun setLikedCount(n: Int) { /* no-op: likedCount is derived */ }
}
