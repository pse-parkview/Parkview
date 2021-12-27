package com.parkview.parkview.processing

import SPMV_RESULT
import com.parkview.parkview.git.BenchmarkType
import com.parkview.parkview.processing.transforms.matrix.spmv.SpmvSingleScatterPlot
import com.parkview.parkview.processing.transforms.matrix.spmv.SpmvSpeedupPlot
import kotlin.test.assertTrue
import kotlin.test.Test

internal class AvailablePlotsTest {
    @Test
    fun test_getAvailablePlots_for_subset_of_plots() {
        var plots = AvailablePlots.getPlotList(BenchmarkType.Spmv, listOf(SPMV_RESULT))
        assertTrue(
            plots.map { it.plotName }.contains(SpmvSingleScatterPlot().name)
                and plots.find { it.plotName == SpmvSingleScatterPlot().name }!!.options
                    .find { it.name == "yAxis" }!!.options.contains("time")
        )

        plots = AvailablePlots.getPlotList(BenchmarkType.Spmv, listOf(SPMV_RESULT, SPMV_RESULT))
        assertTrue(plots.find { it.plotName == SpmvSpeedupPlot().name } != null)
    }
}
