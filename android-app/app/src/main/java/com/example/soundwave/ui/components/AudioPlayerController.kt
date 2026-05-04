package com.example.soundwave.ui.components

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
import androidx.core.net.toUri

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
    // Track active play sessions: map trackId -> server playId
    private val activePlaySession = mutableMapOf<String, String>()
    // Last progression sent to server per playId
    private val lastSentProgressionPerPlay = mutableMapOf<String, Double>()
    // Last position (ms) sent to server per playId — used to prevent updates after seeks backward
    private val lastSentPositionPerPlay = mutableMapOf<String, Long>()

    private const val LISTEN_THRESHOLD_MS = 15_000L
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

                        if (totalListenTime >= LISTEN_THRESHOLD_MS && !listenedTracks.contains(trackId)) {
                            // First time threshold reached for this track in this session: create a play on server
                            scope.launch {
                                if (durationMs > 0L) {
                                    val actualProgression: Double = (1.0) * totalListenTime / durationMs
                                    // call addPlay and store returned play id to allow subsequent updates
                                    Log.d("Audio", current.id)
                                    val res = kotlin.runCatching { playRepository.addPlay(current.id, actualProgression) }
                                    Log.d("AudioPlayer", res.toString())
                                    res.onSuccess { result ->
                                        val playDto = result.getOrNull()
                                        if (playDto != null) {
                                            activePlaySession[trackId] = playDto.id
                                            lastSentProgressionPerPlay[playDto.id] = actualProgression
                                            lastSentPositionPerPlay[playDto.id] = positionMs
                                            // mark only after successful creation so failures can be retried later
                                            listenedTracks.add(trackId)
                                        }
                                    }
                                } else {
                                    // duration unknown: still attempt to add play with progression 0.0
                                    val res = kotlin.runCatching { playRepository.addPlay(current.id, 0.0) }
                                    res.onSuccess { result ->
                                        val playDto = result.getOrNull()
                                        if (playDto != null) {
                                            activePlaySession[trackId] = playDto.id
                                            lastSentProgressionPerPlay[playDto.id] = 0.0
                                            lastSentPositionPerPlay[playDto.id] = positionMs
                                            listenedTracks.add(trackId)
                                        }
                                    }
                                }
                            }
                        }

                        // Every 5s after the first play creation, attempt to update the play with new progression
                        val playId = activePlaySession[trackId]
                        if (playId != null) {
                            // Only update every ~5s increments of listened time (we increment listenedTimePerTrack by 500ms)
                            val sentSoFar = lastSentProgressionPerPlay[playId] ?: 0.0
                            // compute candidate progression only if duration known
                            if (durationMs > 0L) {
                                val candidateProgression = (1.0) * totalListenTime / durationMs
                                // Only send update if progression strictly increased and at least 5s since lastSent
                                val shouldUpdate = candidateProgression > sentSoFar &&
                                    (totalListenTime - ((sentSoFar * durationMs).toLong())) >= 5_000L

                                if (shouldUpdate) {
                                    // also ensure user didn't seek backward past the last sent position
                                    val lastPos = lastSentPositionPerPlay[playId] ?: 0L
                                    if (positionMs >= lastPos) {
                                        scope.launch {
                                            kotlin.runCatching {
                                                playRepository.updatePlay(playId, candidateProgression)
                                            }.onSuccess { result ->
                                                // on success, update lastSentProgression and lastSentPosition
                                                lastSentProgressionPerPlay[playId] = candidateProgression
                                                lastSentPositionPerPlay[playId] = positionMs
                                            }
                                        }
                                    }
                                }
                            }
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

    fun play(context: Context, track: MusicTrack, musicList: List<MusicTrack>? = null, playerViewModel: PlayerViewModel? = null) {
        ensureInitialized(context)
        // reset session for previous track so returning later will create a new play after threshold
        val previous = currentTrack
        if (previous != null && previous.id != track.id) {
            val prevId = previous.id
            // clear accumulated listen time for previous
            listenedTimePerTrack.remove(prevId)
            // clear any active server play session for previous
            val prevPlayId = activePlaySession.remove(prevId)
            if (prevPlayId != null) {
                lastSentProgressionPerPlay.remove(prevPlayId)
                lastSentPositionPerPlay.remove(prevPlayId)
            }
            // allow a new play to be created next time we listen to previous track
            listenedTracks.remove(prevId)
        }

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
            val download = kotlinx.coroutines.withContext(Dispatchers.IO) { store.getById(track.id) }
            val uriToPlay = if (download?.localPath != null) android.net.Uri.fromFile(java.io.File(download.localPath)) else track.audioUrl.toUri()
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
        // clear active play sessions for tracks when stopping playback
        activePlaySession.clear()
        lastSentProgressionPerPlay.clear()
        // don't clear listenedTracks entirely here: we want new sessions after full stop+restart
        // cancel progress job to avoid orphan coroutine
        progressJob?.cancel()
        progressJob = null
    }
    fun seekTo(position: Long) {
        player?.seekTo(position)
    }
}
