package com.parkview.parkview.processing.transforms

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.PlottableData

interface PlotTransform {
    /**
     * number of allowed inputs as Pair(min, max)
     */
    val numAllowedInputs: Pair<Int, Int>

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
    val xAxis: List<String>

    /**
     * Transforms the benchmark data to data that is plottable
     *
     * @param results list of benchmark results
     * @return [PlottableData] object containing the data
     */
    fun transform(results: List<BenchmarkResult>, xAxis: String): PlottableData
}