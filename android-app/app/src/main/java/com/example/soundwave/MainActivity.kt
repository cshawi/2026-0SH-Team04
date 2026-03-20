package com.example.soundwave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.example.soundwave.navigation.NavGraph
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.ui.theme.SoundWaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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