package com.example.soundwave.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.soundwave.events.AppEvents
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.ui.components.AudioPlayerController.durationMs
import kotlinx.coroutines.launch

class PlayerViewModel: BaseViewModel() {
    var musicList: MutableList<MusicTrack> = mutableListOf()

    var currentTrack by mutableStateOf<MusicTrack?>(null)

    init {
        // listen for recommendation results and update the player's list by
        // inserting the currently playing track at index 0 and appending the recommendations
        viewModelScope.launch {
            AppEvents.recommendationResults.collect { list ->
                try {
                    val current = currentTrack
                    if (current != null && list.isNotEmpty()) {
                        val newList = mutableListOf<MusicTrack>()
                        newList.add(current)
                        newList.addAll(list.filter { it.id != current.id })
                        musicList = newList
                        Log.d("PlayerView", "${musicList}")

                    }
                } catch (_: Exception) {
                    // ignore
                }
            }
        }
    }

    fun updateMusicList(list: MutableList<MusicTrack>){
        musicList = list
    }

    fun getNextMusic(): MusicTrack?{
        val index = musicList.indexOf(currentTrack)
        return musicList.getOrNull(index + 1)
    }

    fun getPreviousMusic(): MusicTrack?{
        val index = musicList.indexOf(currentTrack)
        return musicList.getOrNull(index - 1)
    }

    suspend fun loadTrackDetails(){

    }
}