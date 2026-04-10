package com.example.soundwave.ui.components

import android.R
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.soundwave.models.MusicTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AudioPlayerController {

    var currentTrack by mutableStateOf<MusicTrack?>(null)
        private set
    var currentId by mutableStateOf<String?>(null)
        private set
    var currentUrl by mutableStateOf<String?>(null)
        private set
    var currentCoverUrl by mutableStateOf<String?>(null)
        private set
    var currentTitle by mutableStateOf<String?>(null)
        private set
    var isPlaying by mutableStateOf(false)
        private set
    var durationMs by mutableStateOf(0L)
        private set
    var positionMs by mutableStateOf(0L)
        private set

    private var player: ExoPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    fun ensureInitialized(context: Context) {
        if (player != null) return
        val appContext = context.applicationContext
        player = ExoPlayer.Builder(appContext).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                    this@AudioPlayerController.isPlaying = isPlayingNow
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    durationMs = (player?.duration ?: 0L).coerceAtLeast(0L)
                }
            })
        }
        startProgressUpdates()
    }

    private fun startProgressUpdates() {
        if (progressJob != null) return
        progressJob = scope.launch {
            while (true) {
                val exo = player
                if (exo != null) {
                    durationMs = exo.duration.coerceAtLeast(0L)
                    positionMs = exo.currentPosition.coerceAtLeast(0L)
                } else {
                    durationMs = 0L
                    positionMs = 0L
                }
                delay(500L)
            }
        }
    }

    fun play(context: Context, track: MusicTrack, musicList: List<MusicTrack>? = null, playerViewModel: com.example.soundwave.viewModels.PlayerViewModel? = null) {
        ensureInitialized(context)
        // set metadata immediately
        currentTrack = track
        currentUrl = track.audioUrl
        currentTitle = track.title
        currentCoverUrl = track.coverUrl
        currentId = track.id

        // if a list and playerViewModel are provided, set the player's music list and current track
        if (playerViewModel != null && musicList != null) {
            playerViewModel.updateMusicList(musicList.toMutableList())
            playerViewModel.currentTrack = track
        }

        // check for a downloaded local file and play it if available
        scope.launch {
            val store = com.example.soundwave.data.local.DownloadStore(context)
            val download = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { store.getById(track.id) }
            val uriToPlay = if (download?.localPath != null) android.net.Uri.fromFile(java.io.File(download.localPath)) else android.net.Uri.parse(track.audioUrl)
            // always set media item to the requested track
            player?.setMediaItem(MediaItem.fromUri(uriToPlay))
            player?.prepare()
            player?.play()
            isPlaying = true
        }
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
        currentTitle = null
        currentUrl = null
        currentCoverUrl = null
        currentTrack = null
        durationMs = 0L
        positionMs = 0L
        currentId = null
    }
    fun seekTo(position: Long) {
        player?.seekTo(position)
    }
}
