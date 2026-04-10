package com.example.soundwave.data.remote.dto.track

import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("_id") val id: String,
    val title: String,
    val style: String,
    val audioUrl: String,
    @SerializedName("userId") val userId: String?,
    val username: String?,
    val coverUrl: String,
    val duration: Double?,
    val description: String?,
    val lyrics: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)
