package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device

/**
 * This is a benchmark result for the benchmarks
 * of the Solver format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param benchmark type of benchmark
 * @param datapoints datapoints for this benchmark
 */
class SolverBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: Benchmark,
    val datapoints: List<SolverDatapoint>
) : BenchmarkResult {
    override fun getSummaryValue(): Map<String, Double> =
        calcBandwidths().mapValues { (_, values) -> values[values.size / 2] }

    private fun calcBandwidths(): Map<String, List<Double>> {
        val bandwidths = mutableMapOf<String, MutableList<Double>>()

        for (datapoint in datapoints) {
            for (solver in datapoint.solvers) {
                bandwidths.getOrPut(solver.name) { mutableListOf<Double>() }.add(solver.applyIterations.toDouble())
            }
        }

        return bandwidths
    }
}