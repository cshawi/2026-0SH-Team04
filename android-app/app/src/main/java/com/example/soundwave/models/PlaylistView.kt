package com.example.soundwave.models

data class PlaylistView(
    val id: String,
    val title: String,
    val ownerId: String,
    val trackIds: List<String>,
    val coverUrl: String?
)
