package fr.cedriccreusot.viewmodel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.collect
import kotlin.time.Duration.Companion.milliseconds

class PomodoroViewModelTest : FunSpec() {
    init {
        test("Given a duration is set in milliseconds, when subscribe to the timer view model, it should return a state that is not started with the same start duration").config(coroutineTestScope = true) {
            val viewModel = PomodoroViewModel(duration = 1_000L.milliseconds)

            val state = viewModel.state.collect() shouldBe PomodoroState.Idle(1_000L.milliseconds)
        }

    }
}