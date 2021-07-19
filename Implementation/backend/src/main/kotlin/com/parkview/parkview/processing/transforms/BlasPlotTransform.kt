package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult

/**
 * Interface for transforms using [BlasBenchmarkResult].
 */
interface BlasPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData {
        for (result in results) if (result !is BlasBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only BlasBenchmarkResult is allowed")

        checkNumInputs(results)
        checkOptions(options)


        return transformBlas(results as List<BlasBenchmarkResult>, options)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of blas benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transformBlas(benchmarkResults: List<BlasBenchmarkResult>, options: Map<String, String>): PlottableData
}