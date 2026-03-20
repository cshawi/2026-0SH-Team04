package com.example.soundwave.models

data class MusicGenerationResult(
	val taskId: String,
	val tracks: List<MusicTrack>
)

data class MusicTrack(
	val id: Int,
	val title: String,
	val styleName: String,
	val duration: Int,
	val createdAt: String,
	val audioUrl: String,
	val coverUrl: String
)