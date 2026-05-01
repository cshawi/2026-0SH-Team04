package com.example.soundwave.viewModels

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.example.soundwave.data.TestDataProvider
import com.example.soundwave.data.TestDataProvider.likedMusic
import com.example.soundwave.data.TestDataProvider.likedMusics
import com.example.soundwave.data.TestDataProvider.musics
import com.example.soundwave.data.repository.TrackRepository
import com.example.soundwave.models.MusicTrack
import kotlinx.coroutines.launch

data class PlaylistItem(val title: String, val trackCount: Int)
data class AlbumItem(val title: String, val subtitle: String)

class LibraryViewModel : BaseViewModel() {

    private val trackRepository = TrackRepository()

    val generatedList: SnapshotStateList<MusicTrack> = mutableStateListOf()

    fun loadGenerated() {
        viewModelScope.launch {
            try {
                val resp = trackRepository.getGenerated().getOrNull()
                if (resp != null) {
                    val mapped = resp.mapNotNull { dto ->
                        try { MusicTrack.fromDto(dto) } catch (e: Exception) { null }
                    }
                    generatedList.clear()
                    generatedList.addAll(mapped)
                }
            } catch (e: Exception) {
                Log.d("LibraryViewModel", "loadGenerated failed: ${e.message}")
            }
        }
    }

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
