package com.example.soundwave.data.remote.api

import com.example.soundwave.data.remote.dto.play.CreatePlayRequestDto
import com.example.soundwave.data.remote.dto.play.PlayDto
import com.example.soundwave.data.remote.dto.play.UpdatePlayRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PlayApi {
    @POST("api/plays")
    suspend fun addPlay(@Body request: CreatePlayRequestDto): PlayDto

    @GET("api/plays")
    suspend fun getAllPlays(): List<PlayDto>

    @GET("api/plays/{id}")
    suspend fun getPlayById(@Path("id") id: String): PlayDto

    @PATCH("api/plays/{id}")
    suspend fun updatePlay(
        @Path("id") id: String,
        @Body request: UpdatePlayRequestDto
    ): PlayDto
}
