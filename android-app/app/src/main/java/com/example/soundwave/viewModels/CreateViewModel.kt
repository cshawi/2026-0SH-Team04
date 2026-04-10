package com.example.soundwave.viewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.soundwave.data.remote.dto.track.CreateTrackRequestDto
import com.example.soundwave.data.repository.TrackRepository
import com.example.soundwave.data.repository.JobRepository
import com.example.soundwave.data.repository.MusicRepository
import com.example.soundwave.models.MusicGenerationResult
import com.example.soundwave.models.MusicTrack
import com.example.soundwave.models.StyleItem
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.CompactDisc
import compose.icons.fontawesomeicons.solid.Drum
import compose.icons.fontawesomeicons.solid.Guitar
import compose.icons.fontawesomeicons.solid.Headphones
import compose.icons.fontawesomeicons.solid.Music
import compose.icons.fontawesomeicons.solid.RecordVinyl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Route

class CreateViewModel: BaseViewModel() {

    var isInstrumental by mutableStateOf(false)
    var isCustomMode by mutableStateOf(false)
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)

    val styles = listOf(
        StyleItem("Pop", FontAwesomeIcons.Solid.Music, Color(0xFF7B61FF)),
        StyleItem("Hip-Hop", FontAwesomeIcons.Solid.RecordVinyl, Color(0xFFFF4D9D)),
        StyleItem("Jazz", FontAwesomeIcons.Solid.Music, Color(0xFF4DA3FF)),
        StyleItem("Électro", FontAwesomeIcons.Solid.CompactDisc, Color(0xFF9B6BFF)),
        StyleItem("Rock", FontAwesomeIcons.Solid.Guitar, Color(0xFFFF8A3D)),
        StyleItem("Lo-fi", FontAwesomeIcons.Solid.Headphones, Color(0xFF38D9A9)),
        StyleItem("Afrobeat", FontAwesomeIcons.Solid.Drum, Color(0xFFF6C453))
    )

    var selectedStyle by mutableStateOf<StyleItem?>(null)

    var isGenerating by mutableStateOf(false)
    var generationResult by mutableStateOf<MusicGenerationResult?>(null)
    var generationError by mutableStateOf<String?>(null)
    var generationTaskId by mutableStateOf<String?>(null)

    private val trackRepository = TrackRepository()
    private val jobRepository = JobRepository()

    fun onImageSelected(uri: Uri?) {
        imageUri = uri
    }

    fun onGenerateClicked() {

        if (isGenerating) return

        isGenerating = true
        generationError = null
        generationResult = null
        generationTaskId = null

        val createReq = CreateTrackRequestDto(
            is_personalized = selectedStyle?.name != null,
            is_instrumental = isInstrumental,
            prompt = description,
            title = title,
            style = selectedStyle?.name,
            coverUrl = null
        )

        viewModelScope.launch {
            val startResult = trackRepository.addTrack(createReq)
            val resp = startResult.getOrElse {
                generationError = it.message ?: "Erreur inconnue"
                isGenerating = false
                return@launch
            }

            val taskId = resp.jobId ?: resp.taskId
            generationTaskId = taskId

            val maxAttempts = 22
            val delayMs = 3000L

            repeat(maxAttempts) { attempt ->
                val jobResult = jobRepository.getJobStatus(taskId ?: "")
                val job = jobResult.getOrElse {
                    generationError = it.message ?: "Erreur inconnue"
                    return@launch
                }

                val trackIds = job.tracks ?: emptyList()
                if (trackIds.isNotEmpty()) {
                    // fetch each track and map to MusicTrack
                    val tracks = mutableListOf<MusicTrack>()
                    for (tid in trackIds) {
                        val tRes = trackRepository.getTrackById(tid)
                        tRes.onSuccess { td ->
                            //val parsedId = td.id.toIntOrNull() ?: kotlin.math.abs(td.id.hashCode())
                            tracks.add(
                                MusicTrack(
                                    id = td.id,
                                    title = td.title,
                                    styleName = td.style,
                                    duration = td.duration?.toInt() ?: 0,
                                    createdAt = td.createdAt,
                                    audioUrl = td.audioUrl,
                                    coverUrl = td.coverUrl,
                                    username = td.username
                                )
                            )

                        }
                    }

                    generationResult = MusicGenerationResult(
                        taskId = taskId ?: "",
                        tracks = tracks
                    )
                    isGenerating = false
                    return@launch
                }

                if (attempt < maxAttempts - 1) {
                    delay(delayMs)
                }
            }

            generationError = "Délai dépassé, réessaie plus tard."
            isGenerating = false
        }
    }


}