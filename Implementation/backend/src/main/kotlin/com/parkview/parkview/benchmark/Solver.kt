package com.parkview.parkview.benchmark

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
class Solver(
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
    val applyIterations: Int,
)