package com.example.soundwave.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class DownloadStore(private val context: Context) {
    private val fileName = "downloads.json"
    private val lock = Any()

    private fun getFile(): File = File(context.filesDir, fileName)

    private fun readJson(): JSONObject {
        val f = getFile()
        return if (f.exists()) {
            try {
                JSONObject(f.readText())
            } catch (e: Exception) {
                JSONObject()
            }
        } else JSONObject()
    }

    private fun writeJson(obj: JSONObject) {
        val f = getFile()
        f.writeText(obj.toString())
    }

    suspend fun upsert(entity: DownloadEntity) = withContext(Dispatchers.IO) {
        synchronized(lock) {
            val root = readJson()
            val tracks = if (root.has("tracks")) root.getJSONObject("tracks") else JSONObject()
            val trackObj = JSONObject()
            trackObj.put("trackId", entity.trackId)
            trackObj.put("title", entity.title)
            trackObj.put("localPath", entity.localPath)
            trackObj.put("status", entity.status)
            trackObj.put("progress", entity.progress)
            trackObj.put("updatedAt", entity.updatedAt)
            // JSON object keys must be strings; store under stringified id
            tracks.put(entity.trackId.toString(), trackObj)
            root.put("tracks", tracks)
            writeJson(root)
        }
    }
    suspend fun getById(trackId: Int): DownloadEntity? = withContext(Dispatchers.IO) {
        synchronized(lock) {
            val root = readJson()
            if (!root.has("tracks")) return@withContext null
            val tracks = root.getJSONObject("tracks")
            val key = trackId.toString()
            if (!tracks.has(key)) return@withContext null
            val o = tracks.getJSONObject(key)
            return@withContext DownloadEntity(
                trackId = o.optInt("trackId", trackId),
                title = o.optString("title", ""),
                localPath = o.optString("localPath", null).let { if (it.isNullOrEmpty()) null else it },
                status = o.optString("status", ""),
                progress = o.optInt("progress", 0),
                updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
            )
        }
    }

    suspend fun delete(trackId: Int) = withContext(Dispatchers.IO) {
        synchronized(lock) {
            val root = readJson()
            if (!root.has("tracks")) return@withContext
            val tracks = root.getJSONObject("tracks")
            tracks.remove(trackId.toString())
            root.put("tracks", tracks)
            writeJson(root)
        }
    }
}
