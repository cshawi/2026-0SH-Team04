package com.example.soundwave.data.remote

data class GenerateMusicRequest(
    val title: String,
    val description: String,
    val style: String?,
    val instrumental: Boolean
)
