package com.parkview.parkview.processing.transforms.spmv

import SPMV_RESULT
import com.parkview.parkview.processing.transforms.PointDataset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SpmvSpeedupPlotTest {
    private val plot = SpmvSpeedupPlot()

    private val benchmark = SPMV_RESULT

    @Test
    fun transformSpmv() {
        val options = mutableMapOf(
            "compare" to "${benchmark.identifier}/${benchmark.identifier}",
            "xAxis" to "rows",
        )
        val data = plot.transformSpmv(listOf(benchmark, benchmark), options)

        for (dataset in data.datasets) {
            for (point in (dataset as PointDataset).data) {
                assertEquals(1.0, point.y, 0.0001)
            }
        }
    }
}
