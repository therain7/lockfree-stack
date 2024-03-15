package stack

interface Stack<T> {
    fun top(): T?

    fun push(value: T)
    fun pop(): T?
}
