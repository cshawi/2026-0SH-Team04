package com.example.soundwave.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundwave.data.remote.TokenProvider
import com.example.soundwave.data.repository.UserRepository
import com.example.soundwave.data.repository.UserSession
import com.example.soundwave.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.soundwave.events.AppEvents

class MainViewModel : ViewModel() {

    val screenState = mutableStateOf("splash")
    val isReady = mutableStateOf(false)
    val isLoadingUser = mutableStateOf(false)

    fun startInit(context: Context) {

        if (isReady.value) return

        viewModelScope.launch(Dispatchers.IO) {

            try {
                TokenProvider.init(context)
            } catch (t: Throwable) {
                Log.w("MainVM", "Init error", t)
            }

            //delay(100)

            withContext(Dispatchers.Main) {
                screenState.value = "intro"
                isReady.value = true
            }
        }
    }

    fun loadUser() {

        viewModelScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                isLoadingUser.value = true
            }

            try {
                val repo = UserRepository()
                val result = repo.tryRestoreSession()

                if (result.isSuccess) {
                    val user = result.getOrNull()

                    if (user != null) {
                        val mappedUser = User(
                            id = user.id,
                            name = user.username,
                            email = user.access?.email ?: "",
                            password = "",
                            avatarUrl = user.avatarUrl,
                            createdAt = user.createdAt
                        )

                        withContext(Dispatchers.Main) {
                            UserSession.login(mappedUser)
                            AppEvents.tryEmitLibraryLoad()
                        }
                    }
                }

            } catch (t: Throwable) {
                Log.w("MainVM", "Restore error", t)
            }

            withContext(Dispatchers.Main) {
                isLoadingUser.value = false
                screenState.value = "app"
            }
        }
    }
}