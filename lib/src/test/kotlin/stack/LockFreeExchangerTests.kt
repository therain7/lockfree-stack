package stack

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class LockFreeExchangerTests {
    @Test
    fun `simple exchange`() {
        val exchanger = LockFreeExchanger<Int>()
        val timeout = 1000.toDuration(DurationUnit.MILLISECONDS)

        lateinit var res1: Result<Int, TimeoutError>
        lateinit var res2: Result<Int, TimeoutError>

        val th1 = thread { res1 = exchanger.exchange(1, timeout) }
        val th2 = thread { res2 = exchanger.exchange(2, timeout) }

        th1.join()
        th2.join()

        assertEquals(Ok<Int, TimeoutError>(2), res1)
        assertEquals(Ok<Int, TimeoutError>(1), res2)
    }

    @Test
    fun timeout() {
        val exchanger = LockFreeExchanger<Int>()
        val timeout = 500.toDuration(DurationUnit.MILLISECONDS)

        val res = exchanger.exchange(1, timeout)

        assertEquals(Err<Int, TimeoutError>(TimeoutError), res)
    }
}
