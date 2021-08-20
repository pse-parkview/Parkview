package com.parkview.parkview.processing.transforms.preconditioner

import com.parkview.parkview.benchmark.PreconditionerBenchmarkResult
import com.parkview.parkview.benchmark.PreconditionerDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException
import com.parkview.parkview.processing.transforms.MatrixPlotTransform
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.filterMatrixDatapoints

/**
 * Interface for transforms using [PreconditionerBenchmarkResult].
 */
abstract class PreconditionerPlotTransform : MatrixPlotTransform() {
    override fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData {
        for (result in results) if (result !is PreconditionerBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only PreconditionerBenchmarkResult is allowed")

        checkNumInputs(results.size)
        checkOptions(results, options)

        val filteredResults = results.filterIsInstance<PreconditionerBenchmarkResult>().map {
            PreconditionerBenchmarkResult(
                commit = it.commit,
                device = it.device,
                datapoints = filterMatrixDatapoints(it.datapoints, options).filterIsInstance<PreconditionerDatapoint>(),
            )
        }

        return transformPreconditioner(filteredResults, options)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of preconditioner benchmark results
     * @return [PlottableData] object containing the data
     */
    abstract fun transformPreconditioner(
        benchmarkResults: List<PreconditionerBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData
}
