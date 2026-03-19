package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LibraryViewModel: ViewModel() {
    val items = mutableStateOf(listOf<String>())

    init {
        // load placeholder items (read DownloadStore)
        items.value = listOf()
    }

    fun setItems(list: List<String>) {
        items.value = list
    }
}
