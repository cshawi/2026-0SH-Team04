package com.example.soundwave.data.remote

data class UserResponse(
    val id: String,
    val name: String,
    val email: String?,
    val password: String,
    val avatarUrl: String?
)
