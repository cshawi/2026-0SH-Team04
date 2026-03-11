package com.example.soundwave.ui.components

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

object AudioPlayerController {
    var currentUrl by mutableStateOf<String?>(null)
        private set
    var currentTitle by mutableStateOf<String?>(null)
        private set
    var isPlaying by mutableStateOf(false)
        private set

    private var player: ExoPlayer? = null

    fun ensureInitialized(context: Context) {
        if (player != null) return
        val appContext = context.applicationContext
        player = ExoPlayer.Builder(appContext).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                    this@AudioPlayerController.isPlaying = isPlayingNow
                }
            })
        }
    }

    fun play(context: Context, url: String, title: String) {
        ensureInitialized(context)
        if (currentUrl != url) {
            player?.setMediaItem(MediaItem.fromUri(url))
            player?.prepare()
        }
        currentUrl = url
        currentTitle = title
        player?.play()
        isPlaying = true
    }

    fun togglePlayPause() {
        val exo = player ?: return
        if (exo.isPlaying) {
            exo.pause()
            isPlaying = false
        } else {
            exo.play()
            isPlaying = true
        }
    }

    fun stop() {
        player?.stop()
        isPlaying = false
    }
}
