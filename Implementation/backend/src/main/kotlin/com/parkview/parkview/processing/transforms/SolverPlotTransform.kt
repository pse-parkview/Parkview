package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlottableData

/**
 * Interface for transforms using multiple [SolverBenchmarkResult].
 */
interface SolverPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>): PlottableData {
        for (result in results) if (result !is SolverBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type")

        return transformSolver(results as List<SolverBenchmarkResult>)
    }

    /**
     * Transforms the benchmark data into a JSON containing the prepared values for plotting
     *
     * @param benchmarkResults list of benchmark results
     * @return a String containing the transformed data in json format
     */
    fun transformSolver(benchmarkResults: List<SolverBenchmarkResult>): PlottableData
}