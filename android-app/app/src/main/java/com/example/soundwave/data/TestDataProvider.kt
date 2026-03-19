package com.example.soundwave.data

import com.example.soundwave.models.MusicTrack
import com.example.soundwave.models.User
import com.example.soundwave.models.StyleItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateListOf

// Provider of in-memory test data that follows the DB schema (Users, Musics, Playlists, Styles)
object TestDataProvider {

    // Users (id as string to match models)
    val users = listOf(
        User(id = "1", name = "Alice", email = "alice@music.com", avatarUrl = "https://i.pravatar.cc/150?img=1"),
        User(id = "2", name = "Bob", email = "bob@music.com", avatarUrl = "https://i.pravatar.cc/150?img=2"),
        User(id = "3", name = "Clara", email = "clara@music.com", avatarUrl = "https://i.pravatar.cc/150?img=3")
    )

    // Musics table mapped to MusicTrack model
    // Use a snapshot state list so generated tracks can be added at runtime and UI updates.
    val musics = mutableStateListOf(
        MusicTrack(
            id = "5",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            imageUrl = "https://images.unsplash.com/photo-1508704019882-f9cf40e475b4",
            title = "Chill Waves",
            duration = 210.20,
            createdAt = "2024-11-02"
        ),
        MusicTrack(
            id = "6",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4",
            title = "Ocean Beats",
            duration = 184.15,
            createdAt = "2024-11-03"
        ),
        MusicTrack(
            id = "7",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f",
            title = "Night Lights",
            duration = 220.50,
            createdAt = "2024-11-04"
        ),
        MusicTrack(
            id = "8",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            imageUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d",
            title = "Lo-Fi Dreams",
            duration = 176.80,
            createdAt = "2024-11-05"
        ),
        MusicTrack(
            id = "9",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            imageUrl = "https://images.unsplash.com/photo-1507874457470-272b3c8d8ee2",
            title = "Midnight Groove",
            duration = 205.10,
            createdAt = "2024-11-06"
        ),
        // additional test tracks
        MusicTrack(
            id = "10",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            imageUrl = "https://images.unsplash.com/photo-1496307042754-b4aa456c4a2d",
            title = "Sunset Ride",
            duration = 198.00,
            createdAt = "2024-12-01"
        ),
        MusicTrack(
            id = "11",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            imageUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
            title = "Deep Focus",
            duration = 240.00,
            createdAt = "2024-12-02"
        )
    )

    // Add a music track to the test dataset if not already present
    fun addMusic(track: MusicTrack) {
        if (musics.none { it.id == track.id }) {
            musics.add(track)
        }
    }

    data class PlaylistView(val id: String, val title: String, val ownerId: String, val trackIds: List<String>, val coverUrl: String?)

    // Use a snapshot state list so Compose observes changes automatically
    val playlists = mutableStateListOf(
        PlaylistView(id = "p1", title = "Détente Chill", ownerId = "1", trackIds = listOf("5", "8"), coverUrl = musics.first { it.id == "5" }.imageUrl),
        PlaylistView(id = "p2", title = "Workout Énergie", ownerId = "2", trackIds = listOf("6", "9", "11", "10"), coverUrl = musics.first { it.id == "6" }.imageUrl),
        PlaylistView(id = "p3", title = "Voyage Musical", ownerId = "1", trackIds = listOf("7"), coverUrl = musics.first { it.id == "7" }.imageUrl),
        PlaylistView(id = "p4", title = "Soirée Party", ownerId = "3", trackIds = listOf("9", "5", "6", "7", "8"), coverUrl = musics.first { it.id == "9" }.imageUrl)
    )

    val styles = listOf(
        StyleItem(name = "Chill", icon = Icons.Default.LibraryMusic, color = Color(0xFF6B1EFF)),
        StyleItem(name = "Workout", icon = Icons.Default.LibraryMusic, color = Color(0xFF00C853)),
        StyleItem(name = "LoFi", icon = Icons.Default.LibraryMusic, color = Color(0xFFFF6D00))
    )

    // Liked musics (IDs) for the test user(s). Use snapshot state list so Compose observes changes.
    val likedMusicIds = mutableStateListOf("5", "10", "11")

    // Convenience view of liked MusicTrack objects
    val likedMusics: List<MusicTrack>
        get() = musics.filter { it.id in likedMusicIds }

    // Add/remove liked track
    fun addToLiked(trackId: String) {
        if (!likedMusicIds.contains(trackId)) likedMusicIds.add(trackId)
    }

    fun removeFromLiked(trackId: String) {
        likedMusicIds.remove(trackId)
    }

    // Add a track to a playlist (creates a new list copy for the playlist's trackIds)
    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        val idx = playlists.indexOfFirst { it.id == playlistId }
        if (idx >= 0) {
            val p = playlists[idx]
            if (!p.trackIds.contains(trackId)) {
                playlists[idx] = p.copy(trackIds = p.trackIds + trackId)
            }
        }
    }

    // Create a new playlist and optionally add the track
    fun createPlaylist(title: String, ownerId: String = "1", initialTrackId: String? = null): String {
        val newId = "p" + (playlists.size + 1)
        val tracks = if (initialTrackId != null) listOf(initialTrackId) else emptyList()
        val cover = musics.firstOrNull()?.imageUrl
        val p = PlaylistView(id = newId, title = title, ownerId = ownerId, trackIds = tracks, coverUrl = cover)
        playlists.add(p)
        return newId
    }

}
