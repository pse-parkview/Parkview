package com.parkview.parkview.processing

import com.parkview.parkview.benchmark.ConversionBenchmarkResult

/**
 * Interface for transforms using a single [ConversionBenchmarkResult].
 */
interface ConversionSinglePlotTransform {
    /**
     * Transforms the benchmark data into a JSON containing the prepared values for plotting
     *
     * @param benchmarkResult benchmark result that gets transformed
     * @return a String containing the transformed data in json format
     */
    fun transform(benchmarkResult: ConversionBenchmarkResult): String
}