package com.example.soundwave

import android.app.Application
import com.example.soundwave.data.remote.TokenProvider

class SoundWaveApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize TokenProvider so EncryptedSharedPreferences are ready
        TokenProvider.init(this)
    }
}
