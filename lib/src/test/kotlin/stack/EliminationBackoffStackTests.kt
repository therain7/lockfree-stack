package stack

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class EliminationBackoffStackTests {
    private val stack =
        EliminationBackoffStack<Int>(10, 50.toDuration(DurationUnit.MICROSECONDS))

    @Operation
    fun top() = stack.top()

    @Operation
    fun push(value: Int) = stack.push(value)

    @Operation
    fun pop() = stack.pop()

    @Test
    fun test() =
        StressOptions()
            .sequentialSpecification(SequentialIntStack::class.java)
            .check(this::class)
}
