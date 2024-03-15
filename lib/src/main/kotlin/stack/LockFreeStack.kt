package stack

class LockFreeStack<T> : Stack<T> {
    private val stack = CASStack<T>()

    override fun top() = stack.top()?.value

    override fun push(value: T) {
        val node = CASStack.Node(value)
        while (true) {
            if (stack.tryPush(node).isOk()) return
        }
    }

    override fun pop(): T? {
        while (true) {
            stack.tryPop().runOk { return it?.value }
        }
    }
}
