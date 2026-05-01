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

    // make serverPlaylists observable so Compose recomposes when it changes
    private val serverPlaylists = mutableStateListOf<com.example.soundwave.data.remote.dto.playlist.PlaylistDto>()

    // derived states for UI consumption
    private val playlistItemsState = derivedStateOf {
        serverPlaylists.map { p -> PlaylistItem(title = p.name, trackCount = p.tracks?.size ?: 0) }
    }

    private val playlistViewsState = derivedStateOf {
        serverPlaylists.map { p ->
            val ownerId = getUser()?.id ?: "0"
            val cover = if (p.tracks != null && p.tracks.isNotEmpty()) {
                p.tracks.firstOrNull()?.coverUrl
            }  else null
            val trackIdsList = when {
                p.tracks != null -> p.tracks.map { it.id }
                p.trackIds != null -> p.trackIds
                else -> emptyList()
            }
            PlaylistView(id = p.id, title = p.name, ownerId = ownerId, trackIds = trackIdsList, coverUrl = cover)
        }
    }

    fun playlistsForUser(): List<PlaylistItem> {
        val user = getUser() ?: return emptyList()
        return playlistItemsState.value
    }

    fun playlistViewsForUser(): List<PlaylistView> {
        val user = getUser() ?: return emptyList()
        return playlistViewsState.value
    }

    // serverPlaylists moved above as observable state

    fun addTrackToPlaylistServer(playlistId: String, trackId: String, onComplete: ((Boolean) -> Unit)? = null) {
        val repo = playlistRepository
        viewModelScope.launch {
            try {
                val resp = repo.addTrackToPlaylist(playlistId, trackId).getOrNull()
                if (resp != null) {
                    // update local cache
                    val idx = serverPlaylists.indexOfFirst { it.id == resp.id }
                    if (idx >= 0) serverPlaylists[idx] = resp else serverPlaylists.add(resp)
                    onComplete?.invoke(true)
                } else {
                    onComplete?.invoke(false)
                }
            } catch (e: Exception) {
                onComplete?.invoke(false)
            }
        }
    }

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
            // reconcile using tracks if available, else trackIds
            if (p.tracks != null) {
                val ids = p.tracks.map { it.id }
                if (!ids.contains(trackId)) {
                    val newTracks = p.tracks + com.example.soundwave.data.remote.dto.track.TrackDto(
                        id = trackId,
                        title = "",
                        style = "",
                        audioUrl = "",
                        userId = null,
                        username = null,
                        coverUrl = "",
                        duration = null,
                        description = null,
                        lyrics = null,
                        createdAt = "",
                        updatedAt = ""
                    )
                    serverPlaylists[idx] = p.copy(tracks = newTracks)
                }
            } else {
                val current = p.trackIds ?: emptyList()
                if (!current.contains(trackId)) {
                    serverPlaylists[idx] = p.copy(trackIds = current + trackId)
                }
            }
        }
    }

    val albums = derivedStateOf { serverPlaylists.map { p -> AlbumItem(title = p.name, subtitle = "${p.tracks?.size ?: 0} tracks") } }
    fun getPlaylistTracksById(playlistId: String): List<com.example.soundwave.models.MusicTrack> {
        val idx = serverPlaylists.indexOfFirst { it.id == playlistId }
        if (idx >= 0) {
            val p = serverPlaylists[idx]
            if (p.tracks != null) {
                return p.tracks.mapNotNull { try { com.example.soundwave.models.MusicTrack.fromDto(it) } catch (_: Exception) { null } }
            }
        }
        return emptyList()
    }

    fun getPlaylistTrackCount(playlistId: String): Int {
        val idx = serverPlaylists.indexOfFirst { it.id == playlistId }
        if (idx >= 0) {
            val p = serverPlaylists[idx]
            return p.tracks?.size ?: 0
        }
        return 0
    }
}
