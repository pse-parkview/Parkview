package com.parkview.parkview.benchmark

import com.google.gson.GsonBuilder
import com.parkview.parkview.git.*


/**
 * A single operation, part of [BlasBenchmarkResult].
 *
 * @param name
 * @param time
 * @param flops
 * @param bandwidth
 * @param completed
 */
data class Operation(
    val name: String,
    val time: Double,
    val flops: Double,
    val bandwidth: Double,
    val completed: Boolean,
    val repetitions: Long = 0,
)

/**
 * A single datapoint, contains the problem description (n, r, m, k) and
 * a list of operations.
 *
 * @param n the given n value
 * @param r the given r value, defaults to 1
 * @param m the given m value, defaults to n
 * @param k the given k value, defaults to n
 * @param operations list of operations
 */
data class BlasDatapoint(
    val n: Long,
    val r: Long = 1,
    val m: Long = n,
    val k: Long = n,
    val operations: List<Operation>,
) : Datapoint {
    override fun serializeComponentsToJson(): String =
        GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(operations)
}

/**
 * This is a benchmark result for the benchmarks
 * of the BLAS format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param benchmark type of benchmark
 * @param datapoints datapoints for this benchmark
 *
 */
data class BlasBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: BenchmarkType,
    override val datapoints: List<BlasDatapoint>,
) : BenchmarkResult {
    override val summaryValues: Map<String, Double>
            by lazy { calcBandwidths().mapValues { (_, values) -> values.sorted()[values.size / 2] } }

    private fun calcBandwidths(): Map<String, List<Double>> {
        val bandwidths = mutableMapOf<String, MutableList<Double>>()

        for (datapoint in datapoints) {
            for (operation in datapoint.operations) {
                if (!operation.completed) continue
                bandwidths.getOrPut(operation.name) { mutableListOf() }.add(operation.bandwidth)
            }
        }

        return bandwidths
    }
}