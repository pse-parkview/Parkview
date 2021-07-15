package com.parkview.parkview.benchmark

import com.parkview.parkview.git.*


/**
 * A single datapoint, contains the problem description for the matrix and
 * a list of preconditioners.
 *
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 * @param preconditioners list of [Preconditioner]
 */
data class PreconditionerDatapoint(
    val rows: Long,
    val columns: Long,
    val nonzeros: Long,
    val preconditioners: List<Preconditioner>
)

/**
 * A single preconditioner, part of [PreconditionerBenchmarkResult].
 *
 * @param name preconditioner name
 * @param generateComponents
 * @param generateTime
 * @param applyComponents
 * @param applyTime
 * @param completed
 */
data class Preconditioner(
    val name: String,
    val generateComponents: List<Component>,
    val generateTime: Double,
    val applyComponents: List<Component>,
    val applyTime: Double,
    val completed: Boolean,
)

/**
 * This is a benchmark result for the benchmarks
 * of the Preconditioner format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param benchmark type of benchmark
 * @param datapoints datapoints for this benchmark
 */
data class PreconditionerBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: BenchmarkType,
    val datapoints: List<PreconditionerDatapoint>,
) : BenchmarkResult {
    override fun getSummaryValue() = getGenerateTimes().mapValues { (_, values) -> values.sorted()[values.size / 2] }

    private fun getGenerateTimes(): Map<String, List<Double>> {
        val times = mutableMapOf<String, MutableList<Double>>()
        for (datapoint in datapoints) {
            for (preconditioner in datapoint.preconditioners) {
                times.getOrPut(preconditioner.name) { mutableListOf() }.add(preconditioner.generateTime)
            }
        }

        return times
    }
}