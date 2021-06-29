package com.parkview.parkview.benchmark

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device

/**
 * This is a benchmark result for the benchmarks
 * of the Preconditioner format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param benchmark type of benchmark
 * @param datapoints datapoints for this benchmark
 */
class PreconditionerBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: Benchmark,
    val datapoints: List<PreconditionerDatapoint>,
) : BenchmarkResult {
    override fun getSummaryValue(): Double {
        TODO("Not yet implemented")
    }
}