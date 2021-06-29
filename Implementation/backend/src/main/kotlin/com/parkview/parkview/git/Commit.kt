package com.parkview.parkview.git

import java.util.*

/**
 * Class that represents a single commit for a given branch and benchmark type
 *
 * @param sha commit sha
 * @param message commit message
 * @param date commit date
 */
class Commit(
    val sha: String,
    val message: String,
    val date: Date,
) {
    private val benchmarkResults: MutableMap<Device, BenchmarkResult> = mutableMapOf()

    val benchmarkResultsByDevice get() = benchmarkResults.toMap()

    fun addBenchmarkResult(result: BenchmarkResult) {
        benchmarkResults[result.device] = result
    }
}
