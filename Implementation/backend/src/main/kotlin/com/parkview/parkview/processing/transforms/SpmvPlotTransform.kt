package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlottableData

/**
 * Interface for transforms using multiple [SpmvBenchmarkResult].
 */
interface SpmvPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>): PlottableData {
        for (result in results) if (result !is SpmvBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type")

        return transformSpmv(results as List<SpmvBenchmarkResult>)
    }

    /**
     * Transforms the benchmark data into a JSON containing the prepared values for plotting
     *
     * @param benchmarkResults list of benchmark results
     * @return a String containing the transformed data in json format
     */
    fun transformSpmv(benchmarkResults: List<SpmvBenchmarkResult>): PlottableData
}