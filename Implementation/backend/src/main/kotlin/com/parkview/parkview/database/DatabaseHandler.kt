package com.parkview.parkview.database

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Commit

interface DatabaseHandler {
    fun updateCommits(commits: List<Commit>)
    fun updateBenchmarkResults(results: List<BenchmarkResult>)
}