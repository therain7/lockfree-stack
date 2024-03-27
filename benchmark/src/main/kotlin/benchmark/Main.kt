package benchmark

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.kandy.dsl.categorical
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import stack.EliminationBackoffStack
import stack.ExpBackoffStack
import stack.LockFreeStack
import stack.Stack
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


private fun Stack<Int>.fillStack(count: Int) {
    repeat(count) {
        push(rand.nextInt(MAX_ELEMENT_VALUE))
    }
}

private fun benchStacks(threadsCounts: List<Int>, opsCount: Int, maxWorkload: Duration, plotNamePrefix: String) {
    val noBackoffLatencies = mutableListOf<Double>()
    val noBackoffThroughput = mutableListOf<Double>()

    val expBackoffLatencies = mutableListOf<Double>()
    val expBackoffThroughput = mutableListOf<Double>()

    val elimBackoffLatencies = mutableListOf<Double>()
    val elimBackoffThroughput = mutableListOf<Double>()

    threadsCounts.forEach {
        val noBackoffStack = LockFreeStack<Int>().apply { fillStack(opsCount / 2) }
        val noRes =
            StackBench(noBackoffStack, it, opsCount, maxWorkload).bench()
        noBackoffLatencies.addLast(noRes.avgLatency)
        noBackoffThroughput.addLast(noRes.avgThroughput)

        val expBackoffStack = ExpBackoffStack<Int>(
            minBackoffDuration = 5.toDuration(DurationUnit.MICROSECONDS),
            maxBackoffDuration = 50.toDuration(DurationUnit.MICROSECONDS)
        ).apply { fillStack(opsCount / 2) }
        val expRes =
            StackBench(expBackoffStack, it, opsCount, maxWorkload).bench()
        expBackoffLatencies.addLast(expRes.avgLatency)
        expBackoffThroughput.addLast(expRes.avgThroughput)

        val elimBackoffStack = EliminationBackoffStack<Int>(
            eliminationArrayCapacity = 8,
            eliminationMaxDuration = 50.toDuration(DurationUnit.MICROSECONDS)
        ).apply { fillStack(opsCount / 2) }
        val elimRes =
            StackBench(elimBackoffStack, it, opsCount, maxWorkload).bench()
        elimBackoffLatencies.addLast(elimRes.avgLatency)
        elimBackoffThroughput.addLast(elimRes.avgThroughput)
    }


    val dfLat = dataFrameOf(
        "threads" to threadsCounts + threadsCounts + threadsCounts,
        "latency" to noBackoffLatencies + expBackoffLatencies + elimBackoffLatencies,
        "Backoff" to List(threadsCounts.size) { "No backoff" }
                + List(threadsCounts.size) { "Exp backoff" }
                + List(threadsCounts.size) { "Elimination backoff" }
    )
    dfLat.groupBy("Backoff").plot {
        line {
            x("threads") {
                axis.name = "Threads"
                scale = categorical()
            }
            y("latency") { axis.name = "Average latency, ns" }
            color("Backoff")
        }
    }.save("${plotNamePrefix}_lat.png")


    val dfThroughput = dataFrameOf(
        "threads" to threadsCounts + threadsCounts + threadsCounts,
        "throughput" to noBackoffThroughput + expBackoffThroughput + elimBackoffThroughput,
        "Backoff" to List(threadsCounts.size) { "No backoff" }
                + List(threadsCounts.size) { "Exp backoff" }
                + List(threadsCounts.size) { "Elimination backoff" }
    )
    dfThroughput.groupBy("Backoff").plot {
        line {
            x("threads") {
                axis.name = "Threads"
                scale = categorical()
            }
            y("throughput") { axis.name = "Average throughput, op/s" }
            color("Backoff")
        }
    }.save("${plotNamePrefix}_throughput.png")

}


fun main() {
    benchStacks(
        threadsCounts = listOf(1, 4, 8, 16, 32),
        opsCount = 500_000,
        maxWorkload = Duration.ZERO,
        plotNamePrefix = "stacks"
    )
}
