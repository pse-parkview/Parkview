package com.parkview.parkview.benchmark

import com.parkview.parkview.git.BenchmarkResult

interface BenchmarkJsonParser {
    /**
     * Converts a json list of objects to a list of benchmark results
     *
     * @param sha sha for commit these benchmarks have been run on
     * @param deviceName device these benchmarks have been run on
     * @param json json as a string
     *
     * @return list of [BenchmarkResult]
     */
    fun benchmarkResultsFromJson(
        sha: String,
        deviceName: String,
        json: String,
    ): List<BenchmarkResult>
}
