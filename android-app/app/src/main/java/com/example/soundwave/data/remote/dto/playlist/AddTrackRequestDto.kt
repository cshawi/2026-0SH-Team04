package com.example.soundwave.data.remote.dto.playlist

import com.google.gson.annotations.SerializedName

data class AddTrackRequestDto(
    @SerializedName("trackId") val trackId: String
)
