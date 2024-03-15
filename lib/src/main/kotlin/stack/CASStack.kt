package stack

import java.util.concurrent.atomic.AtomicReference

internal class CASStack<T> {
    private val topRef = AtomicReference<Node<T>?>(null)

    fun top() = topRef.get()

    fun tryPush(node: Node<T>): Result<Unit, CASFailure> {
        val oldTop = topRef.get()
        node.next = oldTop
        return if (topRef.compareAndSet(oldTop, node)) Ok(Unit)
        else Err(CASFailure)
    }

    fun tryPop(): Result<Node<T>?, CASFailure> {
        val oldTop = topRef.get() ?: return Ok(null)
        val newTop = oldTop.next
        return if (topRef.compareAndSet(oldTop, newTop)) Ok(oldTop)
        else Err(CASFailure)
    }

    class Node<T>(val value: T) {
        var next: Node<T>? = null
    }

    object CASFailure
}
