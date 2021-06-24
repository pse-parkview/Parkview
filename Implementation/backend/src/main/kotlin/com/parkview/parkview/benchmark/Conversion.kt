package com.parkview.parkview.benchmark

/**
 * A single conversion, part of [ConversionBenchmarkResult]
 *
 * @param name conversion name
 * @param time time for conversion
 * @param completed whether or not the conversion completed
 */
class Conversion(
    val name: String,
    val time: Double,
    val completed: Boolean,
)
