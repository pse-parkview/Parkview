package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.MatrixBenchmarkResult
import com.parkview.parkview.benchmark.MatrixDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.benchmark.Solver
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
 * Get a options value by its name.
 *
 * @param name name of option
 *
 * @return value of wanted option
 *
 * @throws InvalidPlotOptionNameException if the option doesn't exist
 */
fun Map<String, String>.getOptionValueByName(name: String) = this[name] ?: throw InvalidPlotOptionNameException(name)

/**
 * Filters all matrix datapoints according to the options
 *
 * @param datapoints list of datapoints
 * @param options given options
 *
 * @return filtered list of datapoints
 */
fun filterMatrixDatapoints(datapoints: List<MatrixDatapoint>, options: Map<String, String>): List<MatrixDatapoint> {
    val minRows = options.getOptionValueByName("minRows").toDouble()
    val maxRows = options.getOptionValueByName("maxRows").toDouble()
    val minColumns = options.getOptionValueByName("minColumns").toDouble()
    val maxColumns = options.getOptionValueByName("maxColumns").toDouble()
    val minNonzeros = options.getOptionValueByName("minNonzeros").toDouble()
    val maxNonzeros = options.getOptionValueByName("maxNonzeros").toDouble()

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

fun Solver.getComponentsByOption(options: Map<String, String>) = when (options["components"]) {
    "apply" -> this.applyComponents
    "generate" -> this.generateComponents
    else -> throw InvalidPlotOptionValueException(options, "components")
}

fun Solver.getTotalTimeByOption(options: Map<String, String>) = when (options["components"]) {
    "apply" -> this.getApplyTotalTimeByOption(options)
    "generate" -> this.getGenerateTotalTimeByOption(options)
    else -> throw InvalidPlotOptionValueException(options, "components")
}

fun Solver.getGenerateTotalTimeByOption(options: Map<String, String>) = when (options["totalTime"]) {
    "sumComponents" -> this.generateComponents.sumOf { it.runtime }
    "givenValue" -> this.generateTotalTime
    else -> throw InvalidPlotOptionValueException(options, "totalTime")
}

fun Solver.getApplyTotalTimeByOption(options: Map<String, String>) = when (options["totalTime"]) {
    "sumComponents" -> this.applyComponents.sumOf { it.runtime }
    "givenValue" -> this.applyTotalTime
    else -> throw InvalidPlotOptionValueException(options, "totalTime")
}

fun Operation.getYAxisByOption(options: Map<String, String>): Double = when (options["yAxis"]) {
    "time" -> this.time
    "flops" -> this.flops
    "bandwidth" -> this.bandwidth
    else -> throw InvalidPlotOptionValueException(options, "yAxis")
}

fun BlasDatapoint.getXAxisByOption(options: Map<String, String>): Long = when (options["xAxis"]) {
    "n" -> this.n
    "r" -> this.r
    "m" -> this.m
    "k" -> this.k
    else -> throw InvalidPlotOptionValueException(options, "xAxis")
}

/**
 * Filters all blas datapoints according to the options
 *
 * @param datapoints list of datapoints
 * @param options given options
 *
 * @return filtered list of datapoints
 */
fun filterBlasDatapoints(datapoints: List<BlasDatapoint>, options: Map<String, String>): List<BlasDatapoint> {
    val maxN = options.getOptionValueByName("maxN").toDouble()
    val minN = options.getOptionValueByName("minN").toDouble()
    val maxR = options.getOptionValueByName("maxR").toDouble()
    val minR = options.getOptionValueByName("minR").toDouble()
    val maxK = options.getOptionValueByName("maxK").toDouble()
    val minK = options.getOptionValueByName("minK").toDouble()
    val maxM = options.getOptionValueByName("maxM").toDouble()
    val minM = options.getOptionValueByName("minM").toDouble()

    return datapoints
        .filter { (it.n >= minN) and (it.n <= maxN) }
        .filter { (it.r >= minR) and (it.r <= maxR) }
        .filter { (it.m >= minM) and (it.m <= maxM) }
        .filter { (it.k >= minK) and (it.k <= maxK) }
}
