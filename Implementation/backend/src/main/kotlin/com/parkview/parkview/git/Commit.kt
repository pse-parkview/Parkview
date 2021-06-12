package com.parkview.parkview.git

import java.util.Date

data class Commit(
    val sha: String,
    val message: String,
    val date: Date,
    val benchmarkResult: BenchmarkResult
)