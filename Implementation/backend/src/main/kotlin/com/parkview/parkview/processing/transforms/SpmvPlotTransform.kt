package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult

/**
 * Interface for transforms using [SpmvBenchmarkResult].
 */
interface SpmvPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData {
        for (result in results) if (result !is SpmvBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only SpmvBenchmarkResult is allowed")

        checkNumInputs(results)
        checkOptions(results, options)

        return transformSpmv(results as List<SpmvBenchmarkResult>, options)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of spmv benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transformSpmv(benchmarkResults: List<SpmvBenchmarkResult>, options: Map<String, String>): PlottableData
}