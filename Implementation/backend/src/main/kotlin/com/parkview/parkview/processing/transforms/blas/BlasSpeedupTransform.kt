package com.parkview.parkview.processing.transforms.blas

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.benchmark.BlasDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.*

class BlasSpeedupTransform : BlasPlotTransform() {
    override val numInputsRange = 2..2
    override val plottableAs = listOf(PlotType.Line, PlotType.Scatter)
    override val name = "Speedup Plot"
    override fun getBlasPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        BLAS_X_AXIS,
        getAvailableComparisons(results),
    )

    override fun transformBlas(
        benchmarkResults: List<BlasBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        val comparison = options.getOptionValueByName("compare")
        val firstComponent = comparison.split("/").first()

        val datapointsA: List<BlasDatapoint>
        val datapointsB: List<BlasDatapoint>

        if (firstComponent == benchmarkResults.first().identifier) {
            datapointsA = benchmarkResults[0].datapoints
            datapointsB = benchmarkResults[1].datapoints
        } else {
            datapointsA = benchmarkResults[1].datapoints
            datapointsB = benchmarkResults[0].datapoints
        }

        for (datapointA in datapointsA) {
            val datapointB = datapointsB.find {
                (it.n == datapointA.n) and (it.m == datapointA.m) and (it.r == datapointA.r) and (it.k == datapointA.k)
            } ?: continue

            for (operationA in datapointA.operations) {
                val operationB = datapointB.operations.find { it.name == operationA.name } ?: continue
                if (!operationA.completed or !operationB.completed) continue

                seriesByName.getOrPut(operationA.name) { mutableListOf() } += PlotPoint(
                    x = datapointA.getXAxisByOption(options).toDouble(),
                    y = operationA.time / operationB.time
                )
            }
        }

        return DatasetSeries(
            seriesByName.map { (key, value) -> PointDataset(label = key, data = value.sortedBy { it.x }) }
        )
    }
}