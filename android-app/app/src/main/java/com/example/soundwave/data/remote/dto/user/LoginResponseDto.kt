package com.example.soundwave.data.remote.dto.user

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String?,
    val user: UserDto
)
