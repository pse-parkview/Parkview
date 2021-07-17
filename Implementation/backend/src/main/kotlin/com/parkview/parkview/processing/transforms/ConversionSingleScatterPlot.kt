package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.processing.*

enum class ConversionSingleScatterPlotYAxis {
    Time,
    Bandwidth,
}

class ConversionSingleScatterPlot(
    private val yAxis: ConversionSingleScatterPlotYAxis,
) : ConversionPlotTransform {
    override val numInputsRange = 1..1
    override val plottableAs: List<PlotType> = listOf(PlotType.Scatter)
    override val name: String = "conversion$yAxis"
    override val availableXAxis: List<String> = listOf("nonzeros")

    override fun transformConversion(benchmarkResults: List<ConversionBenchmarkResult>, xAxis: String): PlottableData {
        val benchmarkResult = benchmarkResults[0]

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for (datapoint in benchmarkResult.datapoints) {
            for (conversion in datapoint.conversions) {
                if (!conversion.completed) continue
                seriesByName.getOrPut(conversion.name) { mutableListOf() } += PlotPoint(
                    x = datapoint.nonzeros.toDouble(),
                    y = when (yAxis) {
                        ConversionSingleScatterPlotYAxis.Bandwidth -> datapoint.nonzeros / conversion.time
                        ConversionSingleScatterPlotYAxis.Time -> conversion.time
                    },
                )
            }
        }

        return DatasetSeries(seriesByName.map { (key, value) -> Dataset(label = key, data = value) })
    }
}