package fr.cedriccreusot.viewmodel

import app.cash.turbine.test
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class PomodoroViewModelTest : FunSpec() {
    init {
        test("Given the pomodorule, when a PomodoroViewModel is created, it should return a state that is not started with the default duration and have the number of pomodoro max defined to 4").config(
            coroutineTestScope = true
        ) {
            val viewModel = PomodoroViewModel()

            viewModel.state.value shouldBe PomodoroState.Pomodoro.Idle(25.minutes)
            viewModel.pomodoroMax shouldBe 4
        }

        test("Given a number of pomodoro is set to 2, when a PomodoroViewModel is created, it should return a state that is not started with the default duration and have the number of pomodoro max defined to 2").config(
            coroutineTestScope = true
        ) {
            val viewModel = PomodoroViewModel(pomodoroMax = 2)

            viewModel.state.value shouldBe PomodoroState.Pomodoro.Idle(25.minutes)
            viewModel.pomodoroMax shouldBe 2
        }

        test("Given a duration is set in milliseconds, when subscribe to the timer view model, it should return a state that is not started with the same start duration").config(
            coroutineTestScope = true
        ) {
            val viewModel = PomodoroViewModel(duration = 1_000L.milliseconds)

            viewModel.state.value shouldBe PomodoroState.Pomodoro.Idle(1_000L.milliseconds)
        }

        test("Given the viewModel is initialized, when we start the pomodoro, it should return a state running with the elapsed time every seconds").config(
            coroutineTestScope = true
        ) {
            val viewModel = PomodoroViewModel(duration = 3.seconds)
            viewModel.start()
            viewModel.state.test(timeout = 4.seconds) {
                skipItems(1)
                awaitItem() shouldBe PomodoroState.Pomodoro.Running(2.seconds)
            }
        }

        test("Given the viewModel is initialized, when we start the pomodoro, it should return a state Finished when all the time is elapsed").config(
            coroutineTestScope = true
        ) {
            val viewModel = PomodoroViewModel(duration = 1.seconds)
            viewModel.start()
            viewModel.state.test(timeout = 2.seconds) {
                skipItems(2)
                awaitItem() shouldBe PomodoroState.Pomodoro.Finished
            }
        }
    }
}