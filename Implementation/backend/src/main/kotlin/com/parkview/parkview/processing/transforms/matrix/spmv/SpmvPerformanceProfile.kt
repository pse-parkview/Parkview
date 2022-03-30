package com.parkview.parkview.processing.transforms.matrix.spmv

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.DynamicNumericalOption
import com.parkview.parkview.processing.NumericalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlotPoint
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.PointDataset

class SpmvPerformanceProfile : SpmvPlotTransform() {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Line)
    override val name = "Performance Profile"

    private val minXOption = NumericalOption(
        name = "minX",
        default = 0.0,
        description = "Minimum X value (value on the left)",
    )

    private val maxXOption = object : DynamicNumericalOption("maxX", "Maximum X value (value on the right)") {
        override fun getDefault(results: Array<BenchmarkResult>): Double =
            results.maxOfOrNull { it.datapoints.size }?.toDouble() ?: 0.0
    }

    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        minXOption,
        maxXOption.realizeOption(results.toTypedArray()),
    )

    override fun transformSpmv(
        benchmarkResults: List<SpmvBenchmarkResult>,
        config: PlotConfiguration,
    ): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()
        val formatSlowdowns: MutableMap<String, MutableList<Double>> = mutableMapOf()

        val dataPoints = benchmarkResults[0].datapoints

        for (dataPoint in dataPoints) {
            val minTime = dataPoint.formats.filter { it.completed }.map { it.time }.minOrNull() ?: continue
            dataPoint.formats.filter { !it.time.isNaN() and it.completed }.forEach {
                formatSlowdowns.getOrPut(it.name) { mutableListOf() } += (it.time / minTime)
            }
        }

        formatSlowdowns.forEach { (_, value) -> value.sort() }

        val minX = config.getNumericalOption(minXOption)
        val maxX = config.getNumericalOption(maxXOption)

        for ((key, value) in formatSlowdowns) {
            seriesByName.getOrPut(key) { mutableListOf() } += value.filter { d -> (d <= maxX) and (d >= minX) }
                .mapIndexed { index, d ->
                    PlotPoint(
                        x = d,
                        y = index.toDouble(),
                    )
                }.filter { it.x > 1 }
        }

        return PlottableData(
            seriesByName.map { (key, value) -> PointDataset(label = key, data = value.sortedBy { it.x }.toTypedArray()) }.toTypedArray()
        )
    }
}
