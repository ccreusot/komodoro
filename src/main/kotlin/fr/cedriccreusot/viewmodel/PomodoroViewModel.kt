package fr.cedriccreusot.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

sealed class PomodoroState(open val duration: Duration) {
    sealed class Pomodoro(override val duration: Duration) : PomodoroState(duration) {
        object Finished : PomodoroState(0.seconds)
        data class Idle(override val duration: Duration) : Pomodoro(duration)
        data class Running(override val duration: Duration) : Pomodoro(duration)
    }
}

class PomodoroViewModel(duration: Duration = 25.minutes, val pomodoroMax: Int = 4) {

    private val _state = MutableStateFlow<PomodoroState>(PomodoroState.Pomodoro.Idle(duration))
    val state = _state.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    private var jobTimer: Job? = null

    fun start() {
        jobTimer = viewModelScope.launch {
            while(isActive && state.value.duration > 0.seconds) {
                delay(1.seconds)
                _state.emit(PomodoroState.Pomodoro.Running(state.value.duration - 1.seconds))
            }
            _state.emit(PomodoroState.Pomodoro.Finished)
        }
    }

    fun pause() {
        jobTimer?.cancel()
        viewModelScope.launch {
            _state.emit(PomodoroState.Pomodoro.Idle(state.value.duration))
        }
    }
}