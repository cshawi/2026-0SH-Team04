package com.example.soundwave.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MusicApiService {
    @POST("/generate")
    suspend fun generateMusic(@Body request: GenerateMusicRequest): GenerateMusicResponse

    @GET("/generate/status")
    suspend fun checkStatus(@Query("taskId") taskId: String): GenerateStatusResponse

    @GET("/me")
    suspend fun getMe(): UserResponse
}
