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
        options: Map<String, String>
    ): PlottableData {
        val benchmarkResult = benchmarkResults.first()

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()
        for (datapoint in benchmarkResult.datapoints) {
            for (operation in datapoint.operations) {
                if (!operation.completed) continue
                seriesByName.getOrPut(operation.name) { mutableListOf() } += PlotPoint(
                    x = datapoint.getFieldByName(
                        options["xAxis"] ?: throw InvalidPlotTransformException("No value given for xAxis")
                    ).toDouble(),
                    y = operation.getFieldByName(
                        options["yAxis"] ?: throw InvalidPlotTransformException("No value given for yAxis")
                    ),
                )
            }
        }

        return DatasetSeries(seriesByName.map { (key, value) -> Dataset(label = key, data = value.sortedBy { it.x }) })
    }

    private fun Operation.getFieldByName(name: String): Double = when (name) {
        "time" -> this.time
        "flops" -> this.flops
        "bandwidth" -> this.bandwidth
        else -> throw InvalidPlotTransformException("$name is not a valid yAxis value")
    }

    private fun BlasDatapoint.getFieldByName(name: String): Long = when (name) {
        "n" -> this.n
        "r" -> this.r
        "m" -> this.m
        "k" -> this.k
        else -> throw InvalidPlotTransformException("$name is not a valid xAxis value")
    }
}