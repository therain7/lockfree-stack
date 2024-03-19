package stack

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class EliminationBackoffStackTests {
    private val stack = EliminationBackoffStack<Int>()

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
