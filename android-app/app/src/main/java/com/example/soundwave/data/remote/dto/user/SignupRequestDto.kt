package com.example.soundwave.data.remote.dto.user

data class SignupRequestDto(
    val username: String,
    val email: String,
    val password: String,
    val avatarUrl: String? = null
)
