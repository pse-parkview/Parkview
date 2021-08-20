package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.MatrixBenchmarkResult
import com.parkview.parkview.benchmark.MatrixDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.PlotOption

/**
 * Most common X axis options for matrix benchmarks
 */
val MATRIX_X_AXIS = CategoricalOption(
    name = "xAxis",
    options = listOf("nonzeros", "rows", "columns")
)

/**
 * Most common x axis options for blas benchmarks
 */
val BLAS_X_AXIS = CategoricalOption(
    name = "xAxis",
    options = listOf("n", "m", "r", "k"),
    description = "Value displayed on x axis",
)

/**
 * Most common y axis options for blas benchmarks
 */
val BLAS_Y_AXIS = CategoricalOption(
    name = "yAxis",
    options = listOf("time", "flops", "bandwidth"),
    description = "Value displayed on y axis"
)

/**
 * Returns a list of available comparisons for a list of benchmark results.
 *
 * @param results list of results
 *
 * @return [PlotOption] containing the available comparisons
 */
fun getAvailableComparisons(results: List<BenchmarkResult>): PlotOption {
    if (results.size != 2) throw InvalidPlotTransformException("Comparison is only possible between two benchmarks")
    return CategoricalOption(
        name = "compare",
        options = listOf(
            results[0].identifier + "/" + results[1].identifier,
            results[1].identifier + "/" + results[0].identifier,
        ),
        description = "Which speedup to compute",
    )
}

/**
 * Filters all matrix datapoints according to the options
 *
 * @param datapoints list of datapoints
 * @param config given configuration
 *
 * @return filtered list of datapoints
 */
fun filterMatrixDatapoints(datapoints: List<MatrixDatapoint>, config: PlotConfiguration): List<MatrixDatapoint> {
    val minRows = config.getNumericalOption("minRows")
    val maxRows = config.getNumericalOption("maxRows")
    val minColumns = config.getNumericalOption("minColumns")
    val maxColumns = config.getNumericalOption("maxColumns")
    val minNonzeros = config.getNumericalOption("minNonzeros")
    val maxNonzeros = config.getNumericalOption("maxNonzeros")

    return datapoints
        .filter { (it.rows >= minRows) and (it.rows <= maxRows) }
        .filter { (it.columns >= minColumns) and (it.columns <= maxColumns) }
        .filter { (it.nonzeros >= minNonzeros) and (it.nonzeros <= maxNonzeros) }
}

/**
 * Returns a option for available matrix names for a given result.
 *
 * @param result given result
 *
 * @return [CategoricalOption] containing all available matrix names
 */
fun getAvailableMatrixNames(result: BenchmarkResult) = CategoricalOption(
    name = "matrix",
    options = (result as MatrixBenchmarkResult).datapoints.map { it.name }
)

fun Operation.getYAxisByOption(config: PlotConfiguration): Double = when (config.getCategoricalOption(BLAS_Y_AXIS)) {
    "time" -> this.time
    "flops" -> this.flops
    "bandwidth" -> this.bandwidth
    else -> throw InvalidPlotConfigValueException(config.getCategoricalOption(BLAS_Y_AXIS), BLAS_Y_AXIS.name)
}

fun BlasDatapoint.getXAxisByConfig(config: PlotConfiguration): Long = when (config.getCategoricalOption(BLAS_X_AXIS)) {
    "n" -> this.n
    "r" -> this.r
    "m" -> this.m
    "k" -> this.k
    else -> throw InvalidPlotConfigValueException(config.getCategoricalOption(BLAS_X_AXIS), BLAS_X_AXIS.name)
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
    val maxN = config.getNumericalOption("maxN")
    val minN = config.getNumericalOption("minN")
    val maxR = config.getNumericalOption("maxR")
    val minR = config.getNumericalOption("minR")
    val maxK = config.getNumericalOption("maxK")
    val minK = config.getNumericalOption("minK")
    val maxM = config.getNumericalOption("maxM")
    val minM = config.getNumericalOption("minM")

    return datapoints
        .filter { (it.n >= minN) and (it.n <= maxN) }
        .filter { (it.r >= minR) and (it.r <= maxR) }
        .filter { (it.m >= minM) and (it.m <= maxM) }
        .filter { (it.k >= minK) and (it.k <= maxK) }
}
