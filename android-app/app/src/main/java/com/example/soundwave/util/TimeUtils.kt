package com.example.soundwave.util

/**
 * Helpers to format durations.
 */
object TimeUtils {
    fun formatSecondsToMMSS(secondsDouble: Double): String {
        val totalSeconds = secondsDouble.toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
