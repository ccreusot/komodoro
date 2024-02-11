package fr.cedriccreusot.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

sealed class PomodoroState(open val duration: Duration) {
    sealed class Pomodoro(override val duration: Duration) : PomodoroState(duration) {
        object Finished : Pomodoro(0.seconds)
        data class Idle(override val duration: Duration) : Pomodoro(duration)
        data class Running(override val duration: Duration) : Pomodoro(duration)
    }

    sealed class Break(override val duration: Duration) : PomodoroState(duration) {
        object Finished : Break(0.seconds)

        data class Idle(override val duration: Duration) : Break(duration)
        data class Running(override val duration: Duration) : Break(duration)
    }

}

class PomodoroViewModel(private val pomodoroDuration: Duration = 25.minutes, private val breakDuration: Duration = 5.minutes, val pomodoroMax: Int = 4) {

    private val _state = MutableStateFlow<PomodoroState>(PomodoroState.Pomodoro.Idle(pomodoroDuration))
    val state = _state.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    private var jobTimer: Job? = null

    fun start() {
        jobTimer = viewModelScope.launch {
            while(isActive && state.value.duration > 0.seconds) {
                delay(1.seconds)
                val nextDuration = state.value.duration - 1.seconds
                when (state.value) {
                    is PomodoroState.Pomodoro -> _state.emit(PomodoroState.Pomodoro.Running(nextDuration))
                    is PomodoroState.Break -> _state.emit(PomodoroState.Break.Running(nextDuration))
                }
            }
            when (state.value) {
                is PomodoroState.Pomodoro -> _state.emit(PomodoroState.Pomodoro.Finished)
                is PomodoroState.Break -> _state.emit(PomodoroState.Break.Finished)
            }
            next()
        }
    }

    fun pause() {
        jobTimer?.cancel()
        viewModelScope.launch {
            when (state.value) {
                is PomodoroState.Pomodoro -> _state.emit(PomodoroState.Pomodoro.Idle(state.value.duration))
                is PomodoroState.Break -> _state.emit(PomodoroState.Break.Idle(state.value.duration))
            }
        }
    }

    fun next() {
        jobTimer?.cancel()
        viewModelScope.launch {
            if (state.value is PomodoroState.Pomodoro) {
                _state.emit(PomodoroState.Break.Running(breakDuration))
                start()
            } else if (state.value is PomodoroState.Break) {
                _state.emit(PomodoroState.Pomodoro.Running(pomodoroDuration))
                start()
            }
        }
    }
}