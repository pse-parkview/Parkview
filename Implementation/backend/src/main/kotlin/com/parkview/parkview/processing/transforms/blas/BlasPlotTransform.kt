package com.parkview.parkview.processing.transforms.blas

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.transforms.InvalidPlotOptionsException
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException
import com.parkview.parkview.processing.transforms.PlotTransform
import com.parkview.parkview.processing.transforms.PlottableData

val BLAS_X_AXIS = PlotOption(
    name = "xAxis",
    options = listOf("n", "m", "r", "k")
)

val BLAS_Y_AXIS = PlotOption(
    name = "yAxis",
    options = listOf("time", "flops", "bandwidth")
)

/**
 * Interface for transforms using [BlasBenchmarkResult].
 */
interface BlasPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData {
        for (result in results) if (result !is BlasBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only BlasBenchmarkResult is allowed")

        checkNumInputs(results)
        checkOptions(results, options)

        return transformBlas(results.filterIsInstance<BlasBenchmarkResult>(), options)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of blas benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transformBlas(benchmarkResults: List<BlasBenchmarkResult>, options: Map<String, String>): PlottableData
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
