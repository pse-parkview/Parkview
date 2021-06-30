package com.parkview.parkview.database

import com.parkview.parkview.git.Benchmark
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device

/**
 * This class offers lazy loading of benchmark result. It loads the
 * benchmark result from the database only once it is actually needed.
 *
 * @param commit chosen commit
 * @param device type of device
 * @param benchmark type of benchmark
 * @param databaseHandler handler for accessing the database
 */
class LazyBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: Benchmark,
    private val databaseHandler: DatabaseHandler
) : BenchmarkResult {
    override fun getSummaryValue(): Map<String, Double> {
        TODO("Not yet implemented")
    }
}