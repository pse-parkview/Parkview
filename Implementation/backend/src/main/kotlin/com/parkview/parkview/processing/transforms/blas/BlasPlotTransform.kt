package com.parkview.parkview.processing.transforms.blas

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.NumericalOption
import com.parkview.parkview.processing.PlotOption
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
    abstract fun transformBlas(benchmarkResults: List<BlasBenchmarkResult>, config: PlotConfiguration): PlottableData
}
