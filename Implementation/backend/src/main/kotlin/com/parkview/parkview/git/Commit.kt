package com.parkview.parkview.git

import java.util.*

/**
 * Class that represents a single commit for a given branch and benchmark type
 *
 * @param sha commit sha
 * @param message commit message
 * @param date commit date
 * @param parentCommit parent commit
 * @param benchmarkResultsByDevice a map from device names to benchmark results
 */
class Commit(
    val sha: String,
    val message: String,
    val date: Date,
    val parentCommit: Commit? = null, // TODO replace with null object, only problem is the initial commit
    val benchmarkResultsByDevice: Map<Device, BenchmarkResult> = emptyMap()
)