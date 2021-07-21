package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.BlasBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType

class BlasSpeedupTransform : BlasPlotTransform {
    override val numInputsRange = 2..2
    override val plottableAs = listOf(PlotType.Line)
    override val name = "blasSpeedup"
    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        PlotOption(
            name = "xAxis",
            options = listOf("n", "m", "r", "k")
        )
    )

    override fun transformBlas(
        benchmarkResults: List<BlasBenchmarkResult>,
        options: Map<String, String>
    ): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        val datapointsA = benchmarkResults[0].datapoints
        val datapointsB = benchmarkResults[1].datapoints

        for (datapointA in datapointsA) {
            val datapointB = datapointsB.find {
                (it.n == datapointA.n) and (it.m == datapointA.m) and (it.r == datapointA.r) and (it.k == datapointA.k)
            } ?: continue

            for (operationA in datapointA.operations) {
                val operationB = datapointB.operations.find { it.name == operationA.name } ?: continue
                if (!operationA.completed or !operationB.completed) continue

                seriesByName.getOrPut(operationA.name) { mutableListOf() } += PlotPoint(
                    x = when (options["xAxis"]) {
                        "n" -> datapointA.n.toDouble()
                        "r" -> datapointA.r.toDouble()
                        "k" -> datapointA.k.toDouble()
                        "m" -> datapointA.m.toDouble()
                        else -> throw InvalidPlotTransformException("${options["xAxis"]} is not a valid value for xAxis")
                    },
                    y = operationA.time / operationB.time
                )
            }
        }

        return DatasetSeries(
            seriesByName.map { (key, value) -> PointDataset(label = key, data = value.sortedBy { it.x }) }
        )
    }
}