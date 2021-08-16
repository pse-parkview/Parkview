package com.parkview.parkview.processing.transforms.solver

import com.parkview.parkview.benchmark.SolverBenchmarkResult
import com.parkview.parkview.benchmark.SolverDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException
import com.parkview.parkview.processing.transforms.MatrixPlotTransform
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.filterMatrixDatapoints

/**
 * Interface for transforms using [SolverBenchmarkResult].
 */
abstract class SolverPlotTransform : MatrixPlotTransform() {
    override fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData {
        for (result in results) if (result !is SolverBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only SolverBenchmarkResult is allowed")

        checkNumInputs(results)
        checkOptions(results, options)

        val filteredResults = results.filterIsInstance<SolverBenchmarkResult>().map {
            SolverBenchmarkResult(
                commit = it.commit,
                device = it.device,
                benchmark = it.benchmark,
                datapoints = filterMatrixDatapoints(it.datapoints, options).filterIsInstance<SolverDatapoint>(),
            )
        }

        return transformSolver(filteredResults, options)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of solver benchmark results
     * @return [PlottableData] object containing the data
     */
    abstract fun transformSolver(
        benchmarkResults: List<SolverBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData
}