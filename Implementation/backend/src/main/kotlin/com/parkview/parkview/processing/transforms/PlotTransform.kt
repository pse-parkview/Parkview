package com.parkview.parkview.processing.transforms

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotDescription
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType

interface PlotTransform {
    /**
     * number of allowed inputs as Pair(min, max)
     */
    val numInputsRange: IntRange

    /**
     * list of representations possible
     */
    val plottableAs: List<PlotType>

    /**
     * plot name
     */
    val name: String

    /**
     *
     */
    fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption>

    /**
     * Values that can be used for the xAxis
     */
    fun getPlotDescription(results: List<BenchmarkResult>) = PlotDescription(
        name,
        plottableAs,
        getAvailableOptions(results)
    )

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param results list of benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transform(results: List<BenchmarkResult>, config: PlotConfiguration): PlottableData

    /**
     * Checks if the plot is possible with the given number of results.
     *
     * @param num number of results
     *
     * @return true if possible, otherwise false
     */
    fun checkNumInputs(num: Int) {
        if (num !in numInputsRange) throw InvalidPlotTransformException(
            "$name can only be used with ${numInputsRange.first} to ${numInputsRange.last} inputs"
        )
    }
}
