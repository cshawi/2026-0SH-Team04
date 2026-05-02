package com.example.soundwave.data.remote.dto.playlist

import com.google.gson.annotations.SerializedName

data class PlaylistDto(
    @SerializedName("_id") val id: String,
    val name: String,
    @SerializedName("track_ids") val trackIds: List<String>?,
    // server now returns an enriched `tracks` array (each element is a TrackDto)
    val tracks: List<com.example.soundwave.data.remote.dto.track.TrackDto>?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)
