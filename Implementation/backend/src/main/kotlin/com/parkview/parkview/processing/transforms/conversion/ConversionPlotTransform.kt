package com.parkview.parkview.processing.transforms.conversion

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.transforms.InvalidPlotTransformException
import com.parkview.parkview.processing.transforms.PlotTransform
import com.parkview.parkview.processing.transforms.PlottableData

/**
 * Interface for transforms using [ConversionBenchmarkResult].
 */
interface ConversionPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData {
        for (result in results) if (result !is ConversionBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only ConversionBenchmarkResult is allowed")

        checkNumInputs(results)
        checkOptions(results, options)


        return transformConversion(results.filterIsInstance<ConversionBenchmarkResult>(), options)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of conversion benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transformConversion(
        benchmarkResults: List<ConversionBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData
}