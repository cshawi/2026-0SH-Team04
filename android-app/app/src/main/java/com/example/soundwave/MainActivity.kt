package com.example.soundwave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.soundwave.data.remote.TokenProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.soundwave.data.repository.UserRepository
import com.example.soundwave.data.repository.UserSession
import com.example.soundwave.models.User
import android.util.Log
import androidx.compose.runtime.CompositionLocalProvider
import com.example.soundwave.navigation.NavGraph
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.theme.SoundWaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenProvider.init(this)
        lifecycleScope.launch {
            val repo = UserRepository()
            val result = withContext(Dispatchers.IO) { repo.tryRestoreSession() }
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    Log.d("MainActivity", "Session restored for user: ${user.username}")

                    val mappedUser = User(
                        id = user.id,
                        name = user.username,
                        email = user.access?.email ?: "",
                        password = "",
                        avatarUrl = user.avatarUrl
                    )

                    UserSession.login(mappedUser)
                } else {
                    Log.d("MainActivity", "No valid session found at startup")
                }
            } else {
                Log.w("MainActivity", "Error while restoring session", result.exceptionOrNull())
            }
        }
        enableEdgeToEdge()
        setContent {
            SoundWaveTheme {
                CompositionLocalProvider(LocalActivity provides this@MainActivity) {
                    NavGraph()
                }
            }
        }
    }
}