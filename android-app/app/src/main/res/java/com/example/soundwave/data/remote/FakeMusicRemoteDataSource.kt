package com.example.soundwave.data.remote

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FakeMusicRemoteDataSource : MusicRemoteDataSource {
    private val pollCount = mutableMapOf<String, Int>()
    private val requests = mutableMapOf<String, GenerateMusicRequest>()

    override suspend fun generateMusic(request: GenerateMusicRequest): GenerateMusicResponse {
        val taskId = "task_${System.currentTimeMillis()}"
        pollCount[taskId] = 0
        requests[taskId] = request
        return GenerateMusicResponse(taskId = taskId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun checkStatus(taskId: String): GenerateStatusResponse {
        val currentCount = (pollCount[taskId] ?: 0) + 1
        pollCount[taskId] = currentCount

        return if (currentCount < 3) {
            GenerateStatusResponse(
                callbackType = "processing",
                taskId = taskId,
                tracks = emptyList()
            )
        } else {
            val request = requests[taskId]

            val now = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

            GenerateStatusResponse(
                callbackType = "complete",
                taskId = taskId,
                tracks = listOf(
                    GeneratedTrack(
                        id = "track_${System.currentTimeMillis()}",
                        audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                        imageUrl = "https://www.causeur.fr/wp-content/uploads/2020/12/gims-music-awards.jpg",
                        title = request?.title?.ifBlank { "SoundWave" } ?: "SoundWave",
                        duration = 198.44,
                        createdAt = now.format(formatter)
                    )
                )
            )
        }
    }
}
