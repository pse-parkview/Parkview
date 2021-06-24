package com.parkview.parkview.processing

import com.parkview.parkview.benchmark.BlasBenchmarkResult

/**
 * Interface for transforms using a single [BlasBenchmarkResult].
 */
interface BlasSinglePlotTransform {
    /**
     * Transforms the benchmark data into a JSON containing the prepared values for plotting
     *
     * @param benchmarkResult benchmark result that gets transformed
     * @return a String containing the transformed data in json format
     */
    fun transform(benchmarkResult: BlasBenchmarkResult): String
}