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
    override fun getSummaryValue(): Double {
        TODO("Not yet implemented")
    }
}