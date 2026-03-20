package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.data.TestDataProvider

class HomeViewModel : ViewModel() {

    var searchText = mutableStateOf("")

    // Use centralized test data provider so musics can be shared across the app
    val musicList: List<MusicTrack> = TestDataProvider.musics

    fun updateSearch(text: String) {
        searchText.value = text
    }

    fun getFilteredMusic(): List<MusicTrack> {
        return musicList.filter {
            it.title.contains(searchText.value, ignoreCase = true)
        }
    }
}