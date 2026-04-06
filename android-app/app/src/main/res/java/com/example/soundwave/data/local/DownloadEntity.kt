package com.example.soundwave.data.local

data class DownloadEntity(
    val trackId: Int,
    val title: String,
    val localPath: String?,
    val status: String,
    val progress: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
