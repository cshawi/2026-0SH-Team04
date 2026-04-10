package com.example.soundwave.data.worker

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.soundwave.data.local.DownloadEntity
import com.example.soundwave.data.local.DownloadStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class DownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
    val trackId = inputData.getString("trackId") ?: return Result.failure()
        val audioUrl = inputData.getString("audioUrl") ?: return Result.failure()
    val title = inputData.getString("title") ?: trackId

        val store = DownloadStore(applicationContext)
        
    // mark as downloading
    store.upsert(DownloadEntity(trackId = trackId, title = title, localPath = null, status = "DOWNLOADING", progress = 0))

        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                // validate URL early to get clearer errors
                val parsed = audioUrl.toHttpUrlOrNull()
                if (parsed == null) {
                    Log.e("DownloadWorker", "Invalid audio URL: $audioUrl")
                    store.upsert(DownloadEntity(trackId = trackId, title = title, localPath = null, status = "FAILED", progress = 0))
                    return@withContext Result.failure()
                }
                val req = Request.Builder().url(parsed).build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@withContext Result.retry()
                    val dir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: applicationContext.filesDir
                    // ensure directory exists
                    try {
                        if (!dir.exists()) {
                            dir.mkdirs()
                        }
                    } catch (ie: Exception) {
                        Log.w("DownloadWorker", "Could not create dir ${dir?.absolutePath}", ie)
                    }
                    val safeName = title.replace(Regex("[^a-zA-Z0-9]+"), "_")
                    val fileName = "${trackId}_$safeName.mp3"
                    val outFile = File(dir, fileName)

                    resp.body?.byteStream()?.use { input ->
                        FileOutputStream(outFile).use { output ->
                            val buffer = ByteArray(8 * 1024)
                            var bytesRead: Int
                            var total = 0L
                            val contentLength = resp.body?.contentLength() ?: -1L
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                total += bytesRead
                                if (contentLength > 0) {
                                    val progress = (100 * total / contentLength).toInt().coerceIn(0, 100)
                                    setProgress(workDataOf("progress" to progress))
                                    store.upsert(DownloadEntity(trackId = trackId, title = title, localPath = null, status = "DOWNLOADING", progress = progress))
                                }
                            }
                        }
                    }

                    // mark done
                    store.upsert(DownloadEntity(trackId = trackId, title = title, localPath = outFile.absolutePath, status = "DONE", progress = 100))

                    return@withContext Result.success()
                }
            } catch (e: Exception) {
                // Network-related errors should be retried by WorkManager
                when (e) {
                    is java.net.UnknownHostException,
                    is java.io.IOException -> {
                        Log.w("DownloadWorker", "Network error while downloading $audioUrl - will retry", e)
                        // mark retrying so UI can reflect a non-permanent failure (optional)
                        try {
                            store.upsert(DownloadEntity(trackId = trackId, title = title, localPath = null, status = "RETRYING", progress = 0))
                        } catch (_: Exception) {}
                        return@withContext Result.retry()
                    }
                    else -> {
                        Log.e("DownloadWorker", "Fatal error while downloading $audioUrl", e)
                        try {
                            store.upsert(DownloadEntity(trackId = trackId, title = title, localPath = null, status = "FAILED", progress = 0))
                        } catch (_: Exception) {}
                        return@withContext Result.failure()
                    }
                }
            }
        }
    }
}
