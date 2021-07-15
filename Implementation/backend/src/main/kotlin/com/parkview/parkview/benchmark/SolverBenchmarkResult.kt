package com.parkview.parkview.benchmark

import com.google.gson.GsonBuilder
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.git.Commit
import com.parkview.parkview.git.Device

/**
 * A single Solver, part of [SolverBenchmarkResult].
 *
 * @param name
 * @param recurrentResiduals
 * @param trueResiduals
 * @param implicitResiduals
 * @param iterationTimestamps
 * @param rhsNorm
 * @param residualNorm
 * @param completed
 * @param generateComponents
 * @param generateTotalTime
 * @param applyComponents
 * @param applyTotalTime
 * @param applyIterations
 */
data class Solver(
    val name: String,
    val recurrentResiduals: List<Double> = emptyList(),
    val trueResiduals: List<Double> = emptyList(),
    val implicitResiduals: List<Double> = emptyList(),
    val iterationTimestamps: List<Double> = emptyList(),
    val rhsNorm: Double = 0.0,
    val residualNorm: Double = 0.0,
    val completed: Boolean,
    val generateComponents: List<Component>,
    val generateTotalTime: Double,
    val applyComponents: List<Component>,
    val applyTotalTime: Double,
    val applyIterations: Long,
)

/**
 * A single datapoint, contains the problem description for the matrix and
 * a list of solvers.
 *
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 * @param solvers list of [Solver]
 */
data class SolverDatapoint(
    override val rows: Long,
    override val columns: Long,
    override val nonzeros: Long,
    val solvers: List<Solver>
) : MatrixDatapoint {
    override fun serializeComponentsToJson(): String =
        GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(solvers)
}

/**
 * This is a benchmark result for the benchmarks
 * of the Solver format and type.
 *
 * @param commit commit this benchmark has been run on
 * @param device device this benchmark has been run on
 * @param benchmark type of benchmark
 * @param datapoints datapoints for this benchmark
 */
data class SolverBenchmarkResult(
    override val commit: Commit,
    override val device: Device,
    override val benchmark: BenchmarkType,
    override val datapoints: List<SolverDatapoint>
) : MatrixBenchmarkResult {
    override fun getSummaryValue(): Map<String, Double> =
        calcBandwidths().mapValues { (_, values) -> values[values.size / 2] }

    private fun calcBandwidths(): Map<String, List<Double>> {
        val bandwidths = mutableMapOf<String, MutableList<Double>>()

        for (datapoint in datapoints) {
            for (solver in datapoint.solvers) {
                bandwidths.getOrPut(solver.name) { mutableListOf() }.add(solver.applyIterations.toDouble())
            }
        }

        return bandwidths
    }
}