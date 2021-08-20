package com.parkview.parkview.processing.transforms.spmv

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.benchmark.SpmvDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.MATRIX_X_AXIS
import com.parkview.parkview.processing.transforms.PlotConfiguration
import com.parkview.parkview.processing.transforms.PlotPoint
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.PointDataset
import com.parkview.parkview.processing.transforms.getAvailableComparisons

class SpmvSpeedupPlot : SpmvPlotTransform() {
    override val numInputsRange = 2..2
    override val plottableAs = listOf(PlotType.Line, PlotType.Scatter)
    override val name = "Speedup Plot"
    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        MATRIX_X_AXIS,
        getAvailableComparisons(results),
    )

    override fun transformSpmv(
        benchmarkResults: List<SpmvBenchmarkResult>,
        config: PlotConfiguration,
    ): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        val comparison = config.getCategoricalOption("compare")
        val firstComponent = comparison.split("/").first()

        val datapointsA: List<SpmvDatapoint>
        val datapointsB: List<SpmvDatapoint>

        if (firstComponent == benchmarkResults.first().identifier) {
            datapointsA = benchmarkResults[0].datapoints
            datapointsB = benchmarkResults[1].datapoints
        } else {
            datapointsA = benchmarkResults[1].datapoints
            datapointsB = benchmarkResults[0].datapoints
        }

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
                    x = datapointA.getXAxisByConfig(config),
                    y = formatA.time / formatB.time
                )
            }
        }

        return PlottableData(
            seriesByName.map { (key, value) -> PointDataset(label = key, data = value.sortedBy { it.x }) }
        )
    }
}
