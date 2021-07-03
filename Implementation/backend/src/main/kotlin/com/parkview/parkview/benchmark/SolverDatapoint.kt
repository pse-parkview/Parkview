package com.parkview.parkview.benchmark

/**
 * A single datapoint, contains the problem description for the matrix and
 * a list of solvers.
 *
 * @param filename filename
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 * @param solvers list of [Solver]
 */
class SolverDatapoint(rows: Long, columns: Long, nonzeros: Long, val solvers: List<Solver>) :
    MatrixDatapoint(
        rows, columns,
        nonzeros
    )