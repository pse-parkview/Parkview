package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlottableData

/**
 * Interface for transforms using [ConversionBenchmarkResult].
 */
interface ConversionPlotTransform : PlotTransform {
    override fun transform(results: List<BenchmarkResult>, xAxis: String): PlottableData {
        for (result in results) if (result !is ConversionBenchmarkResult) throw InvalidPlotTransformException("Invalid benchmark type, only ConversionBenchmarkResult is allowed")

        checkNumInputs(results)
        checkXAxis(xAxis)


        return transformConversion(results as List<ConversionBenchmarkResult>, xAxis)
    }

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param benchmarkResults list of conversion benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transformConversion(benchmarkResults: List<ConversionBenchmarkResult>, xAxis: String): PlottableData
}