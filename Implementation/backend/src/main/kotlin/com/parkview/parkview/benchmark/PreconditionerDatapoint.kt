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
    rows: Long, columns: Long, nonzeros: Long, val preconditioners: List<Preconditioner>
) : MatrixDatapoint(rows, columns, nonzeros)