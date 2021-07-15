package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.processing.*

enum class SpmvSingleScatterPlotYAxis {
    Time,
    Bandwidth,
}

class SpmvSingleScatterPlot(
    private val yAxis: SpmvSingleScatterPlotYAxis,
) : SpmvPlotTransform {
    override fun transformSpmv(benchmarkResults: List<SpmvBenchmarkResult>): PlottableData {
        if (benchmarkResults.size != 1) throw InvalidPlotTransformException(
            "SpmvSingleScatterPlot can only be used with a single BenchmarkResult"
        )

        val benchmarkResult = benchmarkResults[0]

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        for (datapoint in benchmarkResult.datapoints) {
            for (format in datapoint.formats) {
                if (!format.completed) continue
                seriesByName.getOrPut(format.name) { mutableListOf() } += PlotPoint(
                    x = datapoint.nonzeros.toDouble(),
                    y = when (yAxis) {
                        SpmvSingleScatterPlotYAxis.Bandwidth -> (format.storage + (datapoint.rows + datapoint.columns) / format.time)
                        SpmvSingleScatterPlotYAxis.Time -> format.time
                    },
                )
            }
        }

        return DatasetSeries(seriesByName.map { (key, value) -> Dataset(label = key, data = value) })
    }

    override val numAllowedInputs = Pair(1, 1);
    override val plottableAs = listOf(PlotType.Scatter)
    override val name = "spmvSingleScatter"
}