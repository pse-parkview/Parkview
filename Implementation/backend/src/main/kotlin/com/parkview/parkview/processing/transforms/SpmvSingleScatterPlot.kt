package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.processing.LabeledPoint
import com.parkview.parkview.processing.PlottableData
import com.parkview.parkview.processing.PointList

enum class SpmvSingleScatterPlotYAxis {
    Time,
    Bandwidth,
}

class SpmvSingleScatterPlot(
    private val yAxis: SpmvSingleScatterPlotYAxis,
    private val rows: Long = -1,
    private val cols: Long = -1,
) : SpmvPlotTransform {
    override fun transform(benchmarkResults: List<SpmvBenchmarkResult>): PlottableData {
        if (benchmarkResults.size != 1) throw InvalidPlotTransformException("SpmvSingleScatterPlot can only be used with a single BenchmarkResult")

        val benchmarkResult = benchmarkResults[0]

        val pointList = emptyList<LabeledPoint>().toMutableList()

        for (datapoint in benchmarkResult.datapoints) {
            if (rows > 0 && datapoint.rows != rows) continue
            if (cols > 0 && datapoint.columns != cols) continue

            for (format in datapoint.formats) {
                pointList += LabeledPoint(
                    x = datapoint.nonzeros.toDouble(),
                    y = when (yAxis) {
                        SpmvSingleScatterPlotYAxis.Bandwidth -> format.storage + (datapoint.rows + datapoint.columns) / format.time
                        SpmvSingleScatterPlotYAxis.Time -> format.time
                    },
                    label = format.name,
                )
            }
        }

        return PointList(pointList)
    }
}