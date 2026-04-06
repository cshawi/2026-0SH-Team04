package com.example.soundwave.data.remote.dto.user

data class LoginResponseDto(
    val token: String,
    val user: UserDto
)
