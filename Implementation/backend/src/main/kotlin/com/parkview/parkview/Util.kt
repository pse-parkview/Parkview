package com.parkview.parkview

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkview.parkview.benchmark.Format
import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.benchmark.SpmvDatapoint
import com.parkview.parkview.git.*
import java.util.*

data class ProblemModel(
    val cols: Long = 0,
    val rows: Long = 0,
    val nonzeros: Long = 0,
)

data class FormatModel(
    val completed: Boolean = false,
    val time: Double = 0.0,
)

data class SpmvDatapointModel(
    val problem: ProblemModel,
    var spmv: Map<String, FormatModel> = emptyMap(),
) {
    fun toBenchmarkResultDatapoint(): SpmvDatapoint {
        val formats: MutableList<Format> = mutableListOf()

        // spmv can never be null because it is null safe. But somehow gson manages it to still give it a null value
        // if no spmv property exists.
        if (spmv == null) spmv = emptyMap()

        for ((key, value) in spmv) {
            formats += Format(key, value.time, value.completed)
        }
        return SpmvDatapoint(problem.rows, problem.cols, problem.nonzeros, formats)
    }
}

class Util private constructor() {
    companion object {
        fun benchmarkResultFromJson(json: String, type: BenchmarkType): BenchmarkResult = when (type) {
            BenchmarkType.SpmvBenchmark -> spmvFromJson(json)
            BenchmarkType.SolverBenchmark -> TODO()
            BenchmarkType.PreconditionerBenchmark -> TODO()
            BenchmarkType.ConversionBenchmark -> TODO()
            BenchmarkType.BlasBenchmark -> TODO()
        }

        private fun spmvFromJson(json: String): BenchmarkResult {
            val gson = Gson()
            val arrayType = object : TypeToken<List<SpmvDatapointModel>>() {}.type

            val datapoints: List<SpmvDatapointModel> = gson.fromJson(json, arrayType)

            return SpmvBenchmarkResult(
                Commit("", "", Date(), ""),
                datapoints = datapoints.map { it.toBenchmarkResultDatapoint() }.toList(),
                device = Device(""),
                benchmark = Benchmark("", BenchmarkType.SpmvBenchmark)
            )

        }
    }
}