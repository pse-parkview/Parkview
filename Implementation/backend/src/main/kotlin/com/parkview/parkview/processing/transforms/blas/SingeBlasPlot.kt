package com.parkview.parkview.processing.transforms.blas

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.DatasetSeries
import com.parkview.parkview.processing.transforms.PlotPoint
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.PointDataset

class SingeBlasPlot : BlasPlotTransform() {
    override val numInputsRange: IntRange = 1..1
    override val plottableAs: List<PlotType> = listOf(PlotType.Scatter, PlotType.Line)
    override val name: String = "Blas Plot"

    override fun getBlasPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        BLAS_X_AXIS,
        BLAS_Y_AXIS,
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