package com.parkview.parkview.processing.transforms.conversion

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.*

class ConversionSingleScatterPlot : ConversionPlotTransform {
    override val numInputsRange = 1..1
    override val plottableAs: List<PlotType> = listOf(PlotType.Scatter)
    override val name: String = "conversionSingleScatter"
    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        MATRIX_X_AXIS,
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
                        else -> throw InvalidPlotOptionsException(options, "xAxis")
                    },
                    y = when (options["yAxis"]) {
                        "bandwidth" -> datapoint.nonzeros / conversion.time
                        "time" -> conversion.time
                        else -> throw InvalidPlotOptionsException(options, "yAxis")
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