package com.example.soundwave.data.remote.dto.track

// Matches fields expected by the server's addTrack controller
data class CreateTrackRequestDto(
    val is_personalized: Boolean,
    val is_instrumental: Boolean,
    val prompt: String?,
    val title: String?,
    val style: String?,
    val coverUrl: String?,
    val vocalGender: String? = "m",
    val styleWeight: Double? = 0.65,
    val weirdnessConstraint: Double? = 0.65,
    val audioWeight: Double? = 0.65,
    val model: String? = "V4_5ALL"
)
