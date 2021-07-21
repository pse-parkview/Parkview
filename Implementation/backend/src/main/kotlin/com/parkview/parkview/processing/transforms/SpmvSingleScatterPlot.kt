package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType

class SpmvSingleScatterPlot : SpmvPlotTransform {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Scatter)
    override val name = "spmvSingleScatterPlot"
    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        PlotOption(
            name = "yAxis",
            options = listOf("bandwidth", "time"),
        ),
        PlotOption(
            name = "xAxis",
            options = listOf("nonzeros"),
        ),
    )

    override fun transformSpmv(
        benchmarkResults: List<SpmvBenchmarkResult>,
        options: Map<String, String>
    ): PlottableData {
        val benchmarkResult = benchmarkResults.first()

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for (datapoint in benchmarkResult.datapoints) {
            for (format in datapoint.formats) {
                if (!format.completed) continue
                seriesByName.getOrPut(format.name) { mutableListOf() } += PlotPoint(
                    x = datapoint.nonzeros.toDouble(),
                    y = when (options["yAxis"]) {
                        "bandwidth" -> format.storage + (datapoint.rows + datapoint.columns) / format.time
                        "time" -> format.time
                        else -> throw InvalidPlotTransformException("Illegal value for yAxis")
                    },
                )
            }
        }

        return DatasetSeries(seriesByName.map { (key, value) -> Dataset(label = key, data = value.sortedBy { it.x }) })
    }
}