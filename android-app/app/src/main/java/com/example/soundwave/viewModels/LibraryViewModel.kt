package com.example.soundwave.viewModels

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.example.soundwave.data.TestDataProvider.likedMusic
import com.example.soundwave.data.TestDataProvider.likedMusics
import com.example.soundwave.data.TestDataProvider.musics
import com.example.soundwave.data.local.LikedTrackEntity
import com.example.soundwave.data.remote.dto.playlist.PlaylistDto
import com.example.soundwave.data.repository.TrackRepository
import com.example.soundwave.events.AppEvents
import com.example.soundwave.events.AppEvents.trackMetadataUpdated
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.models.PlaylistView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class PlaylistItem(val title: String, val trackCount: Int)
data class AlbumItem(val title: String, val subtitle: String)

class LibraryViewModel : BaseViewModel() {

    private val trackRepository = TrackRepository()
    private val playlistRepository = com.example.soundwave.data.repository.PlaylistRepository()
    // initialize a compat store: try Room-backed store if AppContext and Room are available; else fallback to file store
    private val likedStoreCompat = try {
        val ctx = com.example.soundwave.AppContext.context
        val room = try { com.example.soundwave.data.local.LikedTrackStore(ctx) } catch (_: Exception) { null }
        val file = try { com.example.soundwave.data.local.LikedTrackFileStore(ctx) } catch (_: Exception) { null }
        com.example.soundwave.data.local.LikedTrackStoreCompat(room = room, file = file)
    } catch (_: Throwable) {
        // Last-resort: no context available
        com.example.soundwave.data.local.LikedTrackStoreCompat(null, null)
    }
    val generatedList: SnapshotStateList<MusicTrack> = mutableStateListOf()
    // persisted liked tracks per connected user
    val likedTracks: SnapshotStateList<MusicTrack> = mutableStateListOf()

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

    init {
        // listen for app-level events (e.g., after login) to preload library data
        viewModelScope.launch {
            AppEvents.libraryLoadTrigger.collectLatest {
                loadPlaylists()
                loadGenerated()
                loadLikedForCurrentUser()
            }
        }

        // listen for metadata-updated events (e.g., duration discovered by player) and
        // update our in-memory lists so the corrected duration persists in UI
        viewModelScope.launch {
            trackMetadataUpdated.collectLatest { updated ->
                try {
                    // update generatedList
                    val genIdx = generatedList.indexOfFirst { it.id == updated.id }
                    if (genIdx >= 0) {
                        val existing = generatedList[genIdx]
                        if (existing.duration != updated.duration) {
                            generatedList[genIdx] = existing.copy(duration = updated.duration)
                        }
                    }

                    // update likedTracks
                    val likedIdx = likedTracks.indexOfFirst { it.id == updated.id }
                    if (likedIdx >= 0) {
                        val existing = likedTracks[likedIdx]
                        if (existing.duration != updated.duration) {
                            likedTracks[likedIdx] = existing.copy(duration = updated.duration)
                        }
                    }

                    // update serverPlaylists cache where tracks are derived -> update PlaylistDto entries if present
                    // This only updates our in-memory mapping used for playlistViews; if needed, next loadPlaylists will refresh.
                    for (i in serverPlaylists.indices) {
                        val p = serverPlaylists[i]
                        if (!p.tracks.isNullOrEmpty()) {
                            val idx = p.tracks.indexOfFirst { it.id == updated.id }
                            if (idx >= 0) {
                                val t = p.tracks[idx]
                                val newTrackDto = t.copy(duration = updated.duration.toDouble())
                                val newTracks = p.tracks.toMutableList()
                                newTracks[idx] = newTrackDto
                                serverPlaylists[i] = p.copy(tracks = newTracks)
                            }
                        }
                    }
                } catch (_: Exception) {
                    // be defensive and ignore issues here
                }
            }
        }
    }

