package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.data.TestDataProvider
import com.example.soundwave.data.repository.TrackRepository
import kotlinx.coroutines.launch
import android.util.Log

class HomeViewModel : BaseViewModel() {

    var searchText = mutableStateOf("")

    // Use centralized test data provider so musics can be shared across the app
    val musicList: List<MusicTrack> = TestDataProvider.musics

    // Mutable state list that will hold backend recommendations
    val recommendationList: SnapshotStateList<MusicTrack> = mutableStateListOf()

    fun updateSearch(text: String) {
        searchText.value = text
    }

    fun getFilteredMusic(): List<MusicTrack> {
        return musicList.filter {
            it.title.contains(searchText.value, ignoreCase = true)
        }
    }

    fun launchRecommendation(limit: Int = 15) {
        viewModelScope.launch {
            try {
                val repo = TrackRepository()
                val resp = repo.getRecommendations(limit).getOrNull()
                if (resp != null) {
                    val mapped = resp.mapNotNull { item ->
                        try {
                            Log.d("HomeViewModel", "${MusicTrack.fromDto(item.track)}")
                            MusicTrack.fromDto(item.track)
                        } catch (e: Exception) {
                            Log.d("HomeViewModel", "${e}")
                            null
                        }
                    }
                    recommendationList.clear()
                    recommendationList.addAll(mapped)
//                    Log.d("HomeViewModel", "${resp}")
                } else {
                    Log.d("HomeViewModel", "No recommendations returned")
                }
            } catch (e: Exception) {
                Log.d("HomeViewModel", "Failed to load recommendations: ${e.message}")
            }
        }
    }

}