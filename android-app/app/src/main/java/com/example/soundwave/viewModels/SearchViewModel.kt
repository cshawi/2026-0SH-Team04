package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    val query = mutableStateOf("")
    val results = mutableStateOf(listOf<String>())
    val isLoading = mutableStateOf(false)

    fun onQueryChange(q: String) {
        query.value = q
    }

    fun search() {
        val q = query.value.trim()
        if (q.isEmpty()) return
        isLoading.value = true
        viewModelScope.launch {
            // simulate network/search delay
            delay(700)
            // fake results for now
            results.value = listOf("$q - Result 1", "$q - Result 2", "$q - Result 3")
            isLoading.value = false
        }
    }
}
