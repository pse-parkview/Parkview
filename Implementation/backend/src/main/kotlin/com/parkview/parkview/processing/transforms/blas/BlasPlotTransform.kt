package com.parkview.parkview.processing.transforms.blas

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.NumericalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.transforms.*

val BLAS_X_AXIS = CategoricalOption(
    name = "xAxis",
    options = listOf("n", "m", "r", "k")
)

val BLAS_Y_AXIS = CategoricalOption(
    name = "yAxis",
    options = listOf("time", "flops", "bandwidth")
)

/**
 * Interface for transforms using [BlasBenchmarkResult].
 */
abstract class BlasPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData {
        for (result in results) if (result !is BlasBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only BlasBenchmarkResult is allowed")

        checkNumInputs(results)
        checkOptions(results, options)

        val filteredResults = results.filterIsInstance<BlasBenchmarkResult>().map {
            BlasBenchmarkResult(
                commit = it.commit,
                device = it.device,
                benchmark = it.benchmark,
                datapoints = filterBlasDatapoints(it.datapoints, options),
            )
        }

        return transformBlas(filteredResults, options)
    }

    final override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> {
        val datapoints = results.asSequence().filterIsInstance<BlasBenchmarkResult>().map { it.datapoints }.flatten()
        return getBlasPlotOptions(results) + listOf<PlotOption>(
            NumericalOption(
                name = "minN",
                description = "Lower limit for n",
                default = datapoints.map { it.n }.minOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "maxN",
                description = "Upper limit for n",
                default = datapoints.map { it.n }.maxOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "minR",
                description = "Lower limit for r",
                default = datapoints.map { it.r }.minOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "maxR",
                description = "Upper limit for r",
                default = datapoints.map { it.r }.maxOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "minM",
                description = "Lower limit for m",
                default = datapoints.map { it.m }.minOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "maxM",
                description = "Upper limit for m",
                default = datapoints.map { it.m }.maxOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "minK",
                description = "Lower limit for k",
                default = datapoints.map { it.k }.minOrNull()?.toDouble() ?: 0.0,
            ),
            NumericalOption(
                name = "maxK",
                description = "Upper limit for k",
                default = datapoints.map { it.k }.maxOrNull()?.toDouble() ?: 0.0,
            ),
        )
    }

    abstract fun getBlasPlotOptions(results: List<BenchmarkResult>): List<PlotOption>

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of blas benchmark results
     * @return [PlottableData] object containing the data
     */
    abstract fun transformBlas(benchmarkResults: List<BlasBenchmarkResult>, options: Map<String, String>): PlottableData
}

fun Operation.getYAxisByOption(options: Map<String, String>): Double = when (options["yAxis"]) {
    "time" -> this.time
    "flops" -> this.flops
    "bandwidth" -> this.bandwidth
    else -> throw InvalidPlotOptionsException(options, "yAxis")
}

fun BlasDatapoint.getXAxisByOption(options: Map<String, String>): Long = when (options["xAxis"]) {
    "n" -> this.n
    "r" -> this.r
    "m" -> this.m
    "k" -> this.k
    else -> throw InvalidPlotOptionsException(options, "xAxis")
}

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