package fr.cedriccreusot.extension

import kotlin.time.Duration

// Format seconds into minutes and seconds
fun Duration.formatToMinutesAndSeconds(): String {
    val minutes = this.inWholeMinutes
    val seconds = this.inWholeSeconds % 60
    return "$minutes:${if (seconds < 10) "0$seconds" else seconds}"
}
