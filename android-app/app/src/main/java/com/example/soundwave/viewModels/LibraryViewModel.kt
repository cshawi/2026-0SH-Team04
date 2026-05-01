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
import com.example.soundwave.models.PlaylistView
import com.example.soundwave.data.repository.TrackRepository
import com.example.soundwave.models.MusicTrack
import kotlinx.coroutines.launch

data class PlaylistItem(val title: String, val trackCount: Int)
data class AlbumItem(val title: String, val subtitle: String)

class LibraryViewModel : BaseViewModel() {

    private val trackRepository = TrackRepository()
    private val playlistRepository = com.example.soundwave.data.repository.PlaylistRepository()

    val generatedList: SnapshotStateList<MusicTrack> = mutableStateListOf()

    fun likedMusicsUser() : List<MusicTrack>{
        val user = getUser() ?: return emptyList()
        return musics.filter { likedMusic(it.id, user.id) in likedMusics }
    }

    fun likedCountForUser(): Int{
        return likedMusicsUser().size
    }

    fun playlistsForUser(): List<PlaylistItem> {
        val user = getUser() ?: return emptyList()
        return serverPlaylists.map { p -> PlaylistItem(title = p.name, trackCount = p.trackIds?.size ?: 0) }
    }

    fun playlistViewsForUser(): List<PlaylistView> {
        val user = getUser() ?: return emptyList()
        return serverPlaylists.map { p ->
            val ownerId = getUser()?.id ?: "0"
            val cover = if (p.trackIds != null && p.trackIds.isNotEmpty()) {
                // try to find a matching track cover in TestDataProvider
                p.trackIds.first()?.coverUrl
            } else null
            PlaylistView(id = p.id, title = p.name, ownerId = ownerId, trackIds = p.trackIds ?: emptyList(), coverUrl = cover)
        }
    }

    private val serverPlaylists: MutableList<com.example.soundwave.data.remote.dto.playlist.PlaylistDto> = mutableListOf()

    fun loadGenerated(){

    }

    fun loadPlaylists(filter: String? = null) {
        val user = getUser() ?: return
        viewModelScope.launch {
            try {
                val resp = playlistRepository.getPlaylists(filter).getOrNull()
                if (resp != null) {
                    serverPlaylists.clear()
                    serverPlaylists.addAll(resp)
                }
            } catch (e: Exception) {
                // ignore and keep test data
            }
        }
    }

    fun createPlaylist(name: String, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val resp = playlistRepository.createPlaylist(name).getOrNull()
                if (resp != null) {
                    serverPlaylists.add(resp)
                    onComplete?.invoke(true)
                } else {
                    onComplete?.invoke(false)
                }
            } catch (e: Exception) {
                onComplete?.invoke(false)
            }
        }
    }

    // Locally update a server playlist by id (mirror adding a track)
    fun addTrackToPlaylistById(playlistId: String, trackId: String) {
        val idx = serverPlaylists.indexOfFirst { it.id == playlistId }
        if (idx >= 0) {
            val p = serverPlaylists[idx]
            val current = p.trackIds ?: emptyList()
            if (!current.contains(trackId)) {
                serverPlaylists[idx] = p.copy(trackIds = current + trackId)
            }
        }
    }

    val albums = derivedStateOf { serverPlaylists.map { p -> AlbumItem(title = p.name, subtitle = "${p.trackIds?.size ?: 0} tracks") } }

    // Placeholder setters kept for API compatibility
    fun setPlaylists(list: List<PlaylistItem>) { /* playlists are derived from TestDataProvider */ }
    fun setAlbums(list: List<AlbumItem>) { /* albums are derived from TestDataProvider */ }
    fun setLikedCount(n: Int) { /* likedCount is derived */ }
}
