package com.example.soundwave.data.repository

import com.example.soundwave.config.RetrofitProvider
import com.example.soundwave.data.remote.api.AddTrackResponse
import com.example.soundwave.data.remote.api.RecommendationItem
import com.example.soundwave.data.remote.dto.track.CreateTrackRequestDto
import com.example.soundwave.data.remote.dto.track.TrackDto

class TrackRepository {
    private val api = RetrofitProvider.trackApi

    suspend fun getTracks(limit: Int = 15, filter: String = ""): Result<List<TrackDto>> = runCatching {
        api.getTracks(limit, filter)
    }

    suspend fun getTrackById(id: String): Result<TrackDto> = runCatching {
        api.getTrackById(id)
    }

    suspend fun getRecommendations(limit: Int = 15): Result<List<RecommendationItem>> = runCatching {
        api.getRecommendations(limit)
    }

    suspend fun addTrack(request: CreateTrackRequestDto): Result<AddTrackResponse> = runCatching {
        api.addTrack(request)
    }
}
