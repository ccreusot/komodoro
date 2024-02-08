package fr.cedriccreusot.viewmodel

import app.cash.turbine.test
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
            val dispatcher = UnconfinedTestDispatcher(testCoroutineScheduler)

            // I am trying to test like this because it will likely be this way in the future
            val job = launch(dispatcher) {
                viewModel.state.test(timeout = 4.seconds) {
                    skipItems(1)
                    awaitItem() shouldBe PomodoroState.Pomodoro.Running(2.seconds)
                }
            }

            viewModel.start()

            job.join()
        }

        test("Given the viewModel is initialized, when we start the pomodoro, it should return a state Finished when all the time is elapsed").config(
            coroutineTestScope = true
        ) {
            val viewModel = PomodoroViewModel(duration = 1.seconds)
            viewModel.state.test(timeout = 2.seconds) {
                viewModel.start()
                skipItems(2)
                awaitItem() shouldBe PomodoroState.Pomodoro.Finished
            }
        }

        test("Given the viewModel and the pomodoro is started, when we call pause it should pause the pomodoro") {
            val viewModel = PomodoroViewModel(duration = 5.seconds)
            viewModel.state.test {
                viewModel.start()
                skipItems(3)
                viewModel.pause()
                awaitItem() shouldBe PomodoroState.Pomodoro.Idle(3.seconds)
            }
        }
    }
}