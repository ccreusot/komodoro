package fr.cedriccreusot.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

sealed class PomodoroState(open val duration: Duration) {
    data class Idle(override val duration: Duration) : PomodoroState(duration)
    data class Running(override val duration: Duration) : PomodoroState(duration)
    data class Paused(override val duration: Duration) : PomodoroState(duration)
    object Finished : PomodoroState(0L.milliseconds)
}

class PomodoroViewModel(duration: Duration = 25.minutes, val pomodoroMax: Int = 4) {

    private val _state = MutableStateFlow(PomodoroState.Idle(duration))
    val state = _state.asStateFlow()
}