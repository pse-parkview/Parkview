package com.parkview.parkview.processing.transforms.spmv

import SPMV_RESULT
import com.parkview.parkview.processing.transforms.PointDataset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SpmvSingleScatterPlotTest {
    private val plot = SpmvSingleScatterPlot()

    @Test
    fun transformSpmv() {
        val benchmark = SPMV_RESULT

        val options = mutableMapOf(
            "xAxis" to "rows",
            "yAxis" to "time",
        )

        val data = plot.transformSpmv(listOf(benchmark), options)

        for (dataset in data.datasets) {
            for (point in (dataset as PointDataset).data) {
                assertEquals(1.0, point.y, 0.0001)
            }
        }
    }
}
