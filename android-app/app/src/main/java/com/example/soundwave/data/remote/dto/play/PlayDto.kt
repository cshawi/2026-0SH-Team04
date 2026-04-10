package com.example.soundwave.data.remote.dto.play

import com.google.gson.annotations.SerializedName

data class PlayDto(
    @SerializedName("_id") val id: String,
    val progression: Double,
    @SerializedName("track_id") val trackId: String,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)
