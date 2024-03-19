package stack

import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val CAPACITY = 10
private const val DURATION_MICRO = 50

class EliminationBackoffStack<T> : Stack<T> {
    private val stack = CASStack<T>()
    private val eliminationArray =
        EliminationArray<T?>(CAPACITY, DURATION_MICRO.toDuration(DurationUnit.MICROSECONDS))

    override fun top() = stack.top()?.value

    override fun push(value: T) {
        val node = CASStack.Node(value)
        while (true) {
            if (stack.tryPush(node).isOk()) return
            eliminationArray.visit(value, CAPACITY).runOk {
                if (it == null) return
            }
        }
    }

    override fun pop(): T? {
        while (true) {
            stack.tryPop().runOk { return it?.value }
            eliminationArray.visit(null, CAPACITY).runOk {
                if (it != null) return it
            }
        }
    }
}
