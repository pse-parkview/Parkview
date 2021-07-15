package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.PreconditionerBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlottableData

/**
 * Interface for transforms using multiple [PreconditionerBenchmarkResult].
 */
interface PreconditionerPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>): PlottableData {
        for (result in results) if (result !is PreconditionerBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type")

        return transformPreconditioner(results as List<PreconditionerBenchmarkResult>)
    }

    /**
     * Transforms the benchmark data into a JSON containing the prepared values for plotting
     *
     * @param benchmarkResults list of benchmark results
     * @return a String containing the transformed data in json format
     */
    fun transformPreconditioner(benchmarkResults: List<PreconditionerBenchmarkResult>): PlottableData
}