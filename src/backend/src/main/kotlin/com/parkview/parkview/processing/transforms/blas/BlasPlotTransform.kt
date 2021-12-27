package com.parkview.parkview.processing.transforms.blas

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.transforms.InvalidPlotConfigValueException
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlotTransform
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.filterBlasDatapoints

/**
 * Interface for transforms using [BlasBenchmarkResult].
 */
abstract class BlasPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, config: PlotConfiguration): PlottableData {
        for (result in results) if (result !is BlasBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only BlasBenchmarkResult is allowed")

        checkNumInputs(results.size)

        val filteredResults = results.filterIsInstance<BlasBenchmarkResult>().map {
            BlasBenchmarkResult(
                commit = it.commit,
                device = it.device,
                datapoints = filterBlasDatapoints(it.datapoints, config),
            )
        }

        return transformBlas(filteredResults, config)
    }

    final override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> =
        getBlasPlotOptions(results) + listOf(
            BlasOptions.minN.realizeOption(results),
            BlasOptions.maxN.realizeOption(results),
            BlasOptions.minR.realizeOption(results),
            BlasOptions.maxR.realizeOption(results),
            BlasOptions.minM.realizeOption(results),
            BlasOptions.maxM.realizeOption(results),
            BlasOptions.minK.realizeOption(results),
            BlasOptions.maxK.realizeOption(results),
        )

    abstract fun getBlasPlotOptions(results: List<BenchmarkResult>): List<PlotOption>

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of blas benchmark results
     * @return [PlottableData] object containing the data
     */
    abstract fun transformBlas(benchmarkResults: List<BlasBenchmarkResult>, config: PlotConfiguration): PlottableData

    protected fun Operation.getYAxisByOption(config: PlotConfiguration): Double =
        when (config.getCategoricalOption(BlasOptions.yAxis)) {
            "time" -> this.time
            "flops" -> this.flops
            "bandwidth" -> this.bandwidth
            else -> throw InvalidPlotConfigValueException(
                config.getCategoricalOption(BlasOptions.yAxis),
                BlasOptions.yAxis.name
            )
        }

    protected fun BlasDatapoint.getXAxisByConfig(config: PlotConfiguration): Long =
        when (config.getCategoricalOption(BlasOptions.xAxis)) {
            "n" -> this.n
            "r" -> this.r
            "m" -> this.m
            "k" -> this.k
            else -> throw InvalidPlotConfigValueException(
                config.getCategoricalOption(BlasOptions.xAxis),
                BlasOptions.xAxis.name
            )
        }
}
