package com.parkview.parkview.git

/**
 * Contains information about a benchmark type.
 *
 * @param name name of benchmark
 */
data class Benchmark(
    val name: String,
    val type: BenchmarkType
)
