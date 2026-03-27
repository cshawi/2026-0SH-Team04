package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateOf
import com.example.soundwave.models.User
import com.example.soundwave.data.TestDataProvider
import com.example.soundwave.data.repository.UserSession
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class ProfileViewModel : BaseViewModel() {

    var currentUser = mutableStateOf<User?>(UserSession.currentUser.value)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    private val users = TestDataProvider.users

    init {
        currentUser.value = UserSession.currentUser.value
    }

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
            UserSession.login(user)
            errorMessage.value = null
            true
        } else {
            errorMessage.value = "Email ou mot de passe incorrect"
            false
        }
    }

    fun updateAvatar(url: String) {
        currentUser.value = currentUser.value?.copy(avatarUrl = url)
        val user = currentUser.value

        if(user != null) {

            val idx = users.indexOfFirst { it.id == user.id }
            if (idx >= 0) {
                users[idx].avatarUrl = user.avatarUrl
            }

            UserSession.login(user)
        }
    }

    fun logout() {
        currentUser.value = null
        UserSession.logout()
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
            UserSession.logout()
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

            var user = currentUser.value

            if (user != null) {

                user = user.copy(
                    name = newName,
                    email = newEmail
                )

                val idx = users.indexOfFirst { it.id == user.id }
                if (idx >= 0) {
                    users[idx] = user
                }

                currentUser.value = user
                UserSession.login(user)
            }

            isLoading.value = false
        }
    }
}
