package com.example.soundwave.viewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.soundwave.data.remote.GenerateMusicRequest
import com.example.soundwave.data.repository.MusicRepository
import com.example.soundwave.models.MusicGenerationResult
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

class CreateViewModel: ViewModel() {

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

    private val musicRepository = MusicRepository()

    fun onImageSelected(uri: Uri?) {
        imageUri = uri
    }

    fun onGenerateClicked() {

        if (isGenerating) return

        isGenerating = true
        generationError = null
        generationResult = null
        generationTaskId = null

        val request = GenerateMusicRequest(
            title = title,
            description = description,
            style = selectedStyle?.name,
            instrumental = isInstrumental
        )

        viewModelScope.launch {
            val startResult = musicRepository.generateMusic(request)
            val taskId = startResult.getOrElse {
                generationError = it.message ?: "Erreur inconnue"
                isGenerating = false
                return@launch
            }.taskId

            generationTaskId = taskId

            val maxAttempts = 12
            val delayMs = 3000L

            repeat(maxAttempts) { attempt ->
                val statusResult = musicRepository.checkStatusMapped(taskId)
                val status = statusResult.getOrElse {
                    generationError = it.message ?: "Erreur inconnue"
                    return@launch
                }

                if (status.tracks.isNotEmpty()) {
                    generationResult = status
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