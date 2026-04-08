package com.example.soundwave.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    var avatarUrl: String?,
    val createdAt: String
)