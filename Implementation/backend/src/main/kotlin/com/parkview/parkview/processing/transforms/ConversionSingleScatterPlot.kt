package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.processing.*

class ConversionSingleScatterPlot : ConversionPlotTransform {
    override val numInputsRange = 1..1
    override val plottableAs: List<PlotType> = listOf(PlotType.Scatter)
    override val name: String = "conversionSingleScatter"
    override val availableOptions: List<PlotOption> = listOf(
        PlotOption(
            name = "xAxis",
            options = listOf("nonzeros")
        )
    )

    override fun transformConversion(
        benchmarkResults: List<ConversionBenchmarkResult>,
        options: Map<String, String>
    ): PlottableData {
        val benchmarkResult = benchmarkResults[0]

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for (datapoint in benchmarkResult.datapoints) {
            for (conversion in datapoint.conversions) {
                if (!conversion.completed) continue
                seriesByName.getOrPut(conversion.name) { mutableListOf() } += PlotPoint(
                    x = datapoint.nonzeros.toDouble(),
                    y = when (options["yAxis"]) {
                        "bandwidth" -> datapoint.nonzeros / conversion.time
                        "time" -> conversion.time
                        else -> throw InvalidPlotTransformException("Invalid value for yAxis")
                    },
                )
            }
        }

        return DatasetSeries(seriesByName.map { (key, value) -> Dataset(label = key, data = value) })
    }
}