package com.example.soundwave.data.repository

import com.example.soundwave.config.RetrofitProvider
import com.example.soundwave.data.remote.dto.play.CreatePlayRequestDto
import com.example.soundwave.data.remote.dto.play.PlayDto

class PlayRepository {
    private val api = RetrofitProvider.playApi

    suspend fun addPlay(trackId: String, progression: Double = 0.0): Result<PlayDto> = runCatching {
        api.addPlay(CreatePlayRequestDto(trackId, progression))
    }

    suspend fun getAllPlays(): Result<List<PlayDto>> = runCatching {
        api.getAllPlays()
    }

    suspend fun getPlayById(id: String): Result<PlayDto> = runCatching {
        api.getPlayById(id)
    }
}
