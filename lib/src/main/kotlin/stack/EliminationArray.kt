package stack

import kotlin.random.Random
import kotlin.time.Duration

internal class EliminationArray<T>(capacity: Int, private val duration: Duration) {
    private val exchangers = Array(capacity) { LockFreeExchanger<T>() }
    private val random = Random.Default

    fun visit(value: T, range: Int): Result<T, TimeoutError> {
        val slot = random.nextInt(range)
        return (exchangers[slot].exchange(value, duration))
    }
}
