package com.parkview.parkview.benchmark

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkview.parkview.git.*
import java.util.*


private data class ProblemModel(
    val cols: Long = 0,
    val rows: Long = 0,
    val nonzeros: Long = 0,
)

private data class FormatModel(
    val completed: Boolean = false,
    val time: Double = 0.0,
)

private data class SpmvDatapointModel(
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

/**
 * This is a benchmark result for the benchmarks
 * of the SPMV format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param benchmark type of benchmark
 * @param datapoints datapoints for this benchmark
 */
class SpmvBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: Benchmark,
    val datapoints: List<SpmvDatapoint>,
) : BenchmarkResult {
    companion object {
        fun fromJson(json: String): BenchmarkResult {
            val arrayType = object : TypeToken<List<SpmvDatapointModel>>() {}.type

            val datapoints: List<SpmvDatapointModel> = Gson().fromJson(json, arrayType)

            return SpmvBenchmarkResult(
                Commit("ba0c3f47d6095b17ae91f3bb3739b9f0e36f79e5", "", Date(), ""),
                datapoints = datapoints.map { it.toBenchmarkResultDatapoint() }.toList(),
                device = Device("gamer"),
                benchmark = Benchmark("benchmark", BenchmarkType.SpmvBenchmark)
            )
        }
    }

    override fun getSummaryValue(): Map<String, Double> = calcBandwidths().mapValues {(_, values) -> values[values.size / 2]}

    private fun calcBandwidths(): Map<String, List<Double>> {
        val bandwidths = mutableMapOf<String, MutableList<Double>>()

        for (datapoint in datapoints) {
            for (format in datapoint.formats) {
                bandwidths.getOrPut(format.name) { mutableListOf<Double>() }.add(datapoint.nonzeros / format.time)
            }
        }

        return bandwidths
    }
}