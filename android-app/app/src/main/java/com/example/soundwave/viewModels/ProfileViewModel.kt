package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateOf
import com.example.soundwave.models.User
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {

    var isLoggedIn = mutableStateOf(false)
        private set

    var user = mutableStateOf<User?>(null)
        private set

    private val testUsers = listOf(
        User(id = "1", name = "Alice", email = "alice@music.com", avatarUrl = null),
        User(id = "2", name = "Bob", email = "bob@music.com", avatarUrl = null)
    )

    fun loginTestUser(name: String, email: String) {
        user.value = User(
            id = "0",
            name = name,
            email = email,
            avatarUrl = null
        )
        isLoggedIn.value = true
    }

    fun logout() {
        user.value = null
        isLoggedIn.value = false
    }
}