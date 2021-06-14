package com.parkview.parkview.processing

import com.parkview.parkview.git.BenchmarkResult

/**
 * This interface provides methods for converting from raw data to usable data
 */
interface RawBenchmarkResult {
    /**
     * Processes and formats the raw data
     *
     * @return [BenchmarkResult] object containing the processed data
     */
    fun process(): BenchmarkResult
}