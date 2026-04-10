package com.example.soundwave.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class AccessDto(
    val email: String
)

data class UserDto(
    @SerializedName("_id") val id: String,
    val username: String,
    val access: AccessDto?,
    val avatarUrl: String?,
    val playlists: List<String>?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)
