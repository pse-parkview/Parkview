package com.parkview.parkview.processing

import com.parkview.parkview.git.BenchmarkResult

/**
 * This interface provides methods for the storage of benchmark results
 */
interface BenchmarkResultStorage {
    /**
     * Stores the given benchmark results in the database
     *
     * @param results results that will be stored
     */
    fun storeBenchmarkResults(results: List<BenchmarkResult>)
}