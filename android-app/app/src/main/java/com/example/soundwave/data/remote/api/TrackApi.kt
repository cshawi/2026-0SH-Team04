package com.example.soundwave.data.remote.api

import com.example.soundwave.data.remote.dto.track.CreateTrackRequestDto
import com.example.soundwave.data.remote.dto.track.TrackDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TrackApi {
    @GET("api/tracks")
    suspend fun getTracks(
        @Query("limit") limit: Int? = 15,
        @Query("filter") filter: String? = ""
    ): List<TrackDto>

    @GET("api/tracks/{id}")
    suspend fun getTrackById(@Path("id") id: String): TrackDto

    @GET("api/tracks/recommendations")
    suspend fun getRecommendations(@Query("limit") limit: Int? = 15): List<RecommendationItem>

    @POST("api/tracks")
    suspend fun addTrack(@Body request: CreateTrackRequestDto): AddTrackResponse
}

// Helper DTOs for API responses used here
data class RecommendationItem(
    val track: TrackDto,
    val score: Double,
    val confidence: Double
)

data class AddTrackResponse(
    val jobId: String?,
    val taskId: String?
)
