package com.parkview.parkview.processing.transforms.conversion

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.benchmark.ConversionDatapoint
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.InvalidPlotOptionValueException
import com.parkview.parkview.processing.transforms.MATRIX_X_AXIS
import com.parkview.parkview.processing.transforms.PlotPoint
import com.parkview.parkview.processing.transforms.PlottableData
import com.parkview.parkview.processing.transforms.PointDataset
import com.parkview.parkview.processing.transforms.getAvailableComparisons
import com.parkview.parkview.processing.transforms.getOptionValueByName

class ConversionSpeedupPlot : ConversionPlotTransform() {
    override val numInputsRange = 2..2
    override val plottableAs = listOf(PlotType.Line, PlotType.Scatter)
    override val name = "Speedup Plot"
    override fun getMatrixPlotOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        MATRIX_X_AXIS,
        getAvailableComparisons(results),
    )

    override fun transformConversion(
        benchmarkResults: List<ConversionBenchmarkResult>,
        options: Map<String, String>,
    ): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        val comparison = options.getOptionValueByName("compare")
        val firstComponent = comparison.split("/").first()

        val datapointsA: List<ConversionDatapoint>
        val datapointsB: List<ConversionDatapoint>

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

            for (conversionA in datapointA.conversions) {
                val conversionB = datapointB.conversions.find { it.name == conversionA.name } ?: continue
                if (!conversionA.completed or !conversionB.completed) continue

                seriesByName.getOrPut(conversionA.name) { mutableListOf() } += PlotPoint(
                    x = when (options.getOptionValueByName("xAxis")) {
                        "nonzeros" -> datapointA.nonzeros.toDouble()
                        "rows" -> datapointA.rows.toDouble()
                        "columns" -> datapointA.columns.toDouble()
                        else -> throw InvalidPlotOptionValueException(options, "xAxis")
                    },
                    y = conversionA.time / conversionB.time,
                )
            }
        }

        return PlottableData(
            seriesByName.map { (key, value) -> PointDataset(label = key, data = value.sortedBy { it.x }) }
        )
    }
}
