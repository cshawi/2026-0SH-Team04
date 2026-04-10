package com.example.soundwave.models

import com.example.soundwave.data.remote.dto.track.TrackDto

data class MusicGenerationResult(
	val taskId: String,
	val tracks: List<MusicTrack>
)

data class MusicTrack(
	val id: String,
	val title: String,
	val styleName: String,
	val duration: Int,
	val createdAt: String,
	val audioUrl: String,
	val coverUrl: String,
	val lyrics: String? = null,
	val username: String? = null
){
	companion object {
		fun fromDto(dto: TrackDto): MusicTrack {
			return MusicTrack(
				id = dto.id,
				title = dto.title,
				styleName = dto.style,
				duration = (dto.duration?.toInt() ?: 0),
				createdAt = dto.createdAt ?: "",
				audioUrl = dto.audioUrl,
				coverUrl = dto.coverUrl,
				lyrics = dto.lyrics,
				username = dto.username
			)
		}
	}

}