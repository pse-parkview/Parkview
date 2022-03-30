package com.parkview.parkview.processing.transforms

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.DynamicCategoricalOption

/**
 * General options used by plots.
 */
object PlotOptions {
    /**
     * Given two benchmarks, which benchmark should be the baseline
     */
    val comparison = object : DynamicCategoricalOption("baseline", "Benchmark used as baseline") {
        override fun getOptions(results: Array<BenchmarkResult>): Array<String> = if (results.size == 2)
            arrayOf(
                results[0].identifier,
                results[1].identifier,
            )
        else throw InvalidPlotTransformException("Comparison is only possible between two benchmarks")
    }
}
