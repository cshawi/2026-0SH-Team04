package com.example.soundwave.data.repository

import android.content.Context
import com.example.soundwave.data.local.DownloadEntity
import com.example.soundwave.data.local.DownloadStore

class DownloadRepository(private val context: Context) {
    private val store by lazy { DownloadStore(context) }

    suspend fun upsert(entity: DownloadEntity) {
        store.upsert(entity)
    }

    suspend fun getById(trackId: Int): DownloadEntity? = store.getById(trackId)

    suspend fun delete(trackId: Int) = store.delete(trackId)
}
