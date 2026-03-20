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
import kotlin.math.abs

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
                    // parse id as Int when possible; otherwise derive an Int from hashCode
                    val parsedId = track.id.toIntOrNull() ?: abs(track.id.hashCode())
                    MusicTrack(
                        id = parsedId,
                        title = track.title,
                        styleName = "Unknown",
                        duration = track.duration.toInt(),
                        createdAt = track.createdAt,
                        audioUrl = track.audioUrl,
                        coverUrl = track.imageUrl
                    )
                }
            )
        }
    }

    fun mapUser(response: UserResponse): User {
        val id = response.id.toIntOrNull() ?: abs(response.id.hashCode())
        return User(
            id = id,
            name = response.name,
            email = response.email ?: "",
            password = "",
            avatarUrl = response.avatarUrl
        )
    }
}
