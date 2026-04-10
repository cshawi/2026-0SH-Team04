package com.example.soundwave.viewModels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.example.soundwave.data.TestDataProvider
import com.example.soundwave.data.TestDataProvider.PlaylistView
import com.example.soundwave.data.TestDataProvider.likedMusic
import com.example.soundwave.data.TestDataProvider.likedMusics
import com.example.soundwave.data.TestDataProvider.musics
import com.example.soundwave.data.TestDataProvider.playlists
import com.example.soundwave.data.repository.UserSession
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.models.User

open class BaseViewModel: ViewModel() {

    // Add a music track to the test dataset if not already present

    fun getUser(): User?{
        return UserSession.currentUser.value
    }

    fun addMusic(track: MusicTrack) {
        if (musics.none { it.id == track.id }) {
            musics.add(track)
        }
    }

    fun addToLiked(trackId: String) {
        val user = getUser()
        if(user != null){
            val music = likedMusic(trackId, user.id)
            if (!likedMusics.contains(music)) likedMusics.add(music)
        }
    }

    fun removeFromLiked(trackId: String) {
        val user = getUser()
        if (user != null) {
            val music = likedMusic(trackId, user.id)
            likedMusics.remove(music)
        }
    }

    fun addTrackToPlaylist(playlistId: Int, trackId: String) {
        val idx = playlists.indexOfFirst { it.id == playlistId }
        if (idx >= 0) {
            val p = playlists[idx]
            if (!p.trackIds.contains(trackId)) {
                playlists[idx] = p.copy(trackIds = p.trackIds + trackId)
            }
        }
    }

    // Create a new playlist and optionally add the track
    fun createPlaylist(title: String, initialTrackId: String? = null): Int {
        val user = getUser() ?: return 0

        val newId = playlists.size + 1
        val tracks = if (initialTrackId != null) listOf(initialTrackId) else emptyList()
        val cover = musics.firstOrNull()?.coverUrl
        val p = PlaylistView(id = newId, title = title, ownerId = user.id, trackIds = tracks, coverUrl = cover)
        playlists.add(p)
        return newId
    }

}