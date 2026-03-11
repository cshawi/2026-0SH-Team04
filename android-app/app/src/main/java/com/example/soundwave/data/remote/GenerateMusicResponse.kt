package com.example.soundwave.data.remote

import java.util.Date

data class GenerateMusicResponse(
    val taskId: String
)

data class GenerateStatusResponse(
    val callbackType: String,
    val taskId: String,
    val tracks: List<GeneratedTrack>
)

data class GeneratedTrack(
    val id: String,
    val audioUrl: String,
    val imageUrl: String,
    val title: String,
    val duration: Double,
    val createdAt: String
)
