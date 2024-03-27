package stack

import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.DurationUnit

class ExpBackoffStack<T>(minBackoffDuration: Duration, maxBackoffDuration: Duration) : Stack<T> {
    private val stack = CASStack<T>()
    private val minDurationMillis = minBackoffDuration.toLong(DurationUnit.MILLISECONDS)
    private val maxDurationMillis = maxBackoffDuration.toLong(DurationUnit.MILLISECONDS)


    override fun top() = stack.top()?.value

    override fun push(value: T) {
        val node = CASStack.Node(value)
        var backoffDuration = minDurationMillis
        while (true) {
            if (stack.tryPush(node).isOk()) return
            Thread.sleep(backoffDuration)
            backoffDuration = max(backoffDuration * 2, maxDurationMillis)
        }
    }

    override fun pop(): T? {
        while (true) {
            stack.tryPop().runOk { return it?.value }
        }
    }
}
