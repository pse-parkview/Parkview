package com.parkview.parkview.processing

import SPMV_RESULT
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.processing.transforms.spmv.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.spmv.SpmvSpeedupPlot
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class AvailablePlotsTest {
    @Test
    fun `test getAvailablePlots for subset of plots`() {
        var plots = AvailablePlots.getPlotList(BenchmarkType.Spmv, listOf(SPMV_RESULT))
        assert(
            plots.map { it.name }.contains(SpmvSingleScatterPlot().name)
                and plots.find { it.name == SpmvSingleScatterPlot().name }!!.options
                    .find { it.name == "yAxis" }!!.options.contains("time")
        )

        plots = AvailablePlots.getPlotList(BenchmarkType.Spmv, listOf(SPMV_RESULT, SPMV_RESULT))
        assertTrue(plots.find { it.name == SpmvSpeedupPlot().name } != null)
    }
}
