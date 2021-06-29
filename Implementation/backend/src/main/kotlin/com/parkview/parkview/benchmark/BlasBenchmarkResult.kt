package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device

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
class BlasBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: Benchmark,
    /**
     * Contains the datapoints for this benchmark
     */
    val datapoints: List<BlasDatapoint>
) : BenchmarkResult {

    override fun getSummaryValue(): Double {
        val bandwidths = getBandwidths()
        return bandwidths[bandwidths.size / 2]
    }

    private fun getBandwidths() =
        datapoints.fold(emptyList<Double>()) { total, problem -> total + problem.operations.map { it.bandwidth } }
}