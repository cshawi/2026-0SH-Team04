package com.example.soundwave.viewModels

import androidx.compose.runtime.derivedStateOf
import com.example.soundwave.data.TestDataProvider
import com.example.soundwave.data.TestDataProvider.likedMusic
import com.example.soundwave.data.TestDataProvider.likedMusics
import com.example.soundwave.data.TestDataProvider.musics
import com.example.soundwave.models.MusicTrack

data class PlaylistItem(val title: String, val trackCount: Int)
data class AlbumItem(val title: String, val subtitle: String)

class LibraryViewModel : BaseViewModel() {

    fun likedMusicsUser() : List<MusicTrack>{
        val user = getUser() ?: return emptyList()
        return musics.filter { likedMusic(it.id, user.id) in likedMusics }
    }

    fun likedCountForUser(): Int{
        return likedMusicsUser().size
    }

    fun playlistsForUser(): List<PlaylistItem> {
        val user = getUser() ?: return emptyList()
        return TestDataProvider.playlists.filter { it.ownerId == user.id }.map { p -> PlaylistItem(title = p.title, trackCount = p.trackIds.size) }
    }

    fun playlistViewsForUser(): List<TestDataProvider.PlaylistView> {
        val user = getUser() ?: return emptyList()
        return TestDataProvider.playlists.filter { it.ownerId == user.id }
    }

    val albums = derivedStateOf { TestDataProvider.playlists.map { p -> AlbumItem(title = p.title, subtitle = "${p.trackIds.size} tracks") } }

    // Placeholder setters kept for API compatibility
    fun setPlaylists(list: List<PlaylistItem>) { /* playlists are derived from TestDataProvider */ }
    fun setAlbums(list: List<AlbumItem>) { /* albums are derived from TestDataProvider */ }
    fun setLikedCount(n: Int) { /* likedCount is derived */ }
}
