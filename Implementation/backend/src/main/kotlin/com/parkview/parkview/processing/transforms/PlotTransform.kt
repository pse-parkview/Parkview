package com.parkview.parkview.processing.transforms

import com.parkview.parkview.git.BenchmarkResult
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
     * Values that can be used for the xAxis
     */
    fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption>

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param results list of benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData

    /**
     * Checks the given options for validity
     *
     * @param results list of [BenchmarkResults][BenchmarkResult]
     * @param options given options
     *
     * @return true if options are valid, otherwise false
     */
    fun checkOptions(results: List<BenchmarkResult>, options: Map<String, String>): Boolean {
        for ((key, value) in options) {
            val option = getAvailableOptions(results).find { it.name == key }
                ?: continue
            if ((value !in option.options) and !option.number) throw InvalidPlotTransformException("$value is not a possible value of ${option.name}")
        }

        return true
    }

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