package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.soundwave.models.MusicTrack

class HomeViewModel : ViewModel() {

    var searchText = mutableStateOf("")

    val musicList = listOf(
        MusicTrack(
            id = "5",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            imageUrl = "https://images.unsplash.com/photo-1508704019882-f9cf40e475b4",
            title = "Chill Waves",
            duration = 210.20,
            createdAt = "34"
        ),
        MusicTrack(
            id = "6",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4",
            title = "Ocean Beats",
            duration = 184.15,
            createdAt = "34"
        ),

        MusicTrack(
            id = "7",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f",
            title = "Night Lights",
            duration = 220.50,
            createdAt = "34"
        ),

        MusicTrack(
            id = "8",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            imageUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d",
            title = "Lo-Fi Dreams",
            duration = 176.80,
            createdAt = "34"
        ),

        MusicTrack(
            id = "9",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            imageUrl = "https://images.unsplash.com/photo-1507874457470-272b3c8d8ee2",
            title = "Midnight Groove",
            duration = 205.10,
            createdAt = "34"
        )
    )

    fun updateSearch(text: String) {
        searchText.value = text
    }

    fun getFilteredMusic(): List<MusicTrack> {
        return musicList.filter {
            it.title.contains(searchText.value, ignoreCase = true)
        }
    }
}