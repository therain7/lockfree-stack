package stack

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.junit.jupiter.api.Test

class SequentialIntStack : Stack<Int> {
    private val stack = ArrayDeque<Int>()

    override fun top() = stack.firstOrNull()
    override fun pop() = stack.removeFirstOrNull()
    override fun push(value: Int) = stack.addFirst(value)
}

class LockFreeStackTest {
    private val stack = LockFreeStack<Int>()

    @Operation
    fun top() = stack.top()

    @Operation
    fun push(value: Int) = stack.push(value)

    @Operation
    fun pop() = stack.pop()

    @Test
    fun test() =
        ModelCheckingOptions()
            .sequentialSpecification(SequentialIntStack::class.java)
            .checkObstructionFreedom(true)
            .check(this::class)
}
