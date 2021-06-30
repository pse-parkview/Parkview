package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device

/**
 * This is a benchmark result for the benchmarks
 * of the SPMV format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param benchmark type of benchmark
 * @param datapoints datapoints for this benchmark
 */
class SpmvBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: Benchmark,
    val datapoints: List<SpmvDatapoint>,
) : BenchmarkResult {
    override fun getSummaryValue(): Map<String, Double> = calcBandwidths().mapValues {(key, values) -> values[values.size / 2]}

    private fun calcBandwidths(): Map<String, List<Double>> {
        val bandwidths = mutableMapOf<String, MutableList<Double>>()

        for (datapoint in datapoints) {
            for (format in datapoint.formats) {
                bandwidths.getOrPut(format.name) { mutableListOf<Double>() }.add(datapoint.nonzeros / format.time)
            }
        }

        return bandwidths
    }
}