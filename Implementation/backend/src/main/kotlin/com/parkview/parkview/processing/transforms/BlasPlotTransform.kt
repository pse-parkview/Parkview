package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlottableData

/**
 * Interface for transforms using [BlasBenchmarkResult].
 */
interface BlasPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, xAxis: String): PlottableData {
        for (result in results) if (result !is BlasBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only BlasBenchmarkResult is allowed")

        checkNumInputs(results)
        checkXAxis(xAxis)


        return transformBlas(results as List<BlasBenchmarkResult>, xAxis)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of blas benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transformBlas(benchmarkResults: List<BlasBenchmarkResult>, xAxis: String): PlottableData
}