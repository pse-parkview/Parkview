package com.parkview.parkview.processing

import com.parkview.parkview.git.BenchmarkType
import org.junit.jupiter.api.Test

internal class AvailablePlotsTest {
    @Test
    fun `test getAvailablePlots for subset of plots`() {
        var plots = AvailablePlots.getPlotList(BenchmarkType.Spmv, 1)
        assert(plots.scatter.map { it.name }.contains("spmvTime") and plots.scatter.map { it.name }.contains("spmvBandwidth"))
        plots = AvailablePlots.getPlotList(BenchmarkType.Spmv, 2)
        assert(plots.line.map { it.name }.contains("spmvSpeedup"))
    }
}