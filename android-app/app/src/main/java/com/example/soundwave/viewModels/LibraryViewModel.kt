package com.example.soundwave.viewModels

import androidx.compose.runtime.derivedStateOf
import com.example.soundwave.data.TestDataProvider
import com.example.soundwave.data.TestDataProvider.likedMusic
import com.example.soundwave.data.TestDataProvider.likedMusics
import com.example.soundwave.data.TestDataProvider.musics
import com.example.soundwave.models.MusicTrack
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.soundwave.data.repository.PlaylistRepository
import com.example.soundwave.data.remote.dto.playlist.PlaylistDto
import androidx.compose.runtime.mutableStateOf


data class PlaylistItem(val title: String, val trackCount: Int)
data class AlbumItem(val title: String, val subtitle: String)

class LibraryViewModel : BaseViewModel() {
    private val playlistRepository = PlaylistRepository()

    var playlistsState = mutableStateOf<List<PlaylistDto>>(emptyList())
    var selectedPlaylist = mutableStateOf<PlaylistDto?>(null)
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

    fun getUserPlaylists(): List<PlaylistItem> {
        val user = getUser() ?: return emptyList()

        return TestDataProvider.playlists
            .filter { it.ownerId == user.id }
            .map {
                PlaylistItem(
                    title = it.title,
                    trackCount = it.trackIds.size
                )
            }
    }

    fun fetchPlaylists() {
        viewModelScope.launch {
            val result = playlistRepository.getPlaylists()

            result.onSuccess {
                playlistsState.value = it
            }

            result.onFailure {
                println("Erreur getPlaylists: ${it.message}")
            }
        }
    }

    fun fetchPlaylistById(id: String) {
        viewModelScope.launch {
            val result = playlistRepository.getPlaylistById(id)

            result.onSuccess {
                selectedPlaylist.value = it
            }

            result.onFailure {
                println("Erreur getPlaylistById: ${it.message}")
            }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            val result = playlistRepository.createPlaylist(name)

            result.onSuccess {
                fetchPlaylists() // refresh automatique
            }

            result.onFailure {
                println("Erreur createPlaylist: ${it.message}")
            }
        }
    }

    fun deletePlaylist(id: String) {
        viewModelScope.launch {
            val result = playlistRepository.deletePlaylist(id)

            result.onSuccess {
                fetchPlaylists() // refresh automatique
            }

            result.onFailure {
                println("Erreur deletePlaylist: ${it.message}")
            }
        }
    }

    val albums = derivedStateOf { TestDataProvider.playlists.map { p -> AlbumItem(title = p.title, subtitle = "${p.trackIds.size} tracks") } }

    // Placeholder setters kept for API compatibility
    fun setPlaylists(list: List<PlaylistItem>) { /* playlists are derived from TestDataProvider */ }
    fun setAlbums(list: List<AlbumItem>) { /* albums are derived from TestDataProvider */ }
    fun setLikedCount(n: Int) { /* likedCount is derived */ }
}
