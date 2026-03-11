package com.example.soundwave.data.repository

import com.example.soundwave.data.remote.GenerateMusicRequest
import com.example.soundwave.data.remote.GenerateMusicResponse
import com.example.soundwave.data.remote.GenerateStatusResponse
import com.example.soundwave.data.remote.FakeMusicRemoteDataSource
import com.example.soundwave.data.remote.MusicRemoteDataSource
import com.example.soundwave.data.remote.UserResponse
import com.example.soundwave.models.MusicGenerationResult
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.models.User

class MusicRepository(
    private val remoteDataSource: MusicRemoteDataSource = FakeMusicRemoteDataSource()
) {
    suspend fun generateMusic(request: GenerateMusicRequest): Result<GenerateMusicResponse> {
        return runCatching { remoteDataSource.generateMusic(request) }
    }

    suspend fun checkStatus(taskId: String): Result<GenerateStatusResponse> {
        return runCatching { remoteDataSource.checkStatus(taskId) }
    }

    suspend fun checkStatusMapped(taskId: String): Result<MusicGenerationResult> {
        return runCatching {
            val status = remoteDataSource.checkStatus(taskId)
            MusicGenerationResult(
                taskId = status.taskId,
                tracks = status.tracks.map { track ->
                    MusicTrack(
                        id = track.id,
                        audioUrl = track.audioUrl,
                        imageUrl = track.imageUrl,
                        title = track.title,
                        duration = track.duration,
                        createdAt = track.createdAt
                    )
                }
            )
        }
    }

    fun mapUser(response: UserResponse): User {
        return User(
            id = response.id,
            name = response.name,
            email = response.email,
            avatarUrl = response.avatarUrl
        )
    }
}
