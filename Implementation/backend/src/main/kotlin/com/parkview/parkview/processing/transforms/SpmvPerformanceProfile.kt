package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType

class SpmvPerformanceProfile : SpmvPlotTransform {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Line)
    override val name = "SpmvPerformanceProfile"
    override val availableOptions: List<PlotOption> = listOf(
        PlotOption(
            name = "xAxis",
            options = listOf("speedup")
        )
    )

    override fun transformSpmv(
        benchmarkResults: List<SpmvBenchmarkResult>,
        options: Map<String, String>
    ): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()
        val formatSlowdowns: MutableMap<String, MutableList<Double>> = mutableMapOf()

        val dataPoints = benchmarkResults[0].datapoints

        for (dataPoint in dataPoints) {
            val minTime = dataPoint.formats.filter { it.completed }.map { it.time }.minOrNull() ?: continue
            dataPoint.formats.forEach {
                formatSlowdowns.getOrPut(it.name) { mutableListOf() } += (it.time / minTime)
            }
        }

        formatSlowdowns.forEach { (_, value) -> value.sort() }

        for ((key, value) in formatSlowdowns) {
            seriesByName.getOrPut(key) { mutableListOf() } += value.mapIndexed { index, d ->
                PlotPoint(
                    x = d,
                    y = index.toDouble(),
                )
            }
        }

        return DatasetSeries(
            seriesByName.map { (key, value) -> Dataset(label = key, data = value.sortedBy { it.x }.toMutableList()) }
        )
    }

}