    fun likedMusicsUser() : List<MusicTrack>{
        // prefer persisted liked tracks if available
        if (likedTracks.isNotEmpty()) return likedTracks.toList()
        val user = getUser() ?: return emptyList()
        return musics.filter { likedMusic(it.id, user.id) in likedMusics }
    }

    fun likedCountForUser(): Int{
        return likedMusicsUser().size
    }

    private fun musicTrackToEntity(track: MusicTrack, userId: String) : LikedTrackEntity {
        return LikedTrackEntity(
            id = track.id,
            title = track.title,
            styleName = track.styleName,
            duration = track.duration,
            createdAt = track.createdAt,
            audioUrl = track.audioUrl,
            coverUrl = track.coverUrl,
            lyrics = track.lyrics,
            username = track.username,
            userId = userId
        )
    }

    fun loadLikedForCurrentUser() {

        val user = getUser()
        Log.d("LVVM", "loadLikedForCurrentUser called, user=$user")
        if (user == null) {
            Log.d("LVVM", "no user - aborting loadLikedForCurrentUser")
            return
        }

        viewModelScope.launch {
            Log.d("LVVM", "loading liked tracks for userId=${user.id}")
            try {
                val entities = likedStoreCompat.getForUser(user.id)

                likedTracks.clear()
                likedTracks.addAll(entities.mapNotNull { e ->
                    try {
                        MusicTrack(
                            id = e.id,
                            title = e.title,
                            styleName = e.styleName,
                            duration = e.duration,
                            createdAt = e.createdAt,
                            audioUrl = e.audioUrl,
                            coverUrl = e.coverUrl,
                            lyrics = e.lyrics,
                            username = e.username
                        )
                    } catch (ex: Exception) {
                        Log.w("LVVM", "mapping entity->MusicTrack failed: ${ex.message}")
                        null
                    }
                })
                Log.d("LVVM", "likedTracks populated: ${likedTracks.size}")
            } catch (e: Exception) {
                Log.w("LVVM", "loadLikedForCurrentUser error: ${e.message}")
            }
        }
    }

    fun persistLike(track: MusicTrack) {
        val user = getUser() ?: return

        viewModelScope.launch {
            try {
                likedStoreCompat.upsert(musicTrackToEntity(track, user.id))
                Log.d("LVVM", "upsert complete for track=${track.id} userId=${user.id}")
                // keep in-memory list in sync
                if (likedTracks.none { it.id == track.id }) {
                    likedTracks.add(track)
                }
            } catch (e: Exception) {
                Log.w("LVVM", "persistLike error: ${e.message}")
            }
        }
    }

    fun persistUnlike(trackId: String) {
        val user = getUser() ?: return
        viewModelScope.launch {
            try {
                likedStoreCompat.deleteByIdForUser(trackId, user.id)
                likedTracks.removeAll { it.id == trackId }
            } catch (_: Exception) {}
        }
    }

    // make serverPlaylists observable so Compose recomposes when it changes
    private val serverPlaylists = mutableStateListOf<PlaylistDto>()

    // derived states for UI consumption
    val playlistItemsState = derivedStateOf {
        serverPlaylists.map { p -> PlaylistItem(title = p.name, trackCount = p.tracks?.size ?: 0) }
    }

    val playlistViewsState = derivedStateOf {
        serverPlaylists.map { p ->
            val ownerId = getUser()?.id ?: "0"
            val cover = if (!p.tracks.isNullOrEmpty()) {
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
        Log.d("LVVM", playlistViewsState.value.toString())
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


    fun loadPlaylists(filter: String? = null) {
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
    fun getPlaylistTracksById(playlistId: String): List<MusicTrack> {
        val idx = serverPlaylists.indexOfFirst { it.id == playlistId }
        if (idx >= 0) {
            val p = serverPlaylists[idx]
            if (p.tracks != null) {
                return p.tracks.mapNotNull { try { MusicTrack.fromDto(it) } catch (_: Exception) { null } }
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
