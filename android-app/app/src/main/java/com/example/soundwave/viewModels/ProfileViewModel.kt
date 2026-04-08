package com.example.soundwave.viewModels

import androidx.compose.runtime.mutableStateOf
import com.example.soundwave.models.User
import com.example.soundwave.data.TestDataProvider
import com.example.soundwave.data.repository.UserSession
import androidx.lifecycle.viewModelScope
import com.example.soundwave.data.repository.UserRepository
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
        viewModelScope.launch {
            UserSession.currentUser.collect { usr ->
                currentUser.value = usr
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): Boolean {
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
        }

        isLoading.value = true
        val repo = UserRepository()
        val result = repo.signup(name.trim(), email.trim(), password.trim())
        isLoading.value = false

        return if (result.isSuccess) {
            errorMessage.value = null
            true
        } else {
            val ex = result.exceptionOrNull()
            errorMessage.value = ex?.message ?: "Erreur lors de l'inscription"
            false
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "Veuillez remplir tous les champs"
            return false
        }

        isLoading.value = true
        val repo = UserRepository()
        val result = repo.login(email.trim(), password)
        isLoading.value = false

        return if (result.isSuccess) {
            val resp = result.getOrNull()
            val userDto = resp?.user
            if (userDto != null) {
                val mapped = User(
                    id = userDto.id,
                    name = userDto.username,
                    email = userDto.access?.email ?: "",
                    password = "",
                    avatarUrl = userDto.avatarUrl,
                    createdAt = userDto.createdAt
                )
                currentUser.value = mapped
                UserSession.login(mapped)
                errorMessage.value = null
                true
            } else {
                errorMessage.value = "Réponse invalide du serveur"
                false
            }
        } else {
            val ex = result.exceptionOrNull()
            // errorMessage.value = ex?.message ?: "Email ou mot de passe incorrect"
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

    suspend fun logout() {
        isLoading.value = true
        val repo = UserRepository()
        repo.logout()
        isLoading.value = false

        currentUser.value = null
        UserSession.logout()
        errorMessage.value = null
    }

    suspend fun deleteAccount() {
        isLoading.value = true
        errorMessage.value = null
        val repo = UserRepository()
        val result = repo.deleteAccount()
        isLoading.value = false

        if (result.isSuccess) {
            currentUser.value = null
            UserSession.logout()
        } else {
            val ex = result.exceptionOrNull()
            errorMessage.value = ex?.message ?: "Impossible de supprimer le compte"
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
