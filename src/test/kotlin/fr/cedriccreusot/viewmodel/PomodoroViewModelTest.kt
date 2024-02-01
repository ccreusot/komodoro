package fr.cedriccreusot.viewmodel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class PomodoroViewModelTest : FunSpec() {
    init {
        test("Given the pomodorule, when a PomodoroViewModel is created, it should return a state that is not started with the default duration and have the number of pomodoro max defined to 4").config(coroutineTestScope = true) {
            val viewModel = PomodoroViewModel()

            viewModel.state.value shouldBe PomodoroState.Idle(25.minutes)
            viewModel.pomodoroMax shouldBe 4
        }

        test("Given a number of pomodoro is set to 2, when a PomodoroViewModel is created, it should return a state that is not started with the default duration and have the number of pomodoro max defined to 2").config(coroutineTestScope = true) {
            val viewModel = PomodoroViewModel(pomodoroMax = 2)

            viewModel.state.value shouldBe PomodoroState.Idle(25.minutes)
            viewModel.pomodoroMax shouldBe 2
        }

        test("Given a duration is set in milliseconds, when subscribe to the timer view model, it should return a state that is not started with the same start duration").config(coroutineTestScope = true) {
            val viewModel = PomodoroViewModel(duration = 1_000L.milliseconds)

            viewModel.state.value shouldBe PomodoroState.Idle(1_000L.milliseconds)
        }
    }
}