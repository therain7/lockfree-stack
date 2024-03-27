package benchmark

import stack.Stack
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTime

const val MAX_ELEMENT_VALUE = 10000

val rand = Random.Default


class StackBench(
    private val stack: Stack<Int>,
    private val threadsCount: Int,
    private val opsCount: Int,
    private val maxWorkload: Duration
) {
    data class BenchResult(
        val avgLatency: Double,
        val avgThroughput: Double
    )

    fun bench(): BenchResult {
        val latch = CountDownLatch(1)

        val latencies = Array(threadsCount) { 0.0 }
        val threads = List(threadsCount) {
            thread {
                latch.await()
                latencies[it] = runOps()
            }
        }
        Thread.sleep(1000)

        val totalTime = measureTime {
            latch.countDown()
            threads.forEach { it.join() }
        }

        return BenchResult(
            avgLatency = latencies.average(),
            avgThroughput = opsCount * threadsCount / totalTime.toDouble(DurationUnit.SECONDS)
        )
    }

    private fun runOps(): Double {
        val latencies = mutableListOf<Duration>()
        repeat(opsCount) {
            val valToPush = rand.nextInt(MAX_ELEMENT_VALUE)
            val op = if (rand.nextBoolean())
                { -> stack.push(valToPush) }
            else { -> stack.pop() }

            latencies.addLast(measureTime { op() })

            val maxWorkloadTime = maxWorkload.toLong(DurationUnit.MILLISECONDS)
            if (maxWorkloadTime > 0) Thread.sleep(rand.nextLong(maxWorkloadTime))
        }

        return latencies.map { it.toLong(DurationUnit.NANOSECONDS) }.average()
    }
}
