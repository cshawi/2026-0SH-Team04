package com.example.soundwave.util

/**
 * Helpers to format durations.
 */
object TimeUtils {
    fun formatSecondsToMMSS(seconds: Int): String {
        val totalSeconds = seconds
        val minutes = totalSeconds / 60
        val secondsPart = totalSeconds % 60
        return "%d:%02d".format(minutes, secondsPart)
    }
}
