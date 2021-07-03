package com.parkview.parkview.benchmark

/**
 * Single datapoint for a benchmark using matrices. Contains
 * a problem description (rows, columns, nonzeros).
 *
 * @param filename
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 */
abstract class MatrixDatapoint(
    val rows: Long,
    val columns: Long,
    val nonzeros: Long,
)
