package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.example.soundwave.events.AppEvents
import kotlinx.coroutines.flow.collect
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.data.TestDataProvider
import com.example.soundwave.data.repository.TrackRepository
import kotlin.random.Random
import kotlinx.coroutines.launch
import android.util.Log


class HomeViewModel : BaseViewModel() {

    var searchText = mutableStateOf("")

    // Use centralized test data provider so musics can be shared across the app
    val musicList: List<MusicTrack> = TestDataProvider.musics

    // Mutable state list that will hold backend recommendations
    val recommendationList: SnapshotStateList<MusicTrack> = mutableStateListOf()

    // results for the search box (fetched from server with filter == searchText)
    val searchResults: SnapshotStateList<MusicTrack> = mutableStateListOf()

    // discover list fetched from server without filter and randomized
    val discoverList: SnapshotStateList<MusicTrack> = mutableStateListOf()

    private val trackRepository = TrackRepository()

    // simple throttling / dedupe for recommendation fetches
    private var lastFetchAt: Long = 0L
    private var isFetching: Boolean = false
    private val fetchThrottleMs: Long = 1000L * 60 * 5 // 5 minutes

    fun updateSearch(text: String) {
        searchText.value = text
    }

    fun getFilteredMusic(): List<MusicTrack> {
        // kept for backward compatibility: fallback to local filter when searchResults empty
        if (searchResults.isNotEmpty()) return searchResults
        return musicList.filter {
            it.title.contains(searchText.value, ignoreCase = true)
        }
    }

    // perform a remote search using the TrackRepository filter parameter
    fun searchTracks(limit: Int = 20) {
        viewModelScope.launch {
            try {
                val resp = trackRepository.getTracks(limit = limit, filter = searchText.value).getOrNull()
                if (resp != null) {
                    val mapped = resp.mapNotNull { dto ->
                        try {
                            MusicTrack.fromDto(dto)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    searchResults.clear()
                    searchResults.addAll(mapped)
                }
            } catch (e: Exception) {
                Log.d("HomeViewModel", "searchTracks failed: ${e.message}")
            }
        }
    }

    // fetch discover list (no filter) and shuffle results for randomness
    fun fetchDiscover(limit: Int = 19) {
        viewModelScope.launch {
            try {
                val resp = trackRepository.getTracks(limit = limit).getOrNull()
                if (resp != null) {
                    val mapped = resp.mapNotNull { dto ->
                        try { MusicTrack.fromDto(dto) } catch (e: Exception) { null }
                    }.toMutableList()
                    mapped.shuffle(Random(System.currentTimeMillis()))
                    discoverList.clear()
                    discoverList.addAll(mapped)

                }
            } catch (e: Exception) {
                Log.d("HomeViewModel", "fetchDiscover failed: ${e.message}")
            }
        }
    }

    init {
        // Collect external triggers to refresh recommendations (e.g. emitted by the player)
        viewModelScope.launch {
            AppEvents.recommendationTrigger.collect {
                refreshRecommendations()
            }
        }
    }

    // Public helper to be called from UI or events. Non-suspending: launches in viewModelScope
    fun refreshRecommendations(limit: Int = 15, force: Boolean = false) {
        viewModelScope.launch {
            if (isFetching) return@launch
            val now = System.currentTimeMillis()
            if (!force && (now - lastFetchAt) < fetchThrottleMs) return@launch
            try {
                isFetching = true
                fetchRecommendationsImpl(limit)
                lastFetchAt = System.currentTimeMillis()
            } catch (e: Exception) {
                Log.d("HomeViewModel", "Failed to refresh recommendations: ${e.message}")
            } finally {
                isFetching = false
            }
        }
    }

    private suspend fun fetchRecommendationsImpl(limit: Int = 15) {
        try {
            val repo = TrackRepository()
            val resp = repo.getRecommendations(limit).getOrNull()
            if (resp != null) {
                val mapped = resp.mapNotNull { item ->
                    try {
                        MusicTrack.fromDto(item.track)
                    } catch (e: Exception) {
                        Log.d("HomeViewModel", "${e}")
                        null
                    }
                }
                recommendationList.clear()
                recommendationList.addAll(mapped)
                try {
                    AppEvents.tryEmitRecommendationResults(mapped)
                } catch (_: Exception) {
                    // ignore emission errors
                }
            } else {
                Log.d("HomeViewModel", "No recommendations returned")
            }
        } catch (e: Exception) {
            Log.d("HomeViewModel", "Failed to load recommendations: ${e.message}")
        }
    }

    fun launchRecommendation(limit: Int = 15) {
        refreshRecommendations(limit)
    }

}