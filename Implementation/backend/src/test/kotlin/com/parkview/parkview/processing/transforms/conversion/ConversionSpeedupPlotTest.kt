package com.parkview.parkview.processing.transforms.conversion

import CONVERSION_RESULT
import com.parkview.parkview.processing.transforms.PointDataset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ConversionSpeedupPlotTest {
    private val plot = ConversionSpeedupPlot()

    private val benchmark = CONVERSION_RESULT

    @Test
    fun transformConversion() {
        val options = mutableMapOf(
            "compare" to "${benchmark.identifier}/${benchmark.identifier}",
            "xAxis" to "rows",
        )
        val data = plot.transformConversion(listOf(benchmark, benchmark), options)

        for (dataset in data.datasets) {
            for (point in (dataset as PointDataset).data) {
                assertEquals(1.0, point.y, 0.0001)
            }
        }
    }
}
