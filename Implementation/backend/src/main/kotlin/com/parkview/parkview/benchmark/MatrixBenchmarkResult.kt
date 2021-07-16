package com.parkview.parkview.benchmark

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Datapoint

/**
 * Specific datapoint for matrix benchmark, contains information about the problem setup
 */
interface MatrixDatapoint : Datapoint {
    val rows: Long
    val columns: Long
    val nonzeros: Long
}

/**
 * Interface for benchmarks using a matrix as a problem description
 */
interface MatrixBenchmarkResult : BenchmarkResult {
    override val datapoints: List<MatrixDatapoint>
}