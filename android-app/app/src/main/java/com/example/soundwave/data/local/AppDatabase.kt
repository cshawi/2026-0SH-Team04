package com.example.soundwave.data.local

// Room removed in favor of lightweight DownloadStore (JSON).
// This file remains as a placeholder for possible future migration back to Room.
class AppDatabase private constructor() {
    companion object {
        fun getInstance(context: android.content.Context): AppDatabase {
            return AppDatabase()
        }
    }
}
