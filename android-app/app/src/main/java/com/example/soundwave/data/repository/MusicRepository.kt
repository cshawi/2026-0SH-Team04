package com.example.soundwave.data.repository

import com.example.soundwave.data.remote.GenerateMusicRequest
import com.example.soundwave.data.remote.GenerateMusicResponse
import com.example.soundwave.data.remote.GenerateStatusResponse
import com.example.soundwave.data.remote.GeneratedTrack
import com.example.soundwave.data.remote.dto.track.CreateTrackRequestDto
import com.example.soundwave.data.remote.UserResponse
import com.example.soundwave.models.MusicGenerationResult
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.models.User
import kotlin.math.abs

class MusicRepository(
    private val trackRepository: TrackRepository = TrackRepository(),
    private val jobRepository: JobRepository = JobRepository()
) {
    // Start generation: delegate to TrackRepository.addTrack and return a GenerateMusicResponse
    // Use jobId as the 'taskId' so existing polling code (which expects a taskId) will poll the job status.
    suspend fun generateMusic(request: GenerateMusicRequest): Result<GenerateMusicResponse> {
        return runCatching {
            val createReq = CreateTrackRequestDto(
                is_personalized = request.style != null,
                is_instrumental = request.instrumental,
                prompt = request.description,
                title = request.title,
                style = request.style,
                coverUrl = null,
                vocalGender = "m",
                styleWeight = 0.65,
                weirdnessConstraint = 0.65,
                audioWeight = 0.65,
                model = "V4_5ALL"
            )

            val resp = trackRepository.addTrack(createReq).getOrThrow()
            // Prefer jobId for polling; fallback to taskId if jobId missing
            val pollId = resp.jobId ?: resp.taskId ?: ""
            GenerateMusicResponse(taskId = pollId)
        }
    }

    // Check status by treating the provided id as a jobId (we returned jobId as taskId above)
    suspend fun checkStatus(taskId: String): Result<GenerateStatusResponse> {
        return runCatching {
            val job = jobRepository.getJobStatus(taskId).getOrThrow()

            val generatedTracks = mutableListOf<GeneratedTrack>()
            val trackIds = job.tracks ?: emptyList()

            for (tid in trackIds) {
                try {
                    val track = trackRepository.getTrackById(tid).getOrThrow()
                    generatedTracks.add(
                        GeneratedTrack(
                            id = track.id,
                            audioUrl = track.audioUrl,
                            imageUrl = track.coverUrl ?: "",
                            title = track.title,
                            duration = track.duration?.toDouble() ?: 0.0,
                            createdAt = track.createdAt ?: ""
                        )
                    )
                } catch (e: Exception) {
                    // skip missing track but continue
                }
            }

            GenerateStatusResponse(
                callbackType = "job",
                taskId = job.taskId ?: job.id,
                tracks = generatedTracks
            )
        }
    }

    suspend fun checkStatusMapped(taskId: String): Result<MusicGenerationResult> {
        return runCatching {
            val status = checkStatus(taskId).getOrThrow()
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
        return User(
            id = response.id,
            name = response.name,
            email = response.email ?: "",
            password = "",
            avatarUrl = response.avatarUrl,
            createdAt = ""
        )
    }
}
