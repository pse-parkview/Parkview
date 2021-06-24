package com.parkview.parkview.benchmark

import com.parkview.parkview.git.BenchmarkResult

/**
 * Benchmark type that uses matrices.
 *
 * @param datapoints list of datapoints for this benchmark
 */
abstract class MatrixBenchmarkResult(
    val datapoints: List<MatrixDatapoint>
) : BenchmarkResult