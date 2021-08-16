package com.parkview.parkview.processing.transforms.spmv

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.CategoricalOption
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.*

class SpmvSingleScatterPlot : SpmvPlotTransform() {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Scatter)
    override val name = "spmvSingleScatterPlot"
    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        CategoricalOption(
            name = "yAxis",
            options = listOf("bandwidth", "time"),
        ),
        MATRIX_X_AXIS,
    )

    override fun transformSpmv(
        benchmarkResults: List<SpmvBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData {
        val benchmarkResult = benchmarkResults.first()

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for (datapoint in benchmarkResult.datapoints) {
            for (format in datapoint.formats) {
                if (!format.completed) continue
                seriesByName.getOrPut(format.name) { mutableListOf() } += PlotPoint(
                    x = when (options["xAxis"]) {
                        "nonzeros" -> datapoint.nonzeros.toDouble()
                        "rows" -> datapoint.rows.toDouble()
                        "columns" -> datapoint.columns.toDouble()
                        else -> throw InvalidPlotOptionsException(options, "xAxis")
                    },
                    y = when (options["yAxis"]) {
                        "bandwidth" -> (format.storage + datapoint.rows + datapoint.columns) / format.time
                        "time" -> format.time
                        else -> throw InvalidPlotOptionsException(options, "yAxis")
                    },
                )
            }
        }

        return DatasetSeries(seriesByName.map { (key, value) ->
            PointDataset(label = key,
                data = value.sortedBy { it.x })
        })
    }
}