package com.parkview.parkview.git

import java.util.Date

/**
 * Class that represents a single commit for a given branch, device, and benchmark type
 *
 * @param sha commit sha
 * @param message commit message
 * @param date commit date
 * @param benchmarkResult benchmark result for this commit
 */
data class Commit(
    val sha: String,
    val message: String,
    val date: Date,
    var benchmarkResult: BenchmarkResult = EmptyBenchmarkResult()
)