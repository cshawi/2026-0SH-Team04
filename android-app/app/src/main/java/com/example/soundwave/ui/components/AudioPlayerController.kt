package com.example.soundwave.ui.components

import android.R
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.events.AppEvents
import com.example.soundwave.viewModels.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.soundwave.data.repository.PlayRepository

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
    private var appContext: Context? = null
    // Keep a reference to the last provided playerViewModel and music list so the controller
    // can detect when the currently playing track is the last in the list and emit events.
    private var lastPlayerViewModel: PlayerViewModel? = null
    private var lastMusicList: List<MusicTrack>? = null
    private var prevTrackId: String? = null
    private val emittedForTrack = mutableSetOf<String>()
    private val listenedTracks = mutableSetOf<String>()
    private val listenedTimePerTrack = mutableMapOf<String, Long>()

    private val LISTEN_THRESHOLD_MS = 15_000L
    private val playRepository = PlayRepository()

    fun ensureInitialized(context: Context) {
        if (player != null) return
        val applicationContext = context.applicationContext
        appContext = applicationContext
        player = ExoPlayer.Builder(applicationContext).build().apply {
            addListener(object : Player.Listener {

                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                    this@AudioPlayerController.isPlaying = isPlayingNow
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    durationMs = (player?.duration ?: 0L).coerceAtLeast(0L)
                    // when playback ends, try to advance to the next track if available
                    if (playbackState == Player.STATE_ENDED) {
                        try {
                            val next = lastPlayerViewModel?.getNextMusic()
                            if (next != null && appContext != null) {
                                // play next track using the same music list and playerViewModel
                                play(appContext!!, next, lastMusicList, lastPlayerViewModel)
                            } else {
                                // no next track: stop playback and clear state
                                stop()
                            }
                        } catch (_: Exception) {
                            // ignore any exception to avoid crashing the listener
                        }
                    }
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
                // detect when current track changes and reset per-track emitted flags
                val current = currentTrack
                if (current?.id != prevTrackId) {
                    prevTrackId = current?.id
                    // clear any emitted state for new track (we only track emissions per-track)
                    emittedForTrack.clear()
                }

                // If we have a music list and the current track is the last element,
                // emit a recommendation trigger once when we reach half of the track or near the end.
                try {
                    if (
                        current != null &&
                        exo?.isPlaying == true
                    ) {

                        val trackId = current.id
                        listenedTimePerTrack[trackId] =
                            (listenedTimePerTrack[trackId] ?: 0L) + 500L

                        val totalListenTime = listenedTimePerTrack[trackId] ?: 0L

                        if (
                            totalListenTime >= LISTEN_THRESHOLD_MS &&
                            !listenedTracks.contains(trackId)
                        ) {

                            scope.launch {
                                playRepository.addPlay(current.id)
                            }

                            listenedTracks.add(trackId)
                        }
                    }

                    val list = lastMusicList
                    if (current != null && list != null && durationMs > 0L) {
                        val index = list.indexOfFirst { it.id == current.id }
                        val isLast = index != -1 && index == list.lastIndex
                        if (isLast) {
                            val trackId = current.id
                            val halfThreshold = durationMs / 2

                            //val endThreshold = (durationMs - 1000L).coerceAtLeast(0L)
                            val already = emittedForTrack.contains(trackId)
                            if (!already && positionMs >= halfThreshold) {
                                AppEvents.tryEmitRecommendationTrigger()
                                emittedForTrack.add(trackId)
                            }
                        }
                    }
                } catch (_: Exception) {
                    // be defensive — don't allow progress updates to crash
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
            lastPlayerViewModel = playerViewModel
            lastMusicList = musicList
        } else {
            // clear references if none were provided
            lastPlayerViewModel = null
            lastMusicList = null
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
        lastPlayerViewModel = null
        lastMusicList = null
        prevTrackId = null
        emittedForTrack.clear()
    }
    fun seekTo(position: Long) {
        player?.seekTo(position)
    }
}
