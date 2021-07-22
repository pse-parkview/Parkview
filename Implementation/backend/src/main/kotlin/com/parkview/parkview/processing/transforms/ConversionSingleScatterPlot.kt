package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType

class ConversionSingleScatterPlot : ConversionPlotTransform {
    override val numInputsRange = 1..1
    override val plottableAs: List<PlotType> = listOf(PlotType.Scatter)
    override val name: String = "conversionSingleScatter"
    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        PlotOption(
            name = "xAxis",
            options = listOf("nonzeros", "rows", "columns")
        ),
        PlotOption(
            name = "yAxis",
            options = listOf("bandwidth", "time")
        ),
    )

    override fun transformConversion(
        benchmarkResults: List<ConversionBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData {
        val benchmarkResult = benchmarkResults[0]

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for (datapoint in benchmarkResult.datapoints) {
            for (conversion in datapoint.conversions) {
                if (!conversion.completed) continue
                seriesByName.getOrPut(conversion.name) { mutableListOf() } += PlotPoint(
                    x = when (options["xAxis"]) {
                        "nonzeros" -> datapoint.nonzeros.toDouble()
                        "rows" -> datapoint.rows.toDouble()
                        "columns" -> datapoint.columns.toDouble()
                        else -> throw InvalidPlotTransformException("Invalid value for yAxis")
                    },
                    y = when (options["yAxis"]) {
                        "bandwidth" -> datapoint.nonzeros / conversion.time
                        "time" -> conversion.time
                        else -> throw InvalidPlotTransformException("Invalid value for yAxis")
                    },
                )
            }
        }

        return DatasetSeries(seriesByName.map { (key, value) ->
            PointDataset(
                label = key,
                data = value.sortedBy { it.x }.toMutableList()
            )
        })
    }
}