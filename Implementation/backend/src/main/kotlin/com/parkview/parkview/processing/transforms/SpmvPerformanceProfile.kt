package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.processing.*

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

        val dataPoints = benchmarkResults[0].datapoints

        var minTime: Double = -1.0

        for (dataPoint in dataPoints) {
            for (format in dataPoint.formats) {
                if(!format.completed) continue
                if(format.time < minTime ||minTime < 0) {
                    minTime = format.time
                }
            }
        }

        var index: Double = -1.0
        for (dataPoint in dataPoints) {
            for (format in dataPoint.formats) {
                if(!format.completed) continue
                index++
                seriesByName.getOrPut(format.name) { mutableListOf() } += PlotPoint(
                    x = format.time/minTime,
                    y = index
                )
            }
        }

        return DatasetSeries(
            seriesByName.map { (key, value) -> Dataset(label = key, data = value.sortedBy { it.x }.toMutableList()) }
        )
    }

}