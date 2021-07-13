package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device


/**
 * A single conversion, part of [ConversionBenchmarkResult]
 *
 * @param name conversion name
 * @param time time for conversion
 * @param completed whether or not the conversion completed
 */
data class Conversion(
    val name: String,
    val time: Double,
    val completed: Boolean,
)

/**
 * A single datapoint, contains the problem description for the matrix and
 * a list of conversions.
 *
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 * @param conversions list of conversions
 */
data class ConversionDatapoint(
    val rows: Long,
    val columns: Long,
    val nonzeros: Long,
    val conversions: List<Conversion>
)

/**
 * This is a benchmark result for the benchmarks
 * of the Conversion format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param benchmark type of benchmark
 * @param datapoints datapoints for this benchmark
 */
data class ConversionBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: Benchmark,
    val datapoints: List<ConversionDatapoint>,
) : BenchmarkResult {
    override fun getSummaryValue(): Map<String, Double> =
        calcBandwidths().mapValues { (_, values) -> values[values.size / 2] }

    private fun calcBandwidths(): Map<String, List<Double>> {
        val bandwidths = mutableMapOf<String, MutableList<Double>>()

        for (datapoint in datapoints) {
            for (conversion in datapoint.conversions) {
                bandwidths.getOrPut(conversion.name) { mutableListOf<Double>() }
                    .add(datapoint.nonzeros / conversion.time)
            }
        }

        return bandwidths
    }
}