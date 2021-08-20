package com.parkview.parkview.processing.transforms

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.DynamicCategoricalOption

object PlotOptions {
    val comparison = object : DynamicCategoricalOption("compare", "Which speedup to compute") {
        override fun getOptions(results: List<BenchmarkResult>): List<String> = if (results.size == 2)
            listOf(
                results[0].identifier + "/" + results[1].identifier,
                results[1].identifier + "/" + results[0].identifier,
            )
        else throw InvalidPlotTransformException("Comparison is only possible between two benchmarks")
    }
}
