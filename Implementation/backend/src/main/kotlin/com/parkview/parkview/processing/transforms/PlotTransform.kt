package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.MatrixBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType

val MATRIX_X_AXIS = CategoricalOption(
    name = "xAxis",
    options = listOf("nonzeros", "rows", "columns")
)

fun getAvailableMatrixNames(results: List<BenchmarkResult>) = CategoricalOption(
    name = "matrix",
    options = (results.first() as MatrixBenchmarkResult).datapoints.map { it.name }
)

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

    fun checkOptions(results: List<BenchmarkResult>, options: Map<String, String>): Boolean {
        for ((key, value) in options) {
            val option = getAvailableOptions(results).find { it.name == key }
                ?: continue
            if ((value !in option.options) and !option.number) throw InvalidPlotTransformException("$value is not a possible value of ${option.name}")
        }

        return true
    }

    fun checkNumInputs(results: List<BenchmarkResult>) {
        if (results.size !in numInputsRange) throw InvalidPlotTransformException(
            "$name can only be used with ${numInputsRange.first} to ${numInputsRange.last} inputs"
        )
    }
}