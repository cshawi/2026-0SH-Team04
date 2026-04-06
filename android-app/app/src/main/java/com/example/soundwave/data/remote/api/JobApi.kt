package com.example.soundwave.data.remote.api

import com.example.soundwave.data.remote.dto.job.JobDto
import retrofit2.http.GET
import retrofit2.http.Path

interface JobApi {
    @GET("api/jobs/{jobId}")
    suspend fun getJobStatus(@Path("jobId") jobId: String): JobDto
}
