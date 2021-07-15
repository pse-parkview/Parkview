package com.parkview.parkview.benchmark

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.git.Datapoint

interface MatrixDatapoint : Datapoint {
    val rows: Long
    val columns: Long
    val nonzeros: Long
}

interface MatrixBenchmarkResult : BenchmarkResult {
    override val datapoints: List<MatrixDatapoint>
}