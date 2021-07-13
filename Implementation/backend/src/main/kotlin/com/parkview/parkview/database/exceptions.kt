package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkResult

/**
 * Exception for handling missing benchmark results.
 *
 * @param benchmarkResult benchmark result that is missing
 */
class MissingBenchmarkResultException(benchmarkResult: BenchmarkResult) : Exception(
    "Error, the benchmark result for ${benchmarkResult.benchmark.name} on " +
            "commit ${benchmarkResult.commit.sha} using device ${benchmarkResult.device.name} could not be found."
)
