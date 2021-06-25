package com.parkview.parkview.benchmark

/**
 * Single datapoint for a [MatrixBenchmarkResult]. Contains
 * a problem description (rows, columns, nonzeros).
 *
 * @param filename
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 */
abstract class MatrixDatapoint(
    val filename: String,
    val rows: Int,
    val columns: Int,
    val nonzeros: Int,
)
