package com.parkview.parkview.benchmark

/**
 * A single operation, part of [BlasBenchmarkResult].
 *
 * @param name
 * @param time
 * @param flops
 * @param bandwidth
 * @param completed
 */
class Operation(
    val name: String,
    val time: Double,
    val flops: Double,
    val bandwidth: Double,
    val completed: Boolean,
    val repetitions: Long = 0,
)