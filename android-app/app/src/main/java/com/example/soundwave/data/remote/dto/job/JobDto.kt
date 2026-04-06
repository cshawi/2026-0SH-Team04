package com.example.soundwave.data.remote.dto.job

import com.google.gson.annotations.SerializedName

data class JobDto(
    @SerializedName("_id") val id: String,
    val taskId: String?,
    val userId: String?,
    val status: String,
    val tracks: List<String>?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)
