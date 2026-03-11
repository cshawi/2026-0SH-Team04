package com.example.soundwave.data.remote

import com.example.soundwave.config.RetrofitProvider

class RetrofitMusicRemoteDataSource(
    private val api: MusicApiService = RetrofitProvider.apiService
) : MusicRemoteDataSource {
    override suspend fun generateMusic(request: GenerateMusicRequest): GenerateMusicResponse {
        return api.generateMusic(request)
    }

    override suspend fun checkStatus(taskId: String): GenerateStatusResponse {
        return api.checkStatus(taskId)
    }
}
