package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.ConversionBenchmarkResult
import com.parkview.parkview.processing.*

class ConversionSpeedupPlot : ConversionPlotTransform {
    override val numAllowedInputs = Pair(2, 2)
    override val plottableAs = listOf(PlotType.Line)
    override val name = "conversionSpeedup"

    override fun transformConversion(benchmarkResults: List<ConversionBenchmarkResult>): PlottableData {
        if (benchmarkResults.size != 2) throw InvalidPlotTransformException(
            "ConversionSpeedupPlot can only be used with two ConversionBenchmarkResult"
        )

        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        val datapointsA = benchmarkResults[0].datapoints.sortedBy { it.nonzeros }
        val datapointsB = benchmarkResults[1].datapoints.sortedBy { it.nonzeros }

        for (datapointA in datapointsA) {
            val datapointB = datapointsB.find { it.nonzeros == datapointA.nonzeros } ?: continue

            for (conversionA in datapointA.conversions) {
                val conversionB = datapointB.conversions.find { it.name == conversionA.name } ?: continue
                if (!conversionA.completed or !conversionB.completed) continue

                seriesByName.getOrPut(conversionA.name) { mutableListOf() } += PlotPoint(
                    x = datapointA.nonzeros.toDouble(),
                    conversionA.time / conversionB.time
                )
            }
        }


        return DatasetSeries(
            seriesByName.map { (key, value) -> Dataset(label = key, data = value) }
        )
    }
}