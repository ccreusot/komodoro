package fr.cedriccreusot.extension

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class DurationExtensionsTest : FunSpec() {
    init {
        test("Given a duration of 1 minute and 30 seconds, when we format it, it should return 1:30") {
            val duration = 90.seconds
            duration.formatToMinutesAndSeconds() shouldBe "1:30"
        }

        test("Given a duration of 1 minute and 5 seconds, when we format it, it should return 1:05") {
            val duration = 65.seconds
            duration.formatToMinutesAndSeconds() shouldBe "1:05"
        }
    }
}
