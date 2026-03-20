package com.example.soundwave.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import com.example.soundwave.data.TestDataProvider

data class PlaylistItem(val title: String, val trackCount: Int)
data class AlbumItem(val title: String, val subtitle: String)

class LibraryViewModel : ViewModel() {
    val likedCount = derivedStateOf { TestDataProvider.likedMusicIds.size }

    fun playlistsForUser(userId: Int?): List<PlaylistItem> {
        if (userId == null) return emptyList()
        return TestDataProvider.playlists.filter { it.ownerId == userId }.map { p -> PlaylistItem(title = p.title, trackCount = p.trackIds.size) }
    }

    fun playlistViewsForUser(userId: Int?): List<TestDataProvider.PlaylistView> {
        if (userId == null) return emptyList()
        return TestDataProvider.playlists.filter { it.ownerId == userId }
    }

    val albums = derivedStateOf { TestDataProvider.playlists.map { p -> AlbumItem(title = p.title, subtitle = "${p.trackIds.size} tracks") } }

    // Placeholder setters kept for API compatibility
    fun setPlaylists(list: List<PlaylistItem>) { /* playlists are derived from TestDataProvider */ }
    fun setAlbums(list: List<AlbumItem>) { /* albums are derived from TestDataProvider */ }
    fun setLikedCount(n: Int) { /* likedCount is derived */ }
}
