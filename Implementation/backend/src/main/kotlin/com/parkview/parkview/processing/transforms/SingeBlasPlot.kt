package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.benchmark.Operation
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType

class SingeBlasPlot : BlasPlotTransform {
    override val numInputsRange: IntRange = 1..1
    override val plottableAs: List<PlotType> = listOf(PlotType.Scatter, PlotType.Line)
    override val name: String = "blasSingleBenchmark"
    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        PlotOption(
            name = "yAxis",
            options = listOf("time", "flops", "bandwidth")
        ),
        PlotOption(
            name = "xAxis",
            options = listOf("n", "r", "m", "k")
        ),
    )

    override fun transformBlas(
        benchmarkResults: List<BlasBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData {
        val benchmarkResult = benchmarkResults.first()

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()
        for (datapoint in benchmarkResult.datapoints) {
            for (operation in datapoint.operations) {
                if (!operation.completed) continue
                seriesByName.getOrPut(operation.name) { mutableListOf() } += PlotPoint(
                    x = datapoint.getXAxisByOption(options).toDouble(),
                    y = operation.getYAxisByOption(options)
                )
            }
        }

        return DatasetSeries(seriesByName.map { (key, value) ->
            PointDataset(label = key,
                data = value.sortedBy { it.x })
        })
    }
}