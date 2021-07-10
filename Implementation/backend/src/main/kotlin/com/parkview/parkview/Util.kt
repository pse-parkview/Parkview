package com.parkview.parkview

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.*

class Util private constructor() {
    companion object {
        fun benchmarkResultFromJson(json: String, type: BenchmarkType): BenchmarkResult = when (type) {
            BenchmarkType.SpmvBenchmark -> SpmvBenchmarkResult.fromJson(json)
            BenchmarkType.SolverBenchmark -> TODO()
            BenchmarkType.PreconditionerBenchmark -> TODO()
            BenchmarkType.ConversionBenchmark -> TODO()
            BenchmarkType.BlasBenchmark -> TODO()
        }
    }
}