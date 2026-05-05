package com.example.soundwave.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LikedTrackStore(private val context: Context) {
    private val db by lazy { LikedDatabase.getInstance(context) }
    private val dao by lazy { db.likedTrackDao() }

    suspend fun getForUser(userId: String) = withContext(Dispatchers.IO) {
        dao.getForUser(userId)
    }

    suspend fun upsert(entity: LikedTrackEntity) = withContext(Dispatchers.IO) {
        dao.upsert(entity)
    }

    suspend fun deleteByIdForUser(id: String, userId: String) = withContext(Dispatchers.IO) {
        dao.deleteByIdForUser(id, userId)
    }
}
