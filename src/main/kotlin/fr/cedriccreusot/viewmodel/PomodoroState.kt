package fr.cedriccreusot.viewmodel

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class PomodoroState(open val duration: Duration) {
    sealed class Pomodoro(override val duration: Duration) : PomodoroState(duration) {
        data object Finished : Pomodoro(0.seconds)
        data class Idle(override val duration: Duration) : Pomodoro(duration)
        data class Running(override val duration: Duration) : Pomodoro(duration)
    }

    sealed class Break(override val duration: Duration) : PomodoroState(duration) {
        data object Finished : Break(0.seconds)
        data class Idle(override val duration: Duration) : Break(duration)
        data class Running(override val duration: Duration) : Break(duration)
    }

    sealed class LongBreak(override val duration: Duration) : PomodoroState(duration) {
        data object Finished : LongBreak(0.seconds)
        data class Idle(override val duration: Duration) : LongBreak(duration)
        data class Running(override val duration: Duration) : LongBreak(duration)
    }
}
