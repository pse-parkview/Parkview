package com.parkview.parkview.processing

import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.processing.transforms.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.SpmvSpeedupPlot
import org.junit.jupiter.api.Test

internal class AvailablePlotsTest {
    @Test
    fun `test getAvailablePlots for subset of plots`() {
        var plots = AvailablePlots.getPlotList(BenchmarkType.Spmv, 1)
        assert(
            plots.scatter.map { it.plotName }.contains(SpmvSingleScatterPlot().name)
                and plots.scatter
                .find { it.plotName == SpmvSingleScatterPlot().name }!!.options
                .find { it.name == "yAxis" }!!.options.contains("time")
        )

        plots = AvailablePlots.getPlotList(BenchmarkType.Spmv, 2)
        assert(plots.line.find { it.plotName == SpmvSpeedupPlot().name } != null)
    }
}