package com.example.soundwave.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.soundwave.models.MusicTrack

class PlayerViewModel: BaseViewModel() {
    var musicList: MutableList<MusicTrack> = mutableListOf()

    var currentTrack by mutableStateOf<MusicTrack?>(null)

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