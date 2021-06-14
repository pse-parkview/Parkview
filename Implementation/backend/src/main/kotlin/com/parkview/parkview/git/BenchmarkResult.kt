package com.parkview.parkview.git

/**
 * Class for representing a single benchmark result for a given benchmark type
 */
interface BenchmarkResult {
    /**
     * Commit used for this benchmark
     */
    val commit: Commit

    /**
     * Device used for this benchmark
     */
    val device: String

    /**
     * name of benchmark
     */
    val benchmark: String

    /**
     * Serializes the benchmark result using the json format
     *
     * @return json representation as string
     */
    fun toJson(): String

    /**
     * Returns a summary value for this benchmark result
     *
     * @return summary value
     */
    fun getSummaryValue(): Double
}