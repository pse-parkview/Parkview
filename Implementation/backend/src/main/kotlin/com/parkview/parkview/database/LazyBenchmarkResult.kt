package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit

/**
 * This class offers lazy loading of benchmark result. It loads the
 * benchmark result from the database only once it is actually needed.
 */
class LazyBenchmarkResult(commit: Commit, device: String) : BenchmarkResult {
    override fun toJson(): String {
        TODO("Not yet implemented")
    }

    override fun getSummaryValue(): Double {
        TODO("Not yet implemented")
    }
}