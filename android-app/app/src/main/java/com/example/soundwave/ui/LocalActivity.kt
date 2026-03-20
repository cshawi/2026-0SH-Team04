package com.example.soundwave.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.staticCompositionLocalOf


val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("No ComponentActivity provided in CompositionLocal")
}
