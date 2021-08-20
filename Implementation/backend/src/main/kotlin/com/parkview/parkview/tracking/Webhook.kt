package com.parkview.parkview.tracking

import com.parkview.parkview.git.BenchmarkResult

/**
 * Interface for webhooks used by [PerformanceTracker].
 */
interface Webhook {
    /**
     * Adds a benchmark result for a report.
     *
     * @param new new [BenchmarkResult]
     * @param previous previous [BenchmarkResult] with available data
     */
    fun addResult(new: BenchmarkResult, previous: BenchmarkResult)

    /**
     * Notifies the webhook with the collected report.
     */
    fun notifyHook()
}
