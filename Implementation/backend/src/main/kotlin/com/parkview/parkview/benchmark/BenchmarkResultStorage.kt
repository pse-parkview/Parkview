package com.parkview.parkview.benchmark

import com.parkview.parkview.git.BenchmarkResult

/**
 * This interface provides methods for the storage of benchmark results.
 */
interface BenchmarkResultStorage {
    /**
     * Stores the given benchmark results in the database. If the
     * benchmark result already exists, it gets replaced.
     *
     * @param results list of results that will be stored
     */
    fun storeBenchmarkResults(results: List<BenchmarkResult>)
}