package com.parkview.parkview.processing.transforms.matrix.conversion

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.benchmark.ConversionDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException
import com.parkview.parkview.processing.transforms.matrix.MatrixPlotTransform
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.filterMatrixDatapoints

/**
 * Interface for transforms using [ConversionBenchmarkResult].
 */
abstract class ConversionPlotTransform : MatrixPlotTransform() {
    override fun transform(results: List<BenchmarkResult>, config: PlotConfiguration): PlottableData {
        for (result in results) if (result !is ConversionBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only ConversionBenchmarkResult is allowed")

        checkNumInputs(results.size)

        val filteredResults = results.filterIsInstance<ConversionBenchmarkResult>().map {
            ConversionBenchmarkResult(
                commit = it.commit,
                device = it.device,
                datapoints = filterMatrixDatapoints(it.datapoints, config).filterIsInstance<ConversionDatapoint>(),
            )
        }

        return transformConversion(filteredResults, config)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of conversion benchmark results
     * @return [PlottableData] object containing the data
     */
    abstract fun transformConversion(
        benchmarkResults: List<ConversionBenchmarkResult>,
        config: PlotConfiguration,
    ): PlottableData
}
