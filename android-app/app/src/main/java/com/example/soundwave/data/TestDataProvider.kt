package com.example.soundwave.data

import com.example.soundwave.models.MusicTrack
import com.example.soundwave.models.User
import com.example.soundwave.models.StyleItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateListOf

// Provider of in-memory test data
object TestDataProvider {

    // Users (ids as Int)
    val users = listOf(
        User(id = 1, name = "Alice", email = "alice@music.com", password = "password", avatarUrl = "https://i.pravatar.cc/150?img=1"),
        User(id = 2, name = "Bob", email = "bob@music.com", password = "password", avatarUrl = "https://i.pravatar.cc/150?img=2"),
        User(id = 3, name = "Clara", email = "clara@music.com", password = "password", avatarUrl = "https://i.pravatar.cc/150?img=3")
    )

    // Musics table mapped to MusicTrack model
    // Use a snapshot state list so generated tracks can be added at runtime and UI updates.
    val musics = mutableStateListOf(
        MusicTrack(
            id = 5,
            title = "Chill Waves",
            styleName = "Chill",
            duration = 210,
            createdAt = "2024-11-02",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            coverUrl = "https://images.unsplash.com/photo-1508704019882-f9cf40e475b4"
        ),
        MusicTrack(
            id = 6,
            title = "Ocean Beats",
            styleName = "Workout",
            duration = 184,
            createdAt = "2024-11-03",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4"
        ),
        MusicTrack(
            id = 7,
            title = "Night Lights",
            styleName = "LoFi",
            duration = 220,
            createdAt = "2024-11-04",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            coverUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"
        ),
        MusicTrack(
            id = 8,
            title = "Lo-Fi Dreams",
            styleName = "LoFi",
            duration = 176,
            createdAt = "2024-11-05",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            coverUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d"
        ),
        MusicTrack(
            id = 9,
            title = "Midnight Groove",
            styleName = "Chill",
            duration = 205,
            createdAt = "2024-11-06",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            coverUrl = "https://images.unsplash.com/photo-1507874457470-272b3c8d8ee2"
        ),
        // additional test tracks
        MusicTrack(
            id = 10,
            title = "Sunset Ride",
            styleName = "Workout",
            duration = 198,
            createdAt = "2024-12-01",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            coverUrl = "https://images.unsplash.com/photo-1496307042754-b4aa456c4a2d"
        ),
        MusicTrack(
            id = 11,
            title = "Deep Focus",
            styleName = "Chill",
            duration = 240,
            createdAt = "2024-12-02",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            coverUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee"
        )
    )

    // Add a music track to the test dataset if not already present
    fun addMusic(track: MusicTrack) {
        if (musics.none { it.id == track.id }) {
            musics.add(track)
        }
    }

    data class PlaylistView(val id: Int, val title: String, val ownerId: Int, val trackIds: List<Int>, val coverUrl: String?)

    // Use a snapshot state list so Compose observes changes automatically
    val playlists = mutableStateListOf(
        PlaylistView(id = 1, title = "Détente Chill", ownerId = 1, trackIds = listOf(5, 8), coverUrl = musics.first { it.id == 5 }.coverUrl),
        PlaylistView(id = 2, title = "Workout Énergie", ownerId = 2, trackIds = listOf(6, 9, 11, 10), coverUrl = musics.first { it.id == 6 }.coverUrl),
        PlaylistView(id = 3, title = "Voyage Musical", ownerId = 1, trackIds = listOf(7), coverUrl = musics.first { it.id == 7 }.coverUrl),
        PlaylistView(id = 4, title = "Soirée Party", ownerId = 3, trackIds = listOf(9, 5, 6, 7, 8), coverUrl = musics.first { it.id == 9 }.coverUrl)
    )

    val styles = listOf(
        StyleItem(name = "Chill", icon = Icons.Default.LibraryMusic, color = Color(0xFF6B1EFF)),
        StyleItem(name = "Workout", icon = Icons.Default.LibraryMusic, color = Color(0xFF00C853)),
        StyleItem(name = "LoFi", icon = Icons.Default.LibraryMusic, color = Color(0xFFFF6D00))
    )

    val likedMusicIds = mutableStateListOf(5, 10, 11)

    val likedMusics: List<MusicTrack>
        get() = musics.filter { it.id in likedMusicIds }


    fun addToLiked(trackId: Int) {
        if (!likedMusicIds.contains(trackId)) likedMusicIds.add(trackId)
    }

    fun removeFromLiked(trackId: Int) {
        likedMusicIds.remove(trackId)
    }

    // Add a track to a playlist
    fun addTrackToPlaylist(playlistId: Int, trackId: Int) {
        val idx = playlists.indexOfFirst { it.id == playlistId }
        if (idx >= 0) {
            val p = playlists[idx]
            if (!p.trackIds.contains(trackId)) {
                playlists[idx] = p.copy(trackIds = p.trackIds + trackId)
            }
        }
    }

    // Create a new playlist and optionally add the track
    fun createPlaylist(title: String, ownerId: Int = 1, initialTrackId: Int? = null): Int {
        val newId = playlists.size + 1
        val tracks = if (initialTrackId != null) listOf(initialTrackId) else emptyList()
        val cover = musics.firstOrNull()?.coverUrl
        val p = PlaylistView(id = newId, title = title, ownerId = ownerId, trackIds = tracks, coverUrl = cover)
        playlists.add(p)
        return newId
    }

}
