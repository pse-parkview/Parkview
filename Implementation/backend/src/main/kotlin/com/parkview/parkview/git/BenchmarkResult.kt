package com.parkview.parkview.git

/**
 * Interface for representing a single benchmark result for a given benchmark type.
 */
interface BenchmarkResult {
    /**
     * Commit used for this benchmark
     */
    val commit: Commit

    /**
     * Device used for this benchmark
     */
    val device: Device

    /**
     * name of benchmark
     */
    val benchmark: Benchmark

    /**
     * Returns a summary value for this benchmark result
     *
     * @return summary value
     */
    fun getSummaryValue(): Double
}