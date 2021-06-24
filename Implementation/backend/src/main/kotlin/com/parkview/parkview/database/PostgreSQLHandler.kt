package com.parkview.parkview.database

import com.parkview.parkview.git.*

/**
 * [DatabaseHandler] for accessing a PostgreSQL database.
 */
class PostgreSQLHandler : DatabaseHandler {
    override fun updateCommits(commits: List<Commit>) {
        TODO("Not yet implemented")
    }

    override fun updateBenchmarkResults(results: List<BenchmarkResult>) {
        TODO("Not yet implemented")
    }

    override fun fetchBranch(branch: String, benchmark: Benchmark): BranchForBenchmark {
        TODO("Not yet implemented")
    }

    override fun fetchBenchmarkResult(commit: Commit, device: Device, benchmark: Benchmark): BenchmarkResult {
        TODO("Not yet implemented")
    }
}