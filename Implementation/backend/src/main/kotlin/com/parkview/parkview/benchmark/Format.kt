package com.parkview.parkview.benchmark

/**
 * A single format, part of [SpmvBenchmarkResult].
 *
 * TODO don't know what any of this is
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
    val storage: Int = 0,
    val maxRelativeNorm2: Double = 0.0,
)
