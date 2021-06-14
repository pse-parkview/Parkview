package com.parkview.parkview.git

import java.util.Date

/**
 * Class that represents a single commit for a given branch, device, and benchmark type
 *
 * @param sha commit sha
 * @param message commit message
 * @param date commit date
 * @param previousCommit parent commit
 * @param benchmarkResultsByDevice a map from device names to benchmark results
 */
class Commit(
    val sha: String,
    val message: String,
    val date: Date,
    val previousCommit: Commit,
    val benchmarkResultsByDevice: Map<String, BenchmarkResult> = emptyMap()
)