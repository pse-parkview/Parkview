package com.parkview.parkview.processing.transforms.matrix.preconditioner

import com.parkview.parkview.benchmark.PreconditionerBenchmarkResult
import com.parkview.parkview.benchmark.PreconditionerDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.filterMatrixDatapoints
import com.parkview.parkview.processing.transforms.matrix.MatrixPlotTransform

/**
 * Interface for transforms using [PreconditionerBenchmarkResult].
 */
abstract class PreconditionerPlotTransform : MatrixPlotTransform() {
    override fun transform(results: List<BenchmarkResult>, config: PlotConfiguration): PlottableData {
        for (result in results) if (result !is PreconditionerBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only PreconditionerBenchmarkResult is allowed")

        checkNumInputs(results.size)

        val filteredResults = results.filterIsInstance<PreconditionerBenchmarkResult>().map {
            PreconditionerBenchmarkResult(
                commit = it.commit,
                device = it.device,
                datapoints = filterMatrixDatapoints(it.datapoints, config).filterIsInstance<PreconditionerDatapoint>(),
            )
        }

        return transformPreconditioner(filteredResults, config)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of preconditioner benchmark results
     * @return [PlottableData] object containing the data
     */
    abstract fun transformPreconditioner(
        benchmarkResults: List<PreconditionerBenchmarkResult>,
        config: PlotConfiguration,
    ): PlottableData
}
