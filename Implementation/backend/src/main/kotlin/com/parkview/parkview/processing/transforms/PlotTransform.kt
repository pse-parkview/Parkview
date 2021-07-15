package com.parkview.parkview.processing.transforms

import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.PlottableData

interface PlotTransform {
    val numAllowedInputs: Pair<Int, Int>
    val plottableAs: List<PlotType>
    val name: String

    fun transform(results: List<BenchmarkResult>): PlottableData
}