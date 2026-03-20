package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateOf
import com.example.soundwave.models.User
import com.example.soundwave.data.TestDataProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class ProfileViewModel : ViewModel() {

    var currentUser = mutableStateOf<User?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    private val users = TestDataProvider.users

    fun register(name: String, email: String, password: String): Boolean {
        when {
            name.isBlank() || email.isBlank() || password.isBlank() -> {
                errorMessage.value = "Tous les champs sont requis"
                return false
            }
            password.length < 6 -> {
                errorMessage.value = "Le mot de passe doit contenir au moins 6 caractères"
                return false
            }
            !isValidEmail(email) -> {
                errorMessage.value = "Format d'email invalide"
                return false
            }
            users.any { it.email.equals(email, ignoreCase = true) } -> {
                errorMessage.value = "Cet email est déjà utilisé"
                return false
            }
        }

        errorMessage.value = null
        return true
    }

    fun login(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "Veuillez remplir tous les champs"
            return false
        }

        val hashedPassword = hashPassword(password)
        val user = users.find {
            it.email.equals(email, ignoreCase = true) &&
                    it.password == hashedPassword
        }

        return if (user != null) {
            currentUser.value = user
            errorMessage.value = null
            true
        } else {
            errorMessage.value = "Email ou mot de passe incorrect"
            false
        }
    }

    fun updateAvatar(url: String) {
        currentUser.value = currentUser.value?.copy(avatarUrl = url)
    }

    fun logout() {
        currentUser.value = null
        errorMessage.value = null
    }

    fun deleteAccount() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            delay(500)

            currentUser.value?.let { user ->
                users.removeAll { it.id == user.id }
            }

            currentUser.value = null
            isLoading.value = false
        }
    }


    private fun hashPassword(password: String): String {
        return password.hashCode().toString()
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )
        return pattern.matcher(email).matches()
    }

    fun updateUserInfo(newName: String, newEmail: String) {
        viewModelScope.launch {
            isLoading.value = true
            delay(500)

            currentUser.value = currentUser.value?.copy(
                name = newName,
                email = newEmail
            )

            isLoading.value = false
        }
    }
}