package com.example.soundwave.data.remote.dto.play

data class CreatePlayRequestDto(
    val track_id: String,
    val progression: Double = 0.0
)
