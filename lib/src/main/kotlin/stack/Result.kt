package stack

internal data class Ok<T, E>(val value: T) : Result<T, E>()
internal data class Err<T, E>(val err: E) : Result<T, E>()

internal sealed class Result<T, E> {
    fun isOk() = this is Ok

    inline fun runOk(f: (T) -> Unit) {
        when (this) {
            is Ok -> f(this.value)
            is Err -> {}
        }
    }
}
