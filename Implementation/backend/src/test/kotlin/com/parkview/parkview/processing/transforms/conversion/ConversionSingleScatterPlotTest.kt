package com.parkview.parkview.processing.transforms.conversion

import CONVERSION_RESULT
import com.parkview.parkview.processing.transforms.PointDataset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ConversionSingleScatterPlotTest {
    private val plot = ConversionSingleScatterPlot()

    @Test
    fun transformConversion() {
        val benchmark = CONVERSION_RESULT

        val options = mutableMapOf(
            "xAxis" to "rows",
            "yAxis" to "time",
        )

        val data = plot.transformConversion(listOf(benchmark), options)

        for (dataset in data.datasets) {
            for (point in (dataset as PointDataset).data) {
                assertEquals(1.0, point.y, 0.0001)
            }
        }
    }
}
