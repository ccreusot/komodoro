package fr.cedriccreusot.viewmodel

import app.cash.turbine.test
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
            val viewModel = PomodoroViewModel(pomodoroDuration = 1_000L.milliseconds)

            viewModel.state.value shouldBe PomodoroState.Pomodoro.Idle(1_000L.milliseconds)
        }

        test("Given the viewModel is initialized, when we start the pomodoro, it should return a state running with the elapsed time every seconds").config(
            coroutineTestScope = true
        ) {
            val viewModel = PomodoroViewModel(pomodoroDuration = 3.seconds)
            viewModel.state.test {
                viewModel.start()
                skipItems(1)
                awaitItem() shouldBe PomodoroState.Pomodoro.Running(2.seconds)
            }
        }

        test("Given the viewModel is initialized, when we start the pomodoro, it should return a state Finished when all the time is elapsed").config(
            coroutineTestScope = true
        ) {
            val viewModel = PomodoroViewModel(pomodoroDuration = 1.seconds)
            viewModel.state.test {
                viewModel.start()
                skipItems(2)
                awaitItem() shouldBe PomodoroState.Pomodoro.Finished
            }
        }

        test("Given the viewModel and the pomodoro is started, when we call pause it should pause the pomodoro").config(coroutineTestScope = true) {
            val viewModel = PomodoroViewModel(pomodoroDuration = 5.seconds)
            viewModel.state.test {
                viewModel.start()
                skipItems(3)
                viewModel.pause()
                awaitItem() shouldBe PomodoroState.Pomodoro.Idle(3.seconds)
            }
        }

        test("Given the viewModel is started, when we call next it should return to the next state from Pomodoro to Break if the counter of Pomodoro is less than the max count").config(coroutineTestScope = true) {
            val viewModel = PomodoroViewModel(pomodoroMax = 2, pomodoroDuration = 5.seconds)
            viewModel.state.test {
                viewModel.start()
                skipItems(2)
                viewModel.next()
                awaitItem() shouldBe PomodoroState.Break.Running(5.minutes)
            }
        }

        test("Given the viewModel is started on break, if the break is paused, it should return the break state in pause").config(coroutineTestScope = true) {
            val viewModel = PomodoroViewModel(pomodoroMax = 2, pomodoroDuration = 5.seconds, breakDuration = 4.seconds)
            viewModel.state.test {
                viewModel.start()
                skipItems(1)
                viewModel.next()
                skipItems(1)
                viewModel.pause()
                awaitItem() shouldBe PomodoroState.Break.Idle(4.seconds)
            }
        }

        test("Given the viewModel started at the end of the time, the next state should be the Break one").config(coroutineTestScope = true) {
            val viewModel = PomodoroViewModel(pomodoroMax = 2, pomodoroDuration = 1.seconds)
            viewModel.state.test {
                viewModel.start()
                skipItems(3)
                awaitItem() shouldBe PomodoroState.Break.Running(5.minutes)
            }
        }

        test("Givent the viewModel is at the end of the break, the next state should be the Pomodoro one").config(coroutineTestScope = true) {
            val viewModel = PomodoroViewModel(pomodoroMax = 2, pomodoroDuration = 3.seconds, breakDuration = 1.seconds)
            viewModel.state.test {
                viewModel.start()
                skipItems(1)
                viewModel.next()
                skipItems(3)
                awaitItem() shouldBe PomodoroState.Pomodoro.Running(3.seconds)
            }
        }
    }
}