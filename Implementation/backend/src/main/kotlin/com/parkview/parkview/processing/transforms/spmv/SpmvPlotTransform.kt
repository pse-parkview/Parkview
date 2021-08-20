package com.parkview.parkview.processing.transforms.spmv

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.benchmark.SpmvDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException
import com.parkview.parkview.processing.transforms.MatrixPlotTransform
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.filterMatrixDatapoints

/**
 * Interface for transforms using [SpmvBenchmarkResult].
 */
abstract class SpmvPlotTransform : MatrixPlotTransform() {
    override fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData {
        for (result in results) if (result !is SpmvBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only SpmvBenchmarkResult is allowed")

        checkNumInputs(results.size)
        checkOptions(results, options)

        val filteredResults = results.filterIsInstance<SpmvBenchmarkResult>().map {
            SpmvBenchmarkResult(
                commit = it.commit,
                device = it.device,
                datapoints = filterMatrixDatapoints(it.datapoints, options).filterIsInstance<SpmvDatapoint>(),
            )
        }

        return transformSpmv(filteredResults, options)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of spmv benchmark results
     * @return [PlottableData] object containing the data
     */
    abstract fun transformSpmv(benchmarkResults: List<SpmvBenchmarkResult>, options: Map<String, String>): PlottableData
}
