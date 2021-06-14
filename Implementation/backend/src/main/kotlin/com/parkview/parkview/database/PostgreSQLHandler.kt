package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Branch
import com.parkview.parkview.git.Commit

/**
 * [DatabaseHandler] for accessing a PostgreSQL database
 */
class PostgreSQLHandler: DatabaseHandler {
    override fun updateCommits(commits: List<Commit>) {
        TODO("Not yet implemented")
    }

    override fun updateBenchmarkResults(results: List<BenchmarkResult>) {
        TODO("Not yet implemented")
    }

    override fun fetchBranch(branch: String, benchmark: String): Branch {
        TODO("Not yet implemented")
    }
}