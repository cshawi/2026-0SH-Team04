package com.example.soundwave.data.repository

import com.example.soundwave.config.RetrofitProvider
import com.example.soundwave.data.remote.dto.job.JobDto

class JobRepository {
    private val api = RetrofitProvider.jobApi

    suspend fun getJobStatus(jobId: String): Result<JobDto> = runCatching {
        api.getJobStatus(jobId)
    }
}
