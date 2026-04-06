package com.example.soundwave.data.remote.api

import com.example.soundwave.data.remote.dto.playlist.CreatePlaylistRequestDto
import com.example.soundwave.data.remote.dto.playlist.PlaylistDto
import com.example.soundwave.data.remote.dto.user.MessageResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaylistApi {
    @POST("api/playlists")
    suspend fun createPlaylist(@Body request: CreatePlaylistRequestDto): PlaylistDto

    @GET("api/playlists")
    suspend fun getPlaylists(@Query("filter") filter: String? = null): List<PlaylistDto>

    @GET("api/playlists/{id}")
    suspend fun getPlaylistById(@Path("id") id: String): PlaylistDto

    @DELETE("api/playlists/{id}")
    suspend fun deletePlaylist(@Path("id") id: String): PlaylistDto
}
