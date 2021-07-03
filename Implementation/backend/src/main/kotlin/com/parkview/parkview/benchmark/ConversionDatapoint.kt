package com.parkview.parkview.benchmark


/**
 * A single datapoint, contains the problem description for the matrix and
 * a list of conversions.
 *
 * @param filename filename
 * @param rows number of rows
 * @param columns number of columns
 * @param nonzeros number of nonzeros
 * @param conversions list of conversions
 */
class ConversionDatapoint(rows: Long, columns: Long, nonzeros: Long, val conversions: List<Conversion>) :
    MatrixDatapoint(
        rows, columns, nonzeros
    )