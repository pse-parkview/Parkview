package com.parkview.parkview.processing.transforms.conversion

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.git.BenchmarkResult
import com.parkview.parkview.processing.PlotOption
import com.parkview.parkview.processing.PlotType
import com.parkview.parkview.processing.transforms.*

class ConversionSpeedupPlot : ConversionPlotTransform {
    override val numInputsRange = 2..2
    override val plottableAs = listOf(PlotType.Line)
    override val name = "conversionSpeedup"
    override fun getAvailableOptions(results: List<BenchmarkResult>): List<PlotOption> = listOf(
        MATRIX_X_AXIS,
    )

    override fun transformConversion(
        benchmarkResults: List<ConversionBenchmarkResult>,
        options: Map<String, String>,
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

            for (conversionA in datapointA.conversions) {
                val conversionB = datapointB.conversions.find { it.name == conversionA.name } ?: continue
                if (!conversionA.completed or !conversionB.completed) continue

                seriesByName.getOrPut(conversionA.name) { mutableListOf() } += PlotPoint(
                    x = when (options["xAxis"]) {
                        "nonzeros" -> datapointA.nonzeros.toDouble()
                        "rows" -> datapointA.rows.toDouble()
                        "columns" -> datapointA.columns.toDouble()
                        else -> throw InvalidPlotOptionsException(options, "xAxis")
                    },
                    y = conversionA.time / conversionB.time,
                )
            }
        }

        return DatasetSeries(
            seriesByName.map { (key, value) -> PointDataset(label = key, data = value.sortedBy { it.x }) }
        )
    }
}