package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlottableData

/**
 * Interface for transforms using multiple [BlasBenchmarkResult].
 */
interface BlasPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>): PlottableData {
        for (result in results) if (result !is BlasBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type")

        return transformBlas(results as List<BlasBenchmarkResult>)
    }

    /**
     * Transforms the benchmark data into a JSON containing the prepared values for plotting
     *
     * @param benchmarkResults list of benchmark results
     * @return a String containing the transformed data in json format
     */
    fun transformBlas(benchmarkResults: List<BlasBenchmarkResult>): PlottableData
}