package com.example.soundwave.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.example.soundwave.models.MusicTrack

/**
 * Lightweight application-wide event bus for simple cross-ViewModel events.
 * Currently used to trigger recommendation refreshes from the player.
 */
object AppEvents {
    private val _recommendationTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val recommendationTrigger = _recommendationTrigger.asSharedFlow()

    private val _recommendationResults = MutableSharedFlow<List<MusicTrack>>(extraBufferCapacity = 1)
    val recommendationResults = _recommendationResults.asSharedFlow()

    fun tryEmitRecommendationTrigger() {
        _recommendationTrigger.tryEmit(Unit)
    }

    fun tryEmitRecommendationResults(list: List<MusicTrack>) {
        _recommendationResults.tryEmit(list)
    }

    private val _libraryLoadTrigger = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1)
    val libraryLoadTrigger = _libraryLoadTrigger.asSharedFlow()

    fun tryEmitLibraryLoad() {
        _libraryLoadTrigger.tryEmit(Unit)
    }
}
