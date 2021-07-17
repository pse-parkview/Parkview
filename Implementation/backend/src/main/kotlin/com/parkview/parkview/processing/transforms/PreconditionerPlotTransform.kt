package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.PreconditionerBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlottableData

/**
 * Interface for transforms using [PreconditionerBenchmarkResult].
 */
interface PreconditionerPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, xAxis: String): PlottableData {
        for (result in results) if (result !is PreconditionerBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only PreconditionerBenchmarkResult is allowed")

        checkNumInputs(results)
        checkXAxis(xAxis)


        return transformPreconditioner(results as List<PreconditionerBenchmarkResult>, xAxis)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of preconditioner benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transformPreconditioner(benchmarkResults: List<PreconditionerBenchmarkResult>, xAxis: String): PlottableData
}