package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.MatrixDatapoint
import com.parkview.parkview.processing.transforms.blas.BlasOptions
import com.parkview.parkview.processing.transforms.matrix.MatrixOptions

/**
 * Filters all matrix datapoints according to the options
 *
 * @param datapoints list of datapoints
 * @param config given configuration
 *
 * @return filtered list of datapoints
 */
fun filterMatrixDatapoints(datapoints: List<MatrixDatapoint>, config: PlotConfiguration): List<MatrixDatapoint> {
    val minRows = config.getNumericalOption(MatrixOptions.minRows)
    val maxRows = config.getNumericalOption(MatrixOptions.maxRows)
    val minColumns = config.getNumericalOption(MatrixOptions.minColumns)
    val maxColumns = config.getNumericalOption(MatrixOptions.maxColumns)
    val minNonzeros = config.getNumericalOption(MatrixOptions.minNonzeros)
    val maxNonzeros = config.getNumericalOption(MatrixOptions.maxNonzeros)

    return datapoints
        .filter { (it.rows >= minRows) and (it.rows <= maxRows) }
        .filter { (it.columns >= minColumns) and (it.columns <= maxColumns) }
        .filter { (it.nonzeros >= minNonzeros) and (it.nonzeros <= maxNonzeros) }
}

/**
 * Filters all blas datapoints according to the options
 *
 * @param datapoints list of datapoints
 * @param config given configuration
 *
 * @return filtered list of datapoints
 */
fun filterBlasDatapoints(datapoints: List<BlasDatapoint>, config: PlotConfiguration): List<BlasDatapoint> {
    val maxN = config.getNumericalOption(BlasOptions.maxN)
    val minN = config.getNumericalOption(BlasOptions.minN)
    val maxR = config.getNumericalOption(BlasOptions.maxR)
    val minR = config.getNumericalOption(BlasOptions.minR)
    val maxK = config.getNumericalOption(BlasOptions.maxK)
    val minK = config.getNumericalOption(BlasOptions.minK)
    val maxM = config.getNumericalOption(BlasOptions.maxM)
    val minM = config.getNumericalOption(BlasOptions.minM)

    return datapoints
        .filter { (it.n >= minN) and (it.n <= maxN) }
        .filter { (it.r >= minR) and (it.r <= maxR) }
        .filter { (it.m >= minM) and (it.m <= maxM) }
        .filter { (it.k >= minK) and (it.k <= maxK) }
}
