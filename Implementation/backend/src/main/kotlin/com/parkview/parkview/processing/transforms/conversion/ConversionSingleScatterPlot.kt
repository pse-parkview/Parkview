package com.parkview.parkview.processing.transforms.conversion

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.*

class ConversionSingleScatterPlot : ConversionPlotTransform() {
    override val numInputsRange = 1..1
    override val plottableAs: List<PlotType> = listOf(PlotType.Scatter)
    override val name: String = "Scatter Plot"
    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        MATRIX_X_AXIS,
        CategoricalOption(
            name = "yAxis",
            options = listOf("bandwidth", "time"),
            description = "Value that gets displayed on the y axis"
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
                    x = when (options.getOptionValueByName("xAxis")) {
                        "nonzeros" -> datapoint.nonzeros.toDouble()
                        "rows" -> datapoint.rows.toDouble()
                        "columns" -> datapoint.columns.toDouble()
                        else -> throw InvalidPlotOptionValueException(options, "xAxis")
                    },
                    y = when (options.getOptionValueByName("yAxis")) {
                        "bandwidth" -> datapoint.nonzeros / conversion.time
                        "time" -> conversion.time
                        else -> throw InvalidPlotOptionValueException(options, "yAxis")
                    },
                )
            }
        }

        return PlottableData(seriesByName.map { (key, value) ->
            PointDataset(
                label = key,
                data = value.sortedBy { it.x }.toMutableList()
            )
        })
    }
}