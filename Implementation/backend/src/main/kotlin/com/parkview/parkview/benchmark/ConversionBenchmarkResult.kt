package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device

/**
 * This is a benchmark result for the benchmarks
 * of the Conversion format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param benchmark type of benchmark
 * @param datapoints datapoints for this benchmark
 */
class ConversionBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: Benchmark,
    /**
     * Contains the datapoints for this benchmark
     */
    val datapoints: List<ConversionDatapoint>,
) : BenchmarkResult {
    override fun getSummaryValue(): Double {
        val bandwidths = calcBandwidths()
        return bandwidths[bandwidths.size / 2]
    }

    private fun calcBandwidths()  = datapoints.fold(emptyList<Double>()) {
                total, problem -> total + problem.conversions.map {
            // TODO: what is meant with some_number in benchmark.pdf
            problem.nonzeros / it.time
                }
        }
}