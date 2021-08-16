package com.parkview.parkview.processing.transforms.spmv

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.DatasetSeries
import com.parkview.parkview.processing.transforms.PlotPoint
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.PointDataset

class SpmvPerformanceProfile : SpmvPlotTransform() {
    override val numInputsRange = 1..1
    override val plottableAs = listOf(PlotType.Line)
    override val name = "SpmvPerformanceProfile"
    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf()

    override fun transformSpmv(
        benchmarkResults: List<SpmvBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()
        val formatSlowdowns: MutableMap<String, MutableList<Double>> = mutableMapOf()

        val dataPoints = benchmarkResults[0].datapoints

        for (dataPoint in dataPoints) {
            val minTime = dataPoint.formats.filter { it.completed }.map { it.time }.minOrNull() ?: continue
            dataPoint.formats.forEach {
                formatSlowdowns.getOrPut(it.name) { mutableListOf() } += (it.time / minTime)
            }
        }

        formatSlowdowns.forEach { (_, value) -> value.sort() }

        for ((key, value) in formatSlowdowns) {
            seriesByName.getOrPut(key) { mutableListOf() } += value.mapIndexed { index, d ->
                PlotPoint(
                    x = d,
                    y = index.toDouble(),
                )
            }
        }

        return DatasetSeries(
            seriesByName.map { (key, value) -> PointDataset(label = key, data = value.sortedBy { it.x }) }
        )
    }

}