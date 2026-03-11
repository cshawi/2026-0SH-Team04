package com.example.soundwave.models

data class MusicGenerationResult(
	val taskId: String,
	val tracks: List<MusicTrack>
)

data class MusicTrack(
	val id: String,
	val audioUrl: String,
	val imageUrl: String,
	val title: String,
	val duration: Double,
	val createdAt: String
)