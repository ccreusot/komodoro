package fr.cedriccreusot.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroViewModel(
    private val pomodoroDuration: Duration = 25.minutes,
    private val breakDuration: Duration = 5.minutes,
    private val longBreakDuration: Duration = 15.minutes,
    val pomodoroMax: Int = 4
) {

    private val _state = MutableStateFlow<PomodoroState>(PomodoroState.Pomodoro.Idle(pomodoroDuration))
    val state = _state.asStateFlow()

    private val _pomodoroCount = MutableStateFlow(0)
    val pomodoroCount = _pomodoroCount.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    private var jobTimer: Job? = null

    fun start() {
        jobTimer?.cancel()
        jobTimer = viewModelScope.launch {
            while (isActive && state.value.duration > 0.seconds) {
                delay(1.seconds)
                val nextDuration = state.value.duration - 1.seconds
                when (state.value) {
                    is PomodoroState.Pomodoro -> _state.emit(PomodoroState.Pomodoro.Running(nextDuration))
                    is PomodoroState.Break -> _state.emit(PomodoroState.Break.Running(nextDuration))
                    is PomodoroState.LongBreak -> _state.emit(PomodoroState.LongBreak.Running(nextDuration))
                }
            }
            when (state.value) {
                is PomodoroState.Pomodoro -> _state.emit(PomodoroState.Pomodoro.Finished)
                is PomodoroState.Break -> _state.emit(PomodoroState.Break.Finished)
                is PomodoroState.LongBreak -> _state.emit(PomodoroState.LongBreak.Finished)
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
                is PomodoroState.LongBreak -> _state.emit(PomodoroState.LongBreak.Idle(state.value.duration))
            }
        }
    }

    fun next() {
        jobTimer?.cancel()
        viewModelScope.launch {
            when (state.value) {
                is PomodoroState.Pomodoro -> {
                    if (pomodoroCount.value + 1 == pomodoroMax) {
                        _state.emit(PomodoroState.LongBreak.Running(longBreakDuration))
                        start()
                        return@launch
                    }
                    _pomodoroCount.inc()
                    _state.emit(PomodoroState.Break.Running(breakDuration))
                    start()
                }

                is PomodoroState.Break -> {
                    _state.emit(PomodoroState.Pomodoro.Running(pomodoroDuration))
                    start()
                }

                is PomodoroState.LongBreak -> {
                    _pomodoroCount.emit(0)
                    _state.emit(PomodoroState.Pomodoro.Running(pomodoroDuration))
                    start()
                }
            }
        }
    }

    fun stop() {
        jobTimer?.cancel()
        viewModelScope.launch {
            _pomodoroCount.emit(0)
            _state.emit(PomodoroState.Pomodoro.Idle(pomodoroDuration))
        }
    }

    private suspend fun MutableStateFlow<Int>.inc() {
        emit(this.value + 1)
    }
}
