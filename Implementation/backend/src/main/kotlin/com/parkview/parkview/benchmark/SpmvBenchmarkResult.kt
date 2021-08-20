package com.parkview.parkview.benchmark

import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device

/**
 * A single format, part of [SpmvBenchmarkResult].
 *
 * @param name format name
 * @param storage
 * @param time
 * @param maxRelativeNorm2
 * @param completed
 */
data class Format(
    val name: String,
    val time: Double,
    val completed: Boolean,
    val storage: Long = 0,
    val maxRelativeNorm2: Double = 0.0,
)

/**
 * A single datapoint, contains the problem description for the matrix and
 * a list of formats.
 *
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 * @param formats list of [Format]
 */
data class SpmvDatapoint(
    override val name: String,
    override val rows: Long,
    override val columns: Long,
    override val nonzeros: Long,
    val formats: List<Format>,
) : MatrixDatapoint

/**
 * This is a benchmark result for the benchmarks
 * of the SPMV format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param datapoints datapoints for this benchmark
 */
data class SpmvBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val datapoints: List<SpmvDatapoint>,
) : MatrixBenchmarkResult {
    override val benchmark: BenchmarkType = BenchmarkType.Spmv

    override val summaryValues: Map<String, Double>
        by lazy { calcBandwidths().mapValues { (_, values) -> values.sorted()[values.size / 2] } }

    private fun calcBandwidths(): Map<String, List<Double>> {
        val bandwidths = mutableMapOf<String, MutableList<Double>>()

        for (datapoint in datapoints) {
            for (format in datapoint.formats) {
                if (!format.completed) continue
                bandwidths.getOrPut(format.name) { mutableListOf() }.add(datapoint.nonzeros / format.time)
            }
        }

        return bandwidths
    }
}
