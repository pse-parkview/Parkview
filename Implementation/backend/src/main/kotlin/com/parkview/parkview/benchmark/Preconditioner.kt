package com.parkview.parkview.benchmark

/**
 * A single preconditioner, part of [PreconditionerBenchmarkResult].
 *
 * @param name preconditioner name
 * @param generateComponents
 * @param generateTime
 * @param applyComponents
 * @param applyTime
 * @param completed
 */
class Preconditioner(
    val name: String,
    val generateComponents: List<Component>,
    val generateTime: Double,
    val applyComponents: List<Component>,
    val applyTime: Double,
    val completed: Boolean,
)
