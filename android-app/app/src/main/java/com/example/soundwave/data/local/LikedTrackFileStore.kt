package com.example.soundwave.data.local

import android.content.Context
import com.example.soundwave.data.local.LikedTrackEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LikedTrackFileStore(private val context: Context, private val gson: Gson = Gson()) {

    private fun fileForUser(userId: String): File {
        return File(context.filesDir, "liked_${userId}.json")
    }

    suspend fun getForUser(userId: String): List<LikedTrackEntity> = withContext(Dispatchers.IO) {
        val file = fileForUser(userId)
        if (!file.exists()) return@withContext emptyList()
        try {
            val json = file.readText()
            val listType = object : TypeToken<List<LikedTrackEntity>>() {}.type
            val items: List<LikedTrackEntity> = gson.fromJson(json, listType) ?: emptyList()
            return@withContext items
        } catch (_: Exception) {
            return@withContext emptyList()
        }
    }

    suspend fun upsert(entity: LikedTrackEntity) = withContext(Dispatchers.IO) {
        val file = fileForUser(entity.userId)
        val current = if (file.exists()) {
            try {
                val json = file.readText()
                val listType = object : TypeToken<MutableList<LikedTrackEntity>>() {}.type
                gson.fromJson<MutableList<LikedTrackEntity>>(json, listType) ?: mutableListOf()
            } catch (_: Exception) { mutableListOf<LikedTrackEntity>() }
        } else {
            mutableListOf()
        }

        val idx = current.indexOfFirst { it.id == entity.id }
        if (idx >= 0) current[idx] = entity else current.add(0, entity)

        val tmp = File(file.parentFile, "${file.name}.tmp")
        tmp.writeText(gson.toJson(current))
        tmp.renameTo(file)
        return@withContext Unit
    }

    suspend fun deleteByIdForUser(id: String, userId: String) = withContext(Dispatchers.IO) {
        val file = fileForUser(userId)
        if (!file.exists()) return@withContext
        try {
            val json = file.readText()
            val listType = object : TypeToken<MutableList<LikedTrackEntity>>() {}.type
            val current = gson.fromJson<MutableList<LikedTrackEntity>>(json, listType) ?: mutableListOf()
            val newList = current.filter { it.id != id }
            val tmp = File(file.parentFile, "${file.name}.tmp")
            tmp.writeText(gson.toJson(newList))
            tmp.renameTo(file)
        } catch (_: Exception) { }
        return@withContext Unit
    }
}
