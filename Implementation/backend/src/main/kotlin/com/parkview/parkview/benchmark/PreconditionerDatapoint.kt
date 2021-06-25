package com.parkview.parkview.benchmark

/**
 * A single datapoint, contains the problem description for the matrix and
 * a list of preconditioners.
 *
 * @param filename filename
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 * @param preconditioners list of [Preconditioner]
 */
class PreconditionerDatapoint(
    filename: String,
    rows: Int, columns: Int, nonzeros: Int, val preconditioners: List<Preconditioner>
) : MatrixDatapoint(filename, rows, columns, nonzeros)