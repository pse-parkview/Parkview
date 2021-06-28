package com.parkview.parkview.git

/**
 * Null object for representing an empty or non existing benchmark result
 */
class EmptyBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: Benchmark
) : BenchmarkResult {
    override fun getSummaryValue() = 0.0
}