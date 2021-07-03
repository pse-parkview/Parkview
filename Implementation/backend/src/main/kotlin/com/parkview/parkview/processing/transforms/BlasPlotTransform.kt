package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.processing.PlottableData

/**
 * Interface for transforms using multiple [BlasBenchmarkResult].
 */
interface BlasPlotTransform {
    /**
     * Transforms the benchmark data into a JSON containing the prepared values for plotting
     *
     * @param benchmarkResults list of benchmark results
     * @return a String containing the transformed data in json format
     */
    fun transform(benchmarkResults: List<BlasBenchmarkResult>): PlottableData
}