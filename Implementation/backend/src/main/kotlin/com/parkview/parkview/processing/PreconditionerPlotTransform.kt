package com.parkview.parkview.processing

import com.parkview.parkview.benchmark.PreconditionerBenchmarkResult

/**
 * Interface for transforms using multiple [PreconditionerBenchmarkResult].
 */
interface PreconditionerPlotTransform {
    /**
     * Transforms the benchmark data into a JSON containing the prepared values for plotting
     *
     * @param benchmarkResults list of benchmark results
     * @return a String containing the transformed data in json format
     */
    fun transform(benchmarkResults: List<PreconditionerBenchmarkResult>): String
}