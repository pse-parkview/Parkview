package com.parkview.parkview.benchmark

/**
 * A single datapoint, contains the problem description for the matrix and
 * a list of formats.
 *
 * @param filename filename
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 * @param formats list of [Format]
 * @param optimal optimal format
 */
class SpmvDatapoint(
    filename: String, rows: Int, columns: Int, nonzeros: Int,
    val formats: List<Format>,
    val optimal: Format,
) : MatrixDatapoint(
    filename, rows, columns,
    nonzeros,
)