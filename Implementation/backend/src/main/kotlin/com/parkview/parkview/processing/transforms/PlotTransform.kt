package com.parkview.parkview.processing.transforms

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.PlottableData

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
    val availableOptions: List<PlotOption>

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param results list of benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transform(results: List<BenchmarkResult>, options: Map<String, String>): PlottableData

    fun checkOptions(options: Map<String, String>): Boolean {
        for ((key, value) in options) {
            val option = availableOptions.find { it.name == key }
                ?: throw InvalidPlotTransformException("$key not a valid option for $name")
            if (value !in option.options) throw InvalidPlotTransformException("$value is not a possible value of ${option.name}")
        }

        return true
    }

    fun checkNumInputs(results: List<BenchmarkResult>) {
        if (results.size !in numInputsRange) throw InvalidPlotTransformException(
            "$name can only be used with ${numInputsRange.first} to ${numInputsRange.last} inputs"
        )
    }
}