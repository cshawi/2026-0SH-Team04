package com.example.soundwave.data.remote

interface MusicRemoteDataSource {
    suspend fun generateMusic(request: GenerateMusicRequest): GenerateMusicResponse
    suspend fun checkStatus(taskId: String): GenerateStatusResponse
}
