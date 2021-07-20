package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.git.BenchmarkResult


class SpmvSpeedupPlot : SpmvPlotTransform {
    override val numInputsRange = 2..2
    override val plottableAs = listOf(PlotType.Line)
    override val name = "spmvSpeedup"
    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        PlotOption(
            name = "xAxis",
            options = listOf("nonzeros")
        )
    )

    override fun transformSpmv(
        benchmarkResults: List<SpmvBenchmarkResult>,
        options: Map<String, String>
    ): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        val datapointsA = benchmarkResults[0].datapoints
        val datapointsB = benchmarkResults[1].datapoints

        for (datapointA in datapointsA) {
            val datapointB = datapointsB.find {
                (it.nonzeros == datapointA.nonzeros) and
                        (it.rows == datapointA.rows) and
                        (it.columns == datapointA.columns)
            } ?: continue

            for (formatA in datapointA.formats) {
                val formatB = datapointB.formats.find { it.name == formatA.name } ?: continue
                if (!formatA.completed or !formatB.completed) continue

                seriesByName.getOrPut(formatA.name) { mutableListOf() } += PlotPoint(
                    x = datapointA.nonzeros.toDouble(),
                    y = formatA.time / formatB.time
                )
            }
        }


        return DatasetSeries(
            seriesByName.map { (key, value) -> Dataset(label = key, data = value.sortedBy { it.x }) }
        )
    }
}