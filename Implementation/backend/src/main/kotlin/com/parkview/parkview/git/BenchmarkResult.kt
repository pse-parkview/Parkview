package com.parkview.parkview.git

/**
 * Single datapoint for a benchmark result
 */
interface Datapoint

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
    val benchmark: BenchmarkType

    /**
     * datapoints of benchmark
     */
    val datapoints: List<Datapoint>

    /**
     * Returns a summary value for this benchmark result
     *
     * @return summary value
     */
    val summaryValues: Map<String, Double>

    val identifier: String
        get() = commit.sha.substring(0, 7) + "-" + device.name
}