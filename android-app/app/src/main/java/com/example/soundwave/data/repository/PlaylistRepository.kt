package com.example.soundwave.data.repository

import com.example.soundwave.config.RetrofitProvider
import com.example.soundwave.data.remote.dto.playlist.CreatePlaylistRequestDto
import com.example.soundwave.data.remote.dto.playlist.PlaylistDto

class PlaylistRepository {
    private val api = RetrofitProvider.playlistApi

    suspend fun createPlaylist(name: String): Result<PlaylistDto> = runCatching {
        api.createPlaylist(CreatePlaylistRequestDto(name))
    }

    suspend fun getPlaylists(filter: String? = null): Result<List<PlaylistDto>> = runCatching {
        api.getPlaylists(filter)
    }

    suspend fun getPlaylistById(id: String): Result<PlaylistDto> = runCatching {
        api.getPlaylistById(id)
    }

    suspend fun deletePlaylist(id: String): Result<PlaylistDto> = runCatching {
        api.deletePlaylist(id)
    }
}
