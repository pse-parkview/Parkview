package com.parkview.parkview.processing.transforms.spmv

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.InvalidPlotConfigValueException
import com.parkview.parkview.processing.transforms.MATRIX_X_AXIS
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlotPoint
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.PointDataset

class SpmvSingleScatterPlot : SpmvPlotTransform() {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Scatter)
    override val name = "Scatter Plot"

    private val yAxisOption = CategoricalOption(
        name = "yAxis",
        options = listOf("bandwidth", "time"),
        description = "Value that gets displayed on the y axis"
    )

    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        MATRIX_X_AXIS,
        yAxisOption,
    )

    override fun transformSpmv(
        benchmarkResults: List<SpmvBenchmarkResult>,
        config: PlotConfiguration,
    ): PlottableData {
        val benchmarkResult = benchmarkResults.first()

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for (datapoint in benchmarkResult.datapoints) {
            for (format in datapoint.formats) {
                if (!format.completed) continue
                seriesByName.getOrPut(format.name) { mutableListOf() } += PlotPoint(
                    x = datapoint.getXAxisByConfig(config),
                    y = when (config.getCategoricalOption(yAxisOption)) {
                        "bandwidth" -> (format.storage + datapoint.rows + datapoint.columns) / format.time
                        "time" -> format.time
                        else -> throw InvalidPlotConfigValueException(config.getCategoricalOption("yAxis"), "yAxis")
                    },
                )
            }
        }

        return PlottableData(
            seriesByName.map { (key, value) ->
                PointDataset(
                    label = key,
                    data = value.sortedBy { it.x }
                )
            }
        )
    }
}
