package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlottableData

/**
 * Interface for transforms using [SolverBenchmarkResult].
 */
interface SolverPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, xAxis: String): PlottableData {
        for (result in results) if (result !is SolverBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only SolverBenchmarkResult is allowed")

        checkNumInputs(results)
        checkXAxis(xAxis)

        return transformSolver(results as List<SolverBenchmarkResult>, xAxis)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of solver benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transformSolver(benchmarkResults: List<SolverBenchmarkResult>, xAxis: String): PlottableData
}