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
class Format(
    val name: String,
    val storage: Int,
    val time: Double,
    val maxRelativeNorm2: Double,
    val completed: Boolean,
)
