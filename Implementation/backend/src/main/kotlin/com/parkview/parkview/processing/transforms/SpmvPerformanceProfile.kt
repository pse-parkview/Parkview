package com.parkview.parkview.processing.transforms

import com.parkview.parkview.benchmark.SpmvBenchmarkResult
import com.parkview.parkview.processing.*


class SpmvPerformanceProfile : SpmvPlotTransform {
    override val numInputsRange = 2..2
    override val plottableAs = listOf(PlotType.Line)
    override val name = "spmvPerformanceProfile"
    override val availableOptions: List<PlotOption> = listOf(
        PlotOption(
            name = "xAxis",
            options = listOf("speadup")
        )
    )

    override fun transformSpmv(
        benchmarkResult: SpmvBenchmarkResult,
        options: Map<String, String>
    ): PlottableData {
        val seriesByName: MutableMap<String, MutableList<PlotPoint>> = mutableMapOf()

        var minTime: Double = -1.0
        var minName: String = "NA"

        for (format in datapoint.formats) {
            if(!format.completed) continue
            if(format.time < minTime ||minTime < 0) {
                minTime = format.minTime
                minName = format.name
            }
        }

        for (format in datapoint.formats) {
            if(!format.completed) continue
            var index: Int = 0
            seriesByName.getOrPut(format.name) { mutableListOf() } += PlotPoint(
                x = format.time/minTime,
                y = index
            )
            
            }
        }

        return DatasetSeries(
            seriesByName.map { (key, value) -> Dataset(label = key, data = value) }
        )


    }

}