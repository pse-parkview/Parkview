package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device
import java.util.*

class CachingDatabaseHandler(
    private val databaseHandler: DatabaseHandler,
    private val maxCached: Int = 10,
) : DatabaseHandler {
    private val resultCache: MutableList<BenchmarkResult> = mutableListOf()

    override fun insertBenchmarkResults(results: List<BenchmarkResult>) {
        resultCache.clear()
        databaseHandler.insertBenchmarkResults(results)
    }

    override fun fetchBenchmarkResult(commit: Commit, device: Device, benchmark: BenchmarkType): BenchmarkResult {
        val cached =
            resultCache.find { (it.commit.sha == commit.sha) and (it.device == device) && (it.benchmark == benchmark) }

        // miss
        if (cached == null) {
            val result = databaseHandler.fetchBenchmarkResult(commit, device, benchmark)
            addToCache(result)
            return result
        }

        // hit
        // increase priority
        val i = resultCache.indexOf(cached)
        Collections.swap(resultCache, i, resultCache.size - 1)
        return cached
    }

    override fun hasDataAvailable(commit: Commit, device: Device, benchmark: BenchmarkType): Boolean =
        databaseHandler.hasDataAvailable(commit, device, benchmark)

    override fun getAvailableDevicesForCommit(commit: Commit, benchmark: BenchmarkType): List<Device> =
        databaseHandler.getAvailableDevicesForCommit(commit, benchmark)

    private fun addToCache(result: BenchmarkResult) {
        resultCache.add(result)

        if (resultCache.size > maxCached) {
            resultCache.removeAt(0)
        }
    }


